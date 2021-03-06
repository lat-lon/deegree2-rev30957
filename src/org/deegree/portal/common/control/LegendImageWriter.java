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
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.Pair;
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
    public List<List<PreparedLegendInfo>> accessLegend( ServletContext sc, LegendMetadata legendMetadata,
                                                        List<Pair<String, URL>> legends )
                            throws IOException {
        if ( legendMetadata.isDynamicLegend() )
            return createMultipleLegends( sc, legendMetadata, legends );
        else
            return createSingleLegendPage( sc, legendMetadata, legends );

    }

    private List<List<PreparedLegendInfo>> createSingleLegendPage( ServletContext sc, LegendMetadata legendMetadata,
                                                                   List<Pair<String, URL>> legends )
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
            Pair<String, URL> s = legends.get( i );
            URL url = s.second;
            String str = s.first;
            if ( url != null ) {
                LOG.logDebug( "reading legend: " + url );
                Image img = createLegendImage( s );
                if ( img != null ) {
                    if ( img.getWidth( null ) < 50 ) {
                        // it is assumed that no label is assigned
                        g.drawImage( img, 0, k, null );
                        g.drawString( str, img.getWidth( null ) + 10, k + img.getHeight( null ) / 2 );
                    } else {
                        g.drawImage( img, 0, k, null );
                    }
                    k = k + img.getHeight( null ) + 10;
                }
            } else {
                g.drawString( "- " + str, 0, k + 10 );
                k = k + 20;
            }
        }
        g.dispose();
        List<PreparedLegendInfo> preparedLegendInfos = new ArrayList<PreparedLegendInfo>();
        preparedLegendInfos.add( new PreparedLegendInfo( "LEGEND", storeImage( sc, tempDir, bi ) ) );
        return Collections.singletonList( preparedLegendInfos );
    }

    private List<List<PreparedLegendInfo>> createMultipleLegends( ServletContext sc, LegendMetadata legendMetadata,
                                                                  List<Pair<String, URL>> legends )
                            throws IOException {
        List<List<PreparedLegendInfo>> preparedLegendInfoPages = new ArrayList<List<PreparedLegendInfo>>();
        List<List<LegendImage>> legendPages = createLegendPages( legendMetadata, legends );
        LOG.logInfo( "Number of legend pages: " + legendPages.size() );
        int index = 0;
        for ( List<LegendImage> legendPage : legendPages ) {
            List<PreparedLegendInfo> legendInfos = new ArrayList<PreparedLegendInfo>();
            for ( LegendImage legendImage : legendPage ) {
                BufferedImage img = createImage( legendMetadata, legendImage );
                String storedLegendImage = storeImage( sc, tempDir, img );
                String legendId = "LEGEND" + index++;
                legendInfos.add( new PreparedLegendInfo( legendId, storedLegendImage, legendImage.getX(),
                                                         legendImage.getY(), legendImage.getWidth(),
                                                         legendImage.getHeight() ) );
            }
            preparedLegendInfoPages.add( legendInfos );
        }
        return preparedLegendInfoPages;
    }

    private BufferedImage createImage( LegendMetadata legendMetadata, LegendImage legendImage ) {
        BufferedImage bi = new BufferedImage( legendImage.getOriginalWidth(), legendImage.getOriginalHeight(),
                                              BufferedImage.TYPE_INT_ARGB );
        Graphics g = createGraphics( legendMetadata.getLegendBgColor(), bi );
        g.drawImage( legendImage.legendImage, 0, 0, null );
        g.dispose();
        return bi;
    }

    List<List<LegendImage>> createLegendPages( LegendMetadata legendMetadata, List<Pair<String, URL>> legends )
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
            if ( currentHeight != startHeight )
                currentHeight += spacing;
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

    private List<LegendImage> createLegendImages( LegendMetadata legendMetadata, List<Pair<String, URL>> legends )
                            throws IOException {
        List<LegendImage> legendImages = new ArrayList<LegendImageWriter.LegendImage>();
        LOG.logInfo( "Legends: " );
        for ( Pair<String, URL> legend : legends ) {
            LOG.logInfo( "  " + legend.first + ": " + legend.second );
            LegendImage legendImage = createLegendImageWithExtent( legendMetadata, legend );
            if ( legendImage != null )
                legendImages.add( legendImage );
        }
        return legendImages;
    }

    private LegendImage createLegendImageWithExtent( LegendMetadata legendMetadata, Pair<String, URL> legend )
                            throws IOException {
        float columnWidth = calculateColumnWidth( legendMetadata );
        BufferedImage legendBufferedImage = createLegendImage( legend, legendMetadata, (int) columnWidth );
        if ( legendBufferedImage != null ) {
            if ( !legendFitsInColumn( legendMetadata, legendBufferedImage, columnWidth ) )
                legendBufferedImage = createLegendImageDoesNotFit( legendMetadata, legend.first );
            LegendImage legendImage = new LegendImage( legend.first, legendBufferedImage );
            legendImage.width = legendBufferedImage.getWidth() / 2;
            legendImage.height = legendBufferedImage.getHeight() / 2;
            return legendImage;
        }
        return null;
    }

    private BufferedImage createLegendImageDoesNotFit( LegendMetadata legendMetadata, String name ) {
        LOG.logInfo( "Legend image for " + name + " does not fit." );
        String msg = String.format( legendMetadata.getToLargeMsg(), name );
        int spacing = legendMetadata.getSpacing();

        int width = 2 * ( (int) calculateColumnWidth( legendMetadata ) );
        int height = 2 * ( calculateHeight( legendMetadata.getFontFamily(), legendMetadata.getFontSize() * 2, msg,
                                            width - spacing ) + 2 * spacing );
        BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = createGraphics( legendMetadata.getLegendBgColor(), bi );

        AttributedString attributedString = createAttributedString( legendMetadata.getFontFamily(),
                                                                    legendMetadata.getFontSize() * 2, msg );

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

    private int calculateHeight( String fontFamily, float fontSize, String msg, int width ) {
        BufferedImage bi = new BufferedImage( width, 100, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = bi.createGraphics();
        AttributedString attributedString = createAttributedString( fontFamily, fontSize, msg );

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

    private AttributedString createAttributedString( String fontFamily, float fontSize, String msg ) {
        Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
        map.put( TextAttribute.FAMILY, fontFamily );
        map.put( TextAttribute.SIZE, fontSize );
        AttributedString attributedString = new AttributedString( msg, map );
        return attributedString;
    }

    private BufferedImage createLegendImage( Pair<String, URL> s )
                            throws IOException {
        URL url = s.second;
        try {
            return ImageUtils.loadImage( url );
        } catch ( Exception e ) {
            LOG.logDebug( "Exception for Layer: " + s.first + " - " + url + ": " + e.getLocalizedMessage() );
            if ( missingLegendUrl != null ) {
                File missingImage = new File( missingLegendUrl );
                if ( missingImage.exists() ) {
                    return ImageUtils.loadImage( missingImage );
                }
            }
        }
        return null;
    }

    private BufferedImage createLegendImage( Pair<String, URL> legend, LegendMetadata legendMetadata, int columnWidth )
                            throws IOException {
        URL url = legend.second;
        try {
            return ImageUtils.loadImage( url );
        } catch ( Exception e ) {
            String name = legend.first;
            LOG.logDebug( "Exception for Layer: " + name + " - " + url + ": " + e.getLocalizedMessage() );
            return createMissingLegendImage( name, legendMetadata, columnWidth );
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
        int calculatedHeight = calculateHeight( legendMetadata.getFontFamily(), legendMetadata.getFontSize(),
                                                layerName, columnWidth );

        int spacingBetweenImgAndText = 3;
        int height = spacing + missingImg.getHeight() + spacingBetweenImgAndText + calculatedHeight + spacing;
        BufferedImage missingLegend = new BufferedImage( columnWidth, height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = createGraphics( legendMetadata.getLegendBgColor(), missingLegend );
        g.drawImage( missingImg, 0, spacing, missingImg.getWidth(), missingImg.getHeight(), null );
        g.setColor( Color.RED );

        AttributedString attributedString = createAttributedString( legendMetadata.getFontFamily(),
                                                                    legendMetadata.getFontSize(), layerName );

        AttributedCharacterIterator paragraph = attributedString.getIterator();
        int paragraphStart = paragraph.getBeginIndex();
        int paragraphEnd = paragraph.getEndIndex();
        FontRenderContext frc = g.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer( paragraph, frc );

        float breakWidth = columnWidth - spacing;
        float drawPosY = spacing + missingImg.getHeight() + spacingBetweenImgAndText;
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

        public BufferedImage getLegendImage() {
            return legendImage;
        }

    }

}