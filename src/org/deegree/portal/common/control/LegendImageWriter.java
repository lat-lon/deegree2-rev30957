package org.deegree.portal.common.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
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
        Graphics g = createGraphics( legendMetadata.getLegendBgColor(), bi );
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
            Graphics g = createGraphics( legendMetadata.getLegendBgColor(), bi );
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

        int spacing = legendMetadata.getSpacing();

        int height = legendMetadata.getHeight();
        int startHeight = 0;
        int currentHeight = startHeight;

        int currentColumn = 0;
        int columns = legendMetadata.getColumns();
        float columnWidth = calculateColumnWidth( legendMetadata );

        List<LegendImage> currentPageLegends = new ArrayList<LegendImage>();
        for ( LegendImage singleLegendImage : createLegendImages( legendMetadata, legends ) ) {
            int childWidth = singleLegendImage.getOriginalWidth();
            int childHeight = singleLegendImage.getOriginalHeight();

            float scaleFactor = calculateScaleFactor( height, childHeight, columnWidth, childWidth );
            if ( scaleFactor < 1 ) {
                setNewBounds( singleLegendImage, currentColumn, currentHeight, childWidth, childHeight, columnWidth,
                              spacing, scaleFactor );
                childWidth = singleLegendImage.width;
                childHeight = singleLegendImage.height;
            }

            if ( doesNotFitInColumn( height, currentHeight, childHeight ) ) {
                LOG.logInfo( "Does not fit." );
                currentHeight = startHeight;
                currentColumn++;
                if ( currentColumn >= columns ) {
                    LOG.logInfo( "Open new page." );
                    LOG.logInfo( "New legend page with " + currentPageLegends.size() + " legends." );
                    allPageChilds.add( currentPageLegends );
                    currentPageLegends = new ArrayList<LegendImage>();
                    currentColumn = 0;
                }
                setNewBounds( singleLegendImage, currentColumn, currentHeight, childWidth, childHeight, columnWidth,
                              spacing );
            } else {
                LOG.logInfo( "Fits in current column." );
                setNewBounds( singleLegendImage, currentColumn, currentHeight, childWidth, childHeight, columnWidth,
                              spacing );
            }
            currentHeight += childHeight;
            currentPageLegends.add( singleLegendImage );
        }
        LOG.logInfo( "New legend page with " + currentPageLegends.size() + " legends." );
        allPageChilds.add( currentPageLegends );
        LOG.logInfo( "Legends fits on " + allPageChilds.size() + " pages." );
        return allPageChilds;
    }

    private Graphics2D createGraphics( String legendBgColor, BufferedImage bi ) {
        if ( legendBgColor == null ) {
            legendBgColor = "0xFFFFFF";
        }
        Color bg = Color.decode( legendBgColor );
        Graphics2D g = bi.createGraphics();
        g.setColor( bg );
        g.fillRect( 0, 0, bi.getWidth(), bi.getHeight() );
        g.setColor( Color.BLACK );
        return g;
    }

    private List<LegendImage> createLegendImages( LegendMetadata legendMetadata, List<String[]> legends )
                            throws IOException {
        List<LegendImage> legendImages = new ArrayList<LegendImageWriter.LegendImage>();
        LOG.logInfo( "Legends: " );
        for ( String[] legend : legends ) {
            LOG.logInfo( "  " + legend[0] + ": " + legend[1] );
            LegendImage legendImage = createLegendImageWithExtent( legendMetadata, legend );
            if ( legendImage != null )
                legendImages.add( legendImage );
        }
        return legendImages;
    }

    private LegendImage createLegendImageWithExtent( LegendMetadata legendMetadata, String[] legend )
                            throws IOException {
        float columnWidth = calculateColumnWidth( legendMetadata );
        BufferedImage legendImage = createLegendImage( legend, legendMetadata, (int) columnWidth );
        if ( legendImage != null ) {
            if ( !legendFitsInColumn( legendMetadata, legendImage, columnWidth ) )
                legendImage = createLegendImageDoesNotFit( legendMetadata, legend[0] );
            return new LegendImage( legend[0], legendImage );
        }
        return null;
    }

    private BufferedImage createLegendImageDoesNotFit( LegendMetadata legendMetadata, String name ) {
        LOG.logInfo( "Legend image for " + name + " does not fit." );
        String msg = String.format( legendMetadata.getToLargeMsg(), name );
        int spacing = legendMetadata.getSpacing();

        int width = (int) calculateColumnWidth( legendMetadata );
        int height = calculateHeight( msg, width - spacing ) + 2 * spacing;
        BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = createGraphics( legendMetadata.getLegendBgColor(), bi );

        AttributedString attributedString = createAttributedString( msg );

        AttributedCharacterIterator paragraph = attributedString.getIterator();
        int paragraphStart = paragraph.getBeginIndex();
        int paragraphEnd = paragraph.getEndIndex();
        FontRenderContext frc = g.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer( paragraph, frc );

        float breakWidth = width - spacing;
        float drawPosY = spacing;
        lineMeasurer.setPosition( paragraphStart );

        while ( lineMeasurer.getPosition() < paragraphEnd ) {
            TextLayout layout = lineMeasurer.nextLayout( breakWidth );
            float drawPosX = layout.isLeftToRight() ? 0 : breakWidth - layout.getAdvance();
            drawPosY += layout.getAscent();
            layout.draw( g, drawPosX, drawPosY );
            drawPosY += layout.getDescent() + layout.getLeading();
        }
        g.dispose();
        return bi;
    }

    private int calculateHeight( String msg, int width ) {
        BufferedImage bi = new BufferedImage( width, 100, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = bi.createGraphics();
        AttributedString attributedString = createAttributedString( msg );

        AttributedCharacterIterator paragraph = attributedString.getIterator();
        int paragraphStart = paragraph.getBeginIndex();
        int paragraphEnd = paragraph.getEndIndex();
        FontRenderContext frc = g.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer( paragraph, frc );

        float breakWidth = width;
        float drawPosY = 0;
        lineMeasurer.setPosition( paragraphStart );

        while ( lineMeasurer.getPosition() < paragraphEnd ) {
            TextLayout layout = lineMeasurer.nextLayout( breakWidth );
            float drawPosX = layout.isLeftToRight() ? 0 : breakWidth - layout.getAdvance();
            drawPosY += layout.getAscent();
            layout.draw( g, drawPosX, drawPosY );
            drawPosY += layout.getDescent() + layout.getLeading();
        }
        return (int) drawPosY;
    }

    private AttributedString createAttributedString( String msg ) {
        Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
        map.put( TextAttribute.FAMILY, "Serif" );
        map.put( TextAttribute.SIZE, new Float( 12.0 ) );
        AttributedString attributedString = new AttributedString( msg, map );
        return attributedString;
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

    private BufferedImage createLegendImage( String[] s, LegendMetadata legendMetadata, int columnWidth )
                            throws IOException {
        try {
            return ImageUtils.loadImage( new URL( s[1] ) );
        } catch ( Exception e ) {
            LOG.logDebug( "Exception for Layer: " + s[0] + " - " + s[1] + ": " + e.getLocalizedMessage() );
            return createMissingLegendImage( s[0], legendMetadata, columnWidth );
        }
    }

    private BufferedImage createMissingLegendImage( String layerName, LegendMetadata legendMetadata, int columnWidth )
                            throws IOException {
        if ( missingLegendUrl != null ) {
            File missingImage = new File( missingLegendUrl );
            if ( missingImage.exists() ) {
                BufferedImage missingLegendImage = ImageUtils.loadImage( missingImage );
                return createMissingLegend( legendMetadata, missingLegendImage, layerName, columnWidth );
            }
        }
        return null;
    }

    private BufferedImage createMissingLegend( LegendMetadata legendMetadata, BufferedImage missingImg,
                                               String layerName, int columnWidth ) {
        int spacing = legendMetadata.getSpacing();
        int calculatedHeight = calculateHeight( layerName, columnWidth );

        int height = missingImg.getHeight() + spacing + calculatedHeight + spacing;
        BufferedImage missingLegend = new BufferedImage( columnWidth, height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = createGraphics( legendMetadata.getLegendBgColor(), missingLegend );
        g.drawImage( missingImg, 0, 0, missingImg.getWidth(), missingImg.getHeight(), null );
        g.setColor( Color.RED );

        AttributedString attributedString = createAttributedString( layerName );

        AttributedCharacterIterator paragraph = attributedString.getIterator();
        int paragraphStart = paragraph.getBeginIndex();
        int paragraphEnd = paragraph.getEndIndex();
        FontRenderContext frc = g.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer( paragraph, frc );

        float breakWidth = columnWidth - spacing;
        float drawPosY = missingImg.getHeight() + spacing;
        lineMeasurer.setPosition( paragraphStart );

        while ( lineMeasurer.getPosition() < paragraphEnd ) {
            TextLayout layout = lineMeasurer.nextLayout( breakWidth );
            float drawPosX = layout.isLeftToRight() ? 0 : breakWidth - layout.getAdvance();
            drawPosY += layout.getAscent();
            layout.draw( g, drawPosX, drawPosY );
            drawPosY += layout.getDescent() + layout.getLeading();
        }
        g.dispose();

        try {
            ImageIO.write( missingLegend, "png", File.createTempFile( "missingLegend_", ".png" ) );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return missingLegend;
    }

    boolean legendFitsInColumn( LegendMetadata legendMetadata, BufferedImage legendImage, float columnWidth ) {
        int maxSizeToFitInPercent = legendMetadata.getMaxSizeToFitInPercent();

        double requiredWidthScaleInPercent = columnWidth / legendImage.getWidth() * 100;
        if ( requiredWidthScaleInPercent < maxSizeToFitInPercent )
            return false;

        int height = legendMetadata.getHeight();
        double requiredHeightScaleInPercent = (double) height / legendImage.getHeight() * 100;
        if ( requiredHeightScaleInPercent < maxSizeToFitInPercent )
            return false;

        return true;
    }

    private void setNewBounds( LegendImage child, int currentColumn, int currentHeight, int childWidth,
                               int childHeight, float columnWidth, int spacing, float scale ) {
        double columnWidthWithSpacing = columnWidth + spacing;
        int currentWidth = (int) ( currentColumn * columnWidthWithSpacing );
        int llx = currentWidth;
        int urx = currentWidth + ( (Float) ( childWidth * scale ) ).intValue();
        int lly = currentHeight;
        int ury = currentHeight + ( (Float) ( childHeight * scale ) ).intValue();
        child.x = llx;
        child.y = lly;
        child.width = urx - llx;
        child.height = ury - lly;
    }

    private void setNewBounds( LegendImage child, int currentColumn, int currentHeight, int childWidth,
                               int childHeight, float columnWidth, int spacing ) {
        setNewBounds( child, currentColumn, currentHeight, childWidth, childHeight, columnWidth, spacing, 1 );
    }

    private boolean doesNotFitInColumn( int totalHeight, int currentHeight, int childHeight ) {
        return totalHeight < childHeight + currentHeight;
    }

    private float calculateScaleFactor( float height, float childHeight, float width, float childWidth ) {
        float scaleFactorInHeight = height / childHeight;
        float scaleFactorInWidth = width / childWidth;

        float scaleFactor = Math.min( scaleFactorInHeight, scaleFactorInWidth );
        if ( scaleFactor > 1 )
            scaleFactor = 1;
        return scaleFactor;
    }

    private float calculateColumnWidth( LegendMetadata legendMetadata ) {
        int width = legendMetadata.getWidth();
        int spacing = legendMetadata.getSpacing();
        int columns = legendMetadata.getColumns();
        int noOfSpaces = columns - 1;
        float widthWithoutSpacing = width - ( noOfSpaces * spacing );
        return widthWithoutSpacing / columns;
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