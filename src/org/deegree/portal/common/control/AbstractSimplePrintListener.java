//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/common/control/AbstractSimplePrintListener.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.portal.common.control;

import static org.deegree.portal.common.control.LegendImageWriter.storeImage;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.MapUtils;
import org.deegree.framework.util.Pair;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.portal.PortalException;
import org.deegree.portal.PortalUtils;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.LayerGroup;
import org.deegree.portal.context.LayerList;
import org.deegree.portal.context.MMLayer;
import org.deegree.portal.context.MapModel;
import org.deegree.portal.context.MapModelEntry;
import org.deegree.portal.context.Style;
import org.deegree.portal.context.ViewContext;
import org.deegree.security.drm.model.User;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * performs a print request/event by creating a PDF document from the current map. For this JASPER is used. Well known
 * parameters that can be passed to a jaser report are:<br>
 * <ul>
 * <li>MAP</li>
 * <li>LEGEND</li>
 * <li>DATE</li>
 * <li>MAPSCALE</li>
 * </ul>
 * <br>
 * Additionaly parameters named 'TA:XXXX' can be used. deegree will create a k-v-p for each TA:XXXX passed as part of
 * RPC.
 * 
 * 
 * @version $Revision: 30945 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version $Revision: 30945 $, $Date: 2011-05-30 10:44:58 +0200 (Mo, 30. Mai 2011) $
 */
public abstract class AbstractSimplePrintListener extends AbstractListener {

    private static ILogger LOG = LoggerFactory.getLogger( AbstractSimplePrintListener.class );

    private final JrxmlManipulator jrxmlManipulator = new JrxmlManipulator();

    private String defaultTemplateDir = "/WEB-INF/igeoportal/print";

    private static float[] scaleBounds = { 100, 250, 500, 1000, 2500, 5000, 10000, 15000, 25000, 50000, 100000, 200000,
                                          250000, 500000, 1000000, 2500000, 5000000, 10000000, 50000000 };

    private static NamespaceContext nsc = CommonNamespaces.getNamespaceContext();

    static {
        nsc.addNamespace( "jasper", URI.create( "http://jasperreports.sourceforge.net/jasperreports" ) );
    }

    /**
     * @param e
     */
    public void actionPerformed( FormEvent e ) {
        String sb = getInitParameter( "ScaleBoundaries" );
        if ( sb != null ) {
            scaleBounds = StringTools.toArrayFloat( sb, "," );
        }
        RPCWebEvent rpc = (RPCWebEvent) e;
        try {
            validate( rpc );
        } catch ( Exception ex ) {
            LOG.logError( ex.getMessage(), ex );
            gotoErrorPage( ex.getMessage() );
        }

        ViewContext vc = getViewContext( rpc );
        if ( vc == null ) {
            LOG.logError( "no valid ViewContext available; maybe your session has reached timeout limit" ); //$NON-NLS-1$
            gotoErrorPage( Messages.getString( "AbstractSimplePrintListener.MISSINGCONTEXT" ) );
            setNextPage( "error.jsp" );
            return;
        }
        try {
            printMap( vc, rpc );
        } catch ( Exception ex ) {
            ex.printStackTrace();
            LOG.logError( ex.getMessage(), ex );
            gotoErrorPage( ex.getMessage() );
        }
    }

    private void printMap( ViewContext vc, RPCWebEvent rpc )
                            throws Exception {
        RPCParameter[] parameters = rpc.getRPCMethodCall().getParameters();
        RPCStruct param1 = (RPCStruct) parameters[1].getValue();
        RPCStruct param2 = (RPCStruct) parameters[2].getValue();

        String printTemplate = (String) param1.getMember( "TEMPLATE" ).getValue();

        // read print template directory from defaultTemplateDir, or, if available, from the init
        // parameters
        String templateDir = getInitParameter( "TEMPLATE_DIR" );
        if ( templateDir == null ) {
            templateDir = defaultTemplateDir;
        }
        ServletContext sc = ( (HttpServletRequest) getRequest() ).getSession( true ).getServletContext();
        String pathx = sc.getRealPath( templateDir ) + '/' + printTemplate + ".jrxml";

        PrintMetadata printMetadata = parsePrintMetadata( pathx );

        Pair<Integer, Integer> scaleBarBize = getScaleBarSize( pathx );

        Integer dpi = calculateDpi( param1 );
        int widthDpi = calculateWidth( dpi, printMetadata );
        int heightDpi = calculateHeight( dpi, printMetadata );
        Envelope bbox = calculateBbox( param2, param1, printMetadata );
        double scaleDenominator = calculateScaleDenominator( printMetadata, bbox );

        List<String> getMap = createGetMapRequests( vc, rpc, bbox, widthDpi, heightDpi );
        String image = performGetMapRequests( getMap );

        List<List<PreparedLegendInfo>> parameterName2Legends = createLegendImages( printMetadata.getLegendMetadata(),
                                                                                   vc, scaleDenominator );

        BufferedImage scaleBar = null;
        if ( scaleBarBize.first > 0 ) {
            scaleBar = createScaleBar( rpc, getMap, scaleBarBize, widthDpi, heightDpi );
        }

        String format = (String) parameters[0].getValue();

        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            LOG.logDebug( "The jrxml template is read from: ", pathx );
        }

        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put( "MAP", image );
        for ( List<PreparedLegendInfo> parameterName2Legend : parameterName2Legends ) {
            for ( PreparedLegendInfo preparedLegendInfo : parameterName2Legend ) {
                parameter.put( preparedLegendInfo.getLegendId(), preparedLegendInfo.getUrlToLegend() );
            }
        }
        parameter.put( "SCALEBAR", scaleBar );

        // enable
        if ( getInitParameter( "LOGO_URL" ) != null ) {
            String logoUrl = getHomePath() + getInitParameter( "LOGO_URL" );
            if ( new File( logoUrl ).exists() ) {
                parameter.put( "LOGO_URL", logoUrl );
            }
        }

        parameter.put( "ROOT", getHomePath() );

        SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault() );
        parameter.put( "DATE", sdf.format( new GregorianCalendar().getTime() ) );

        parameter.put( "MAPSCALE", "" + Math.round( scaleDenominator ) );
        LOG.logDebug( "print map scale: ", scaleDenominator );
        // set text area values
        RPCMember[] members = param1.getMembers();
        String charset = CharsetUtils.getSystemCharset();
        for ( int i = 0; i < members.length; i++ ) {
            if ( members[i].getName().startsWith( "TA:" ) ) {
                String s = members[i].getName().substring( 3, members[i].getName().length() );
                String val = (String) members[i].getValue();
                if ( val != null )
                    val = new String( val.getBytes( charset ), "UTF-8" );
                LOG.logDebug( "text area name: ", s );
                LOG.logDebug( "text area value: ", val );
                parameter.put( s, val );
            }
        }
        XMLFragment xmlReport = jrxmlManipulator.manipulateJrxml( printMetadata, pathx, vc, parameterName2Legends );
        JasperReport jreport = getReport( xmlReport );
        if ( "application/pdf".equals( format ) ) {
            // create the pdf
            byte[] result = null;
            try {
                JRDataSource ds = new JREmptyDataSource();
                result = JasperRunManager.runReportToPdf( jreport, parameter, ds );
            } catch ( JRException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new PortalException( Messages.getString( "AbstractSimplePrintListener.REPORTCREATION" ) );
            } finally {
                removeTmpFiles( image, parameterName2Legends );
            }
            forwardPDF( result );
        } else if ( "image/png".equals( format ) ) {
            // create the image
            Image result = null;
            try {
                JRDataSource ds = new JREmptyDataSource();
                JasperPrint prt = JasperFillManager.fillReport( jreport, parameter, ds );
                result = JasperPrintManager.printPageToImage( prt, 0, 1 );
            } catch ( JRException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new PortalException( Messages.getString( "AbstractSimplePrintListener.REPORTCREATION" ) );
            } finally {
                removeTmpFiles( image, parameterName2Legends );
            }
            forwardImage( result, format );
        }
    }

    private void removeTmpFiles( String image, List<List<PreparedLegendInfo>> parameterName2Legends ) {
        File file = new File( image );
        file.delete();
        for ( List<PreparedLegendInfo> legend : parameterName2Legends ) {
            for ( PreparedLegendInfo preparedLegendInfo : legend ) {
                file = new File( preparedLegendInfo.getUrlToLegend() );
                file.delete();
            }
        }
    }

    private Pair<Integer, Integer> getScaleBarSize( String path )
                            throws Exception {
        File file = new File( path );
        XMLFragment xml = new XMLFragment( file.toURI().toURL() );

        String xpathW = "detail/band/image/reportElement[./@key = 'scaleBar']/@width";
        String xpathH = "detail/band/image/reportElement[./@key = 'scaleBar']/@height";
        int w = XMLTools.getNodeAsInt( xml.getRootElement(), xpathW, nsc, -1 );
        int h = XMLTools.getNodeAsInt( xml.getRootElement(), xpathH, nsc, -1 );
        if ( w < 0 ) {
            xpathW = "jasper:detail/jasper:band/jasper:image/jasper:reportElement[./@key = 'scaleBar']/@width";
            xpathH = "jasper:detail/jasper:band/jasper:image/jasper:reportElement[./@key = 'scaleBar']/@height";
            w = XMLTools.getNodeAsInt( xml.getRootElement(), xpathW, nsc, -1 );
            h = XMLTools.getNodeAsInt( xml.getRootElement(), xpathH, nsc, -1 );
        }
        return new Pair<Integer, Integer>( w, h );
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
    private List<List<PreparedLegendInfo>> createLegendImages( LegendMetadata legendMetadata, ViewContext vc,
                                                               double scaleDenominator )
                            throws IOException {
        String missingImageUrl = null;
        if ( getInitParameter( "MISSING_IMAGE" ) != null )
            missingImageUrl = getHomePath() + getInitParameter( "MISSING_IMAGE" );
        String tempDir = getInitParameter( "TEMPDIR" );
        LegendImageWriter legendImageWriter = new LegendImageWriter( missingImageUrl, tempDir );

        ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();
        List<Pair<String, URL>> legendURLs = createLegendURLs( vc, scaleDenominator );
        return legendImageWriter.accessLegend( sc, legendMetadata, legendURLs );
    }

    /**
     * performs the GetMap requests passed, draws the result onto an image that are stored in a temporary file. The name
     * of the file will be returned.
     * 
     * @param list
     * @return filename of image file
     * @throws PortalException
     * @throws IOException
     */
    private String performGetMapRequests( List<String> list )
                            throws PortalException, IOException {

        Map<String, String> map = KVP2Map.toMap( list.get( 0 ) );
        map.put( "ID", "ww" );
        GetMap getMap = null;
        try {
            getMap = GetMap.create( map );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            String s = Messages.format( "AbstractSimplePrintListener.GETMAPCREATION", list.get( 0 ) );
            throw new PortalException( s );
        }
        BufferedImage bi = new BufferedImage( getMap.getWidth(), getMap.getHeight(), BufferedImage.TYPE_INT_ARGB );
        Graphics g = bi.getGraphics();
        for ( int i = 0; i < list.size(); i++ ) {
            URL url = new URL( list.get( i ) + "&SERVICE=WMS" );
            Image img = null;
            try {
                img = ImageUtils.loadImage( url );
            } catch ( Exception e ) {
                // This is the case where a getmap request produces an error. This does not definitly mean that
                // the wms is not working, it could also be because the bounding box is not correct or too big. Try to
                // zoom
                // in, you might find something
                LOG.logInfo( "could not load map from: ", url.toExternalForm() );
            }
            g.drawImage( img, 0, 0, null );
        }
        g.dispose();

        String tempDir = getInitParameter( "TEMPDIR" );
        ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();
        return storeImage( sc, tempDir, bi );
    }

    private BufferedImage createScaleBar( RPCWebEvent rpc, List<String> list, Pair<Integer, Integer> scaleBarBize,
                                          int width, int height ) {
        RPCStruct struct = (RPCStruct) rpc.getRPCMethodCall().getParameters()[1].getValue();
        Integer dpi = null;
        if ( struct.getMember( "DPI" ) != null ) {
            dpi = Integer.parseInt( struct.getMember( "DPI" ).getValue().toString() );
        }
        Map<String, String> map = KVP2Map.toMap( list.get( 0 ) );
        String tmp = map.get( "BBOX" );
        Envelope bbox = GeometryFactory.createEnvelope( tmp, null );
        int sbw = (int) Math.round( scaleBarBize.first * ( dpi / 72d ) );
        int sbh = (int) Math.round( scaleBarBize.second * ( dpi / 72d ) );
        BufferedImage img = new BufferedImage( sbw, sbh, BufferedImage.TYPE_INT_ARGB );
        int fontSize = sbw / 100 + 6;
        MapUtils.drawScalbar( (Graphics2D) img.getGraphics(), img.getWidth(), bbox, new Dimension( width, height ),
                              null, fontSize );
        return img;
    }

    private void forwardPDF( byte[] result )
                            throws PortalException {
        // must be a byte array
        String tempDir = getInitParameter( "TEMPDIR" );
        if ( !tempDir.endsWith( "/" ) ) {
            tempDir = tempDir + '/';
        }
        if ( tempDir.startsWith( "/" ) ) {
            tempDir = tempDir.substring( 1, tempDir.length() );
        }
        ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();

        String fileName = UUID.randomUUID().toString();
        String s = StringTools.concat( 200, sc.getRealPath( tempDir ), '/', fileName, ".pdf" );
        try {
            RandomAccessFile raf = new RandomAccessFile( s, "rw" );
            raf.write( result );
            raf.close();
        } catch ( Exception e ) {
            LOG.logError( "could not write temporary pdf file: " + s, e );
            throw new PortalException( Messages.format( "AbstractSimplePrintListener.PDFCREATION", s ), e );
        }

        getRequest().setAttribute( "PDF", StringTools.concat( 200, tempDir, fileName, ".pdf" ) );
    }

    private void forwardImage( Image result, String format )
                            throws PortalException {

        format = format.substring( format.indexOf( '/' ) + 1 );

        String tempDir = getInitParameter( "TEMPDIR" );
        if ( !tempDir.endsWith( "/" ) ) {
            tempDir = tempDir + '/';
        }
        if ( tempDir.startsWith( "/" ) ) {
            tempDir = tempDir.substring( 1, tempDir.length() );
        }
        ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();

        String fileName = UUID.randomUUID().toString();
        String s = StringTools.concat( 200, sc.getRealPath( tempDir ), "/", fileName, ".", format );
        try {
            // make sure we have a BufferedImage
            if ( !( result instanceof BufferedImage ) ) {
                BufferedImage img = new BufferedImage( result.getWidth( null ), result.getHeight( null ),
                                                       BufferedImage.TYPE_INT_ARGB );

                Graphics g = img.getGraphics();
                g.drawImage( result, 0, 0, null );
                g.dispose();
                result = img;
            }

            ImageUtils.saveImage( (BufferedImage) result, s, 1 );
        } catch ( Exception e ) {
            LOG.logError( "could not write temporary pdf file: " + s, e );
            throw new PortalException( Messages.format( "AbstractSimplePrintListener.PDFCREATION", s ), e );
        }

        getRequest().setAttribute( "PDF", StringTools.concat( 200, tempDir, fileName, ".", format ) );
    }

    /**
     * fills the passed PrintMap request template with required values
     * 
     * @param vc
     * @return returns a list with all base requests
     */
    private List<String> createGetMapRequests( ViewContext vc, RPCWebEvent rpc, Envelope bbox, int width, int height ) {
        User user = getUser();
        String vsp = getVendorspecificParameters( rpc );

        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "&BBOX=" ).append( bbox.getMin().getX() ).append( ',' );
        sb.append( bbox.getMin().getY() ).append( ',' ).append( bbox.getMax().getX() );
        sb.append( ',' ).append( bbox.getMax().getY() ).append( "&WIDTH=" );
        sb.append( width ).append( "&HEIGHT=" ).append( height );
        if ( user != null ) {
            sb.append( "&user=" ).append( user.getName() );
            sb.append( "&password=" ).append( user.getPassword() );
        }
        if ( vsp != null ) {
            sb.append( "&" ).append( vsp );
        }
        String sessionId = (String) ( (HttpServletRequest) getRequest() ).getSession().getAttribute( "SESSIONID" );
        if ( sessionId != null ) {
            sb.append( "&sessionid=" ).append( sessionId );
        }
        String[] reqs = PortalUtils.createBaseRequests( vc );
        List<String> list = new ArrayList<String>( reqs.length );
        for ( int i = 0; i < reqs.length; i++ ) {
            list.add( reqs[i] + sb.toString() );
            LOG.logDebug( "GetMap request:", reqs[i] + sb.toString() );
        }

        return list;
    }

    private Envelope zoomToScale( Envelope bbox, PrintMetadata printMetadata, RPCMember scaleDef ) {
        if ( scaleDef != null ) {
            double ms = ( printMetadata.getMapWidth() / 72d ) * 0.0254;
            double currentScale = bbox.getWidth() / ms;
            String value = scaleDef.getValue().toString();
            if ( "currentBBOX".equals( value ) ) {
                // DO nothing
            } else if ( "currentBBOXRounded".equals( value ) ) {
                int i = 0;
                double newScale = currentScale;
                do {
                    newScale = scaleBounds[i];
                } while ( currentScale > scaleBounds[i++] );
                bbox = MapUtils.scaleEnvelope( bbox, currentScale, newScale );
            } else {
                double newScale = Double.parseDouble( value.trim() );
                bbox = MapUtils.scaleEnvelope( bbox, currentScale, newScale );
            }

        }
        return bbox;
    }

    /**
     * returns <code>null</code> and should be overwritten by an extending class
     * 
     * @return <code>null</code>
     */
    protected String getVendorspecificParameters( RPCWebEvent rpc ) {
        return null;
    }

    /**
     * returns <code>null</code> and should be overwritten by an extending class
     * 
     * @return <code>null</code>
     */
    protected User getUser() {
        return null;
    }

    /**
     * reads the view context to print from the users session
     * 
     * @param rpc
     * @return the viewcontext defined by the rpc
     */
    abstract protected ViewContext getViewContext( RPCWebEvent rpc );

    /**
     * returns legend access URLs for all visible layers of the passed view context. If a visible layer does not define
     * a LegendURL
     * 
     * @param vc
     * @param scaleDenominator
     * @return legend access URLs for all visible layers of the passed view context. If a visible layer does not define
     *         a LegendURL
     */
    private List<Pair<String, URL>> createLegendURLs( ViewContext vc, double scaleDenominator ) {
        double scale = calculateScaleHint( scaleDenominator );
        List<Pair<String, URL>> list = new ArrayList<Pair<String, URL>>();
        MapModel mapModel = vc.getGeneral().getExtension().getMapModel();
        List<MapModelEntry> mapModelEntries = mapModel.getMapModelEntries();
        addLegendUrls( vc, scale, list, mapModelEntries );
        return list;
    }

    private void addLegendUrls( ViewContext vc, double scale, List<Pair<String, URL>> list,
                                List<MapModelEntry> mapModelEntries ) {
        for ( MapModelEntry mapModelEntry : mapModelEntries ) {
            if ( mapModelEntry instanceof LayerGroup ) {
                LayerGroup group = (LayerGroup) mapModelEntry;
                addLegendUrls( vc, scale, list, group.getMapModelEntries() );
            } else if ( mapModelEntry instanceof MMLayer ) {
                addLegendUrl( vc, scale, list, mapModelEntry );
            }
        }
    }

    private void addLegendUrl( ViewContext vc, double scale, List<Pair<String, URL>> list, MapModelEntry mapModelEntry ) {
        MMLayer layerM = (MMLayer) mapModelEntry;
        String identifier = layerM.getIdentifier();
        Layer layer = findLayerWithIdentifier( vc, identifier );
        if ( layer == null ) {
            LOG.logWarning( "Could not found layer with identifier " + identifier + ". Layer will be ignored!" );
            return;
        }
        if ( !layer.isHidden() && layerIsInScale( scale, layer ) ) {
            Style style = layer.getStyleList().getCurrentStyle();
            URL url = null;
            if ( style.getLegendURL() != null ) {
                url = style.getLegendURL().getOnlineResource();
            }
            list.add( new Pair<String, URL>( layer.getTitle(), url ) );
        }
    }

    private Layer findLayerWithIdentifier( ViewContext vc, String identifier ) {
        LayerList layerList = vc.getLayerList();
        for ( Layer layer : layerList.getLayers() ) {
            if ( identifier.equals( layer.getExtension().getIdentifier() ) ) {
                return layer;
            }
        }
        return null;
    }

    private boolean layerIsInScale( double scale, Layer layer ) {
        if ( scale < layer.getExtension().getMinScaleHint() || scale > layer.getExtension().getMaxScaleHint() ) {
            return false;
        }
        return true;
    }

    /**
     * validates the incoming request/RPC if conatins all required elements
     * 
     * @param rpc
     * @throws PortalException
     */
    protected void validate( RPCWebEvent rpc )
                            throws PortalException {
        RPCStruct struct = (RPCStruct) rpc.getRPCMethodCall().getParameters()[1].getValue();
        if ( struct.getMember( "TEMPLATE" ) == null ) {
            throw new PortalException( Messages.getString( "portal.common.control.VALIDATIONERROR" ) );
        }

        if ( rpc.getRPCMethodCall().getParameters()[0].getValue() == null ) {
            throw new PortalException( Messages.getString( "portal.common.control.VALIDATIONERROR" ) );
        }
    }

    protected JasperReport getReport( XMLFragment xml )
                            throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream( 50000 );
        xml.write( bos, null );
        ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
        JasperDesign jasperDesign = net.sf.jasperreports.engine.xml.JRXmlLoader.load( bis );
        bis.close();
        bos.close();
        return net.sf.jasperreports.engine.JasperCompileManager.compileReport( jasperDesign );
    }

    private PrintMetadata parsePrintMetadata( String path )
                            throws Exception {
        File file = new File( path );
        XMLFragment xml = new XMLFragment( file );

        String xpath = "jasper:detail/jasper:band/jasper:image/jasper:reportElement[./@key = 'image-1']";
        Element element = (Element) XMLTools.getNode( xml.getRootElement(), xpath, nsc );
        int mapWidth;
        int mapHeight;
        if ( element != null ) {
            mapWidth = XMLTools.getNodeAsInt( element, "@width", nsc, Integer.MIN_VALUE );
            mapHeight = XMLTools.getNodeAsInt( element, "@height", nsc, Integer.MIN_VALUE );
        } else {
            mapWidth = Integer.parseInt( getInitParameter( "WIDTH" ) );
            mapHeight = Integer.parseInt( getInitParameter( "HEIGHT" ) );
        }
        LegendMetadata legendMetadata = parseLegendMetadata( xml );
        return new PrintMetadata( mapWidth, mapHeight, legendMetadata );
    }

    private LegendMetadata parseLegendMetadata( XMLFragment xml )
                            throws Exception {
        String legendBgColor = getInitParameter( "LEGENDBGCOLOR" );
        int spacing = parseSpacing( 10 );
        String xpath = "jasper:detail/jasper:band/jasper:image/jasper:reportElement[./@key = 'legendTemplateBand']";
        Element element = (Element) XMLTools.getNode( xml.getRootElement(), xpath, nsc );
        int maxScaleInPercent = parseAsInt( "MAXSCALETOFITINPERCENT", -1 );
        String initParameterLegendToLargeMsg = getInitParameter( "LEGENDTOLARGEMSG" );
        String legendToLargeMsg = initParameterLegendToLargeMsg != null ? initParameterLegendToLargeMsg
                                                                       : "Legend %sis too large.";
        String fontFamily = parseAsString( "FONT_FAMILY", "Serif" );
        float fontSize = parseAsFloat( "FONT_SIZE", 12 );
        if ( element != null ) {
            int width = XMLTools.getNodeAsInt( element, "@width", nsc, Integer.MIN_VALUE );
            int height = XMLTools.getNodeAsInt( element, "@height", nsc, Integer.MIN_VALUE );
            int numberOfColumns = calculateNoOfColumns( width, height );
            if ( width != Integer.MIN_VALUE && height != Integer.MIN_VALUE ) {
                LOG.logDebug( "Found legend on multiple pages, each page with width " + width + " and height " + height );
                return new LegendMetadata( true, width, height, legendBgColor, numberOfColumns, spacing,
                                           maxScaleInPercent, legendToLargeMsg, fontFamily, fontSize );
            }
        }
        int width = Integer.parseInt( getInitParameter( "LEGENDWIDTH" ) );
        int height = Integer.parseInt( getInitParameter( "LEGENDHEIGHT" ) );
        LOG.logDebug( "Default legend." );
        return new LegendMetadata( false, width, height, legendBgColor, 1, spacing, maxScaleInPercent,
                                   legendToLargeMsg, fontFamily, fontSize );
    }

    private String parseAsString( String paramName, String defaultValue ) {
        String initParameter = getInitParameter( paramName );
        if ( initParameter != null ) {
            return initParameter;
        }
        return defaultValue;
    }

    private int parseAsInt( String paramName, int defaultValue ) {
        String initParameter = getInitParameter( paramName );
        if ( initParameter != null )
            try {
                return Integer.parseInt( initParameter );
            } catch ( NumberFormatException e ) {
                LOG.logWarning( "Value of parameter " + paramName + " is not a valid integer!" );
            }
        return defaultValue;
    }

    private float parseAsFloat( String paramName, float defaultValue ) {
        String initParameter = getInitParameter( paramName );
        if ( initParameter != null )
            try {
                return Float.parseFloat( initParameter );
            } catch ( NumberFormatException e ) {
                LOG.logWarning( "Value of parameter " + paramName + " is not a valid integer!" );
            }
        return defaultValue;
    }

    private int calculateHeight( Integer dpi, PrintMetadata printMetadata ) {
        int height = printMetadata.getMapHeight();
        if ( dpi != null ) {
            height = (int) Math.round( height * ( dpi / 72d ) );
        }
        return height;
    }

    private int calculateWidth( Integer dpi, PrintMetadata printMetadata ) {
        int width = printMetadata.getMapWidth();
        if ( dpi != null ) {
            width = (int) Math.round( width * ( dpi / 72d ) );
        }
        return width;
    }

    private Envelope calculateBbox( RPCStruct bboxStruct, RPCStruct scaleStruct, PrintMetadata printMetadata ) {
        // set boundingbox/envelope
        RPCMember member = bboxStruct.getMember( "boundingBox" );
        RPCStruct subStruct = (RPCStruct) member.getValue();

        double minx = (Double) subStruct.getMember( "minx" ).getValue();
        double miny = (Double) subStruct.getMember( "miny" ).getValue();
        double maxx = (Double) subStruct.getMember( "maxx" ).getValue();
        double maxy = (Double) subStruct.getMember( "maxy" ).getValue();

        int width = printMetadata.getMapWidth();
        int height = printMetadata.getMapHeight();

        Envelope bbox = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, null );
        bbox = MapUtils.ensureAspectRatio( bbox, width, height );
        bbox = zoomToScale( bbox, printMetadata, scaleStruct.getMember( "SCALE" ) );
        return bbox;
    }

    private Integer calculateDpi( RPCStruct struct ) {
        Integer dpi = null;
        if ( struct.getMember( "DPI" ) != null ) {
            dpi = Integer.parseInt( struct.getMember( "DPI" ).getValue().toString() );
        }
        LOG.logInfo( "dpi: ", dpi );
        return dpi;
    }

    private double calculateScaleHint( double scaleDenominator ) {
        double pixelwidth = scaleDenominator * 0.00028;
        return Math.sqrt( 2 * ( pixelwidth * pixelwidth ) );
    }

    private double calculateScaleDenominator( PrintMetadata printMetadata, Envelope bbox )
                            throws InconsistentRequestException, XMLParsingException, IOException, SAXException {
        // TODO The map path is static. It should be instead read from somewhere else.
        // A good idea would be to save the path in the web.xml of the corresponding application,
        // or in controller.xml of the PdfPrintListener and sends it with rpc request

        // CoordinateSystem crs = CRSFactory.create( gm.getSrs() );

        // map size in template in metre; templates generated by iReport are designed
        // to assume a resolution of 72dpi
        double ms = ( printMetadata.getMapWidth() / 72d ) * 0.0254;
        // TODO
        // consider no metric CRS
        return bbox.getWidth() / ms;
    }

    private int calculateNoOfColumns( int width, int height ) {
        if ( width > height )
            return parseAsInt( "NUMBEROFCOLUMNS_LANDSCAPE", 4 );
        return parseAsInt( "NUMBEROFCOLUMNS_PORTAIT", 4 );
    }

    private int parseSpacing( int defaultSpacing ) {
        try {
            String spacingParameter = getInitParameter( "SPACING" );
            if ( spacingParameter != null )
                return Integer.parseInt( spacingParameter );
        } catch ( NumberFormatException e ) {
            LOG.logWarning( "Could not parse parameter SPACING (from controller.xml): " + e.getMessage() );
        }
        return defaultSpacing;
    }

}