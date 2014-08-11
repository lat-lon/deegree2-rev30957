package org.deegree.portal.common.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.StringTools;

public class LegendImageWriter {

    private static ILogger LOG = LoggerFactory.getLogger( LegendImageWriter.class );

    private final String missingLegendUrl;

    private final String tempDir;

    public LegendImageWriter( String missingLegendUrl, String tempDir ) {
        this.missingLegendUrl = missingLegendUrl;
        this.tempDir = tempDir;
    }

    /**
     * accesses the legend URLs passed, draws the result onto an image that are stored in a temporary file. The name of
     * the file will be returned.
     * 
     * @param legendMetadata
     * 
     * @param legends
     * @return filename of image file
     */
    public Map<String, String> accessLegend( ServletContext sc, LegendMetadata legendMetadata, List<String[]> legends )
                            throws IOException {
        if ( legendMetadata.isDynamicLegend() )
            return createMultipleLegends( sc, legendMetadata, legends );
        else
            return createSingleLegendPage( sc, legendMetadata, legends );

    }

    private Map<String, String> createSingleLegendPage( ServletContext sc, LegendMetadata legendMetadata,
                                                        List<String[]> legends )
                            throws IOException {
        int height = legendMetadata.getHeight();
        int width = legendMetadata.getWidth();
        BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        Graphics g = createGraphics( legendMetadata, bi );
        int k = 10;

        for ( int i = 0; i < legends.size(); i++ ) {
            if ( k > bi.getHeight() ) {
                if ( LOG.getLevel() <= ILogger.LOG_WARNING ) {
                    LOG.logWarning( "The necessary legend size is larger than the available legend space." );
                }
            }
            String[] s = legends.get( i );
            if ( s[1] != null ) {
                LOG.logDebug( "reading legend: " + s[1] );
                Image img = createLegendImage( s );
                if ( img != null ) {
                    if ( img.getWidth( null ) < 50 ) {
                        // it is assumed that no label is assigned
                        g.drawImage( img, 0, k, null );
                        g.drawString( s[0], img.getWidth( null ) + 10, k + img.getHeight( null ) / 2 );
                    } else {
                        g.drawImage( img, 0, k, null );
                    }
                    k = k + img.getHeight( null ) + 10;
                }
            } else {
                g.drawString( "- " + s[0], 0, k + 10 );
                k = k + 20;
            }
        }
        g.dispose();
        Map<String, String> parameterName2LegendUrl = new HashMap<String, String>();
        parameterName2LegendUrl.put( "LEGEND", storeImage( sc, tempDir, bi ) );
        return parameterName2LegendUrl;
    }

    private Map<String, String> createMultipleLegends( ServletContext sc, LegendMetadata legendMetadata,
                                                       List<String[]> legends )
                            throws IOException {
        Map<String, String> parameterName2LegendUrl = new HashMap<String, String>();
        List<List<LegendImage>> legendPages = createLegendPages( legendMetadata, legends );
        LOG.logInfo( "Number of legend pages: " + legendPages.size() );
        int index = 0;
        for ( List<LegendImage> legendPage : legendPages ) {
            LOG.logInfo( "Draw page " + legendMetadata.getWidth() + "/" + legendMetadata.getHeight() );
            BufferedImage bi = new BufferedImage( legendMetadata.getWidth(), legendMetadata.getHeight(),
                                                  BufferedImage.TYPE_INT_ARGB );
            Graphics g = createGraphics( legendMetadata, bi );
            for ( LegendImage legendImage : legendPage ) {
                LOG.logInfo( "Draw image " + legendImage.getX() + "/" + legendImage.y );
                g.drawImage( legendImage.legendImage, legendImage.getX(), legendImage.y, legendImage.width,
                             legendImage.height, null );
            }
            g.dispose();
            parameterName2LegendUrl.put( "LEGEND" + index++, storeImage( sc, tempDir, bi ) );
        }
        return parameterName2LegendUrl;
    }

    List<List<LegendImage>> createLegendPages( LegendMetadata legendMetadata, List<String[]> legends )
                            throws IOException {
        List<List<LegendImage>> allPageChilds = new ArrayList<List<LegendImage>>();

        int width = legendMetadata.getWidth();
        int height = legendMetadata.getHeight();

        int startHeight = 0;
        int availableHeight = height;

        int startWidth = 0;
        int currentWidth = startWidth;
        int currentHeight = startHeight;
        int currentColumnWidth = 0;

        List<LegendImage> currentPageLegends = new ArrayList<LegendImage>();
        for ( LegendImage singleLegendImage : createLegendImagesSortByHeight( legends ) ) {
            int childWidth = singleLegendImage.getOriginalWidth();
            int childHeight = singleLegendImage.getOriginalHeight();

            if ( doesNotFitInColumn( height, currentHeight, childHeight ) ) {
                float scaleFactor = calculateScaleFactor( height, childHeight );
                setNewBounds( singleLegendImage, currentWidth, currentHeight, childWidth, childHeight, scaleFactor );
                childWidth = singleLegendImage.width;
                childHeight = singleLegendImage.height;
            }

            if ( isFirstInColumnAndDoesNotFitInColumn( height, startHeight, currentHeight, childHeight ) ) {
                LOG.logInfo( "First in column and does not fit." );
                currentHeight = startHeight;
                float scaleFactor = calculateScaleFactor( availableHeight, childHeight );
                if ( maxPageWidthIsAchieved( width, currentWidth, childWidth * scaleFactor ) ) {
                    LOG.logInfo( "Open new page." );
                    currentWidth = startWidth;
                    allPageChilds.add( currentPageLegends );
                    currentPageLegends = new ArrayList<LegendImage>();
                }
                setNewBounds( singleLegendImage, currentWidth, currentHeight, childWidth, childHeight, scaleFactor );
                currentWidth += ( childWidth * scaleFactor );
                currentColumnWidth = 0;
            } else if ( doesNotFitInColumn( height, currentHeight, childHeight ) ) {
                LOG.logInfo( "Does not fit." );
                currentWidth += currentColumnWidth;
                currentHeight = startHeight;
                if ( maxPageWidthIsAchieved( width, currentWidth, childWidth ) ) {
                    LOG.logInfo( "Open new page." );
                    currentWidth = startWidth;
                    allPageChilds.add( currentPageLegends );
                    currentPageLegends = new ArrayList<LegendImage>();
                }
                currentColumnWidth = childWidth;
                setNewBounds( singleLegendImage, currentWidth, currentHeight, childWidth, childHeight );
            } else {
                LOG.logInfo( "Fits in current column." );
                currentColumnWidth = Math.max( currentColumnWidth, childWidth );
                setNewBounds( singleLegendImage, currentWidth, currentHeight, childWidth, childHeight );
            }
            currentHeight += childHeight;
            currentPageLegends.add( singleLegendImage );
        }
        LOG.logInfo( "New legend page with " + currentPageLegends.size() + " legends." );
        allPageChilds.add( currentPageLegends );
        return allPageChilds;
    }

    private Graphics createGraphics( LegendMetadata legendMetadata, BufferedImage bi ) {
        String legendBgColor = legendMetadata.getLegendBgColor();
        if ( legendBgColor == null ) {
            legendBgColor = "0xFFFFFF";
        }
        Color bg = Color.decode( legendBgColor );
        Graphics g = bi.getGraphics();
        g.setColor( bg );
        g.fillRect( 0, 0, bi.getWidth(), bi.getHeight() );
        g.setColor( Color.BLACK );
        return g;
    }

    private List<LegendImage> createLegendImagesSortByHeight( List<String[]> legends )
                            throws IOException {
        List<LegendImage> legendImages = new ArrayList<LegendImageWriter.LegendImage>();
        for ( String[] legend : legends ) {
            LegendImage legendImage = createLegendImageWithExtent( legend );
            if ( legendImage != null )
                legendImages.add( legendImage );
        }
        Collections.sort( legendImages, new Comparator<LegendImage>() {
            public int compare( LegendImage entry1, LegendImage entry2 ) {
                return Float.compare( entry2.getOriginalHeight(), entry1.getOriginalHeight() );
            }
        } );
        return legendImages;
    }

    private LegendImage createLegendImageWithExtent( String[] legend )
                            throws IOException {
        LOG.logInfo( "Legends: " );
        LOG.logInfo( "  " + legend[0] + ": " + legend[1] );
        BufferedImage legendImage = createLegendImage( legend );
        if ( legendImage != null )
            return new LegendImage( legend[0], legendImage );
        return null;
    }

    private BufferedImage createLegendImage( String[] s )
                            throws IOException {
        try {
            return ImageUtils.loadImage( new URL( s[1] ) );
        } catch ( Exception e ) {
            LOG.logDebug( "Exception for Layer: " + s[0] + " - " + s[1] + ": " + e.getLocalizedMessage() );
            if ( missingLegendUrl != null ) {
                File missingImage = new File( missingLegendUrl );
                if ( missingImage.exists() ) {
                    return ImageUtils.loadImage( missingImage );
                }
            }
        }
        return null;
    }

    private void setNewBounds( LegendImage child, int currentWidth, int currentHeight, int childWidth, int childHeight,
                               float scale ) {
        int llx = currentWidth;
        int urx = currentWidth + ( (Float) ( childWidth * scale ) ).intValue();
        int lly = currentHeight;
        int ury = currentHeight + ( (Float) ( childHeight * scale ) ).intValue();
        child.x = llx;
        child.y = lly;
        child.width = urx - llx;
        child.height = ury - lly;
    }

    private void setNewBounds( LegendImage child, int currentWidth, int currentHeight, int childWidth, int childHeight ) {
        int llx = currentWidth;
        int urx = currentWidth + childWidth;
        int lly = currentHeight;
        int ury = currentHeight + childHeight;
        child.x = llx;
        child.y = lly;
        child.width = urx - llx;
        child.height = ury - lly;
    }

    private boolean isFirstInColumnAndDoesNotFitInColumn( int totalHeight, int startHeight, int currentHeight,
                                                          int childHeight ) {
        return currentHeight == startHeight && doesNotFitInColumn( totalHeight, currentHeight, childHeight );
    }

    private boolean doesNotFitInColumn( int totalHeight, int currentHeight, int childHeight ) {
        return totalHeight < childHeight + currentHeight;
    }

    private boolean maxPageWidthIsAchieved( float width, float currentWidth, float childWidth ) {
        return currentWidth + childWidth > width;
    }

    private float calculateScaleFactor( float currentHeight, float childHeight ) {
        float scaleInPercent = currentHeight / childHeight;
        if ( scaleInPercent > 1 )
            scaleInPercent = 1;
        return scaleInPercent;
    }

    /**
     * stores the passed image in the defined temporary directory and returns the dynamicly created filename
     * 
     * @param bi
     * @return filename of image file
     * @throws IOException
     */
    public static String storeImage( ServletContext sc, String tempDir, BufferedImage bi )
                            throws IOException {

        String s = UUID.randomUUID().toString();
        if ( !tempDir.endsWith( "/" ) ) {
            tempDir = tempDir + '/';
        }
        if ( tempDir.startsWith( "/" ) ) {
            tempDir = tempDir.substring( 1, tempDir.length() );
        }
        String fileName = StringTools.concat( 300, sc.getRealPath( tempDir ), '/', s, ".png" );

        FileOutputStream fos = new FileOutputStream( new File( fileName ) );

        ImageUtils.saveImage( bi, fos, "png", 1 );
        fos.close();

        return fileName;
    }

    public class LegendImage {

        private BufferedImage legendImage;

        private int x;

        private int y;

        private int width;

        private int height;

        private String name;

        private LegendImage( String name, BufferedImage legendImage ) {
            this.name = name;
            this.legendImage = legendImage;
        }

        public int getOriginalHeight() {
            return legendImage.getHeight( null );
        }

        public int getOriginalWidth() {
            return legendImage.getWidth( null );
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public String getName() {
            return name;
        }
    }

}