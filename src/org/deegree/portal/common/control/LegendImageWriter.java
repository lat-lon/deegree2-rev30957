package org.deegree.portal.common.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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
        int height = legendMetadata.getLegendHeight();
        int width = legendMetadata.getLegendWidth();
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
                Image img = null;
                try {
                    img = ImageUtils.loadImage( new URL( s[1] ) );
                } catch ( Exception e ) {
                    if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                        String msg = StringTools.concat( 400, "Exception for Layer: ", s[0], " - ", s[1] );
                        LOG.logDebug( msg );
                        LOG.logDebug( e.getLocalizedMessage() );
                    }
                    if ( missingLegendUrl != null ) {
                        File missingImage = new File( missingLegendUrl );
                        if ( missingImage.exists() ) {
                            img = ImageUtils.loadImage( missingImage );
                        }
                    }
                }
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
}