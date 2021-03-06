//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/standard/wms/control/GetWMSLayerListener.java $
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
package org.deegree.portal.standard.wms.control;

import static org.deegree.framework.util.CharsetUtils.convertToReaderWithDefaultCharset;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.enterprise.WebUtils;
import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCUtils;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.i18n.Messages;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocumentFactory;
import org.deegree.owscommon_new.DomainType;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;
import org.deegree.portal.PortalException;
import org.deegree.portal.standard.wms.util.ClientHelper;
import org.xml.sax.SAXException;

/**
 * Lister class for accessing the layers of WMS and return it to the client.
 * 
 * @version $Revision: 24802 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version 1.0. $Revision: 24802 $, $Date: 2010-06-09 14:54:22 +0200 (Mi, 09. Jun 2010) $
 * 
 * @since 2.0
 */
public class GetWMSLayerListener extends AbstractListener {

    private static ILogger LOG = LoggerFactory.getLogger( GetWMSLayerListener.class );

    /**
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    @Override
    public void actionPerformed( FormEvent event ) {

        RPCWebEvent rpc = (RPCWebEvent) event;
        try {
            validate( rpc );
        } catch ( Exception e ) {
            gotoErrorPage( "Not a valid RPC for GetWMSLayer\n" + e.getMessage() );
        }

        WMSCapabilities capabilities = null;
        String href = null;
        try {
            href = getURL( rpc );
            capabilities = getWMSCapabilities( href );
        } catch ( MalformedURLException ue ) {
            LOG.logError( ue.getMessage(), ue );
            gotoErrorPage( Messages.getMessage( "IGEO_STD_INVALID_URL", href ) );
            return;
        } catch ( PortalException e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( Messages.getMessage( "IGEO_STD_ADDWMS_INVALID_VERSION" ) );
            return;
        } catch ( IOException e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( Messages.getMessage( "IGEO_STD_ADDWMS_NO_TARGET", href ) );
            return;
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( Messages.getMessage( "IGEO_STD_INVALID_XML", href ) );
            return;
        } catch ( InvalidCapabilitiesException e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( Messages.getMessage( "IGEO_STD_ADDWMS_INVALID_CAPS", href ) );
            return;
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( Messages.getMessage( "IGEO_STD_INVALID_XML", href ) );
            return;
        }

        String s = ClientHelper.getLayersAsTree( capabilities.getLayer() );
        getRequest().setAttribute( "WMSLAYER", s );
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();
        String address = RPCUtils.getRpcPropertyAsString( struct, "WMSURL" );
        getRequest().setAttribute( "WMSURL", address );
        getRequest().setAttribute( "WMSVERSION", capabilities.getVersion() );
        s = capabilities.getServiceIdentification().getTitle();
        s = s.replaceAll( "'", "" );
        getRequest().setAttribute( "WMSNAME", s );
        getRequest().setAttribute( "ABSTRACT", capabilities.getLayer().getAbstract() );

        OperationsMetadata om = capabilities.getOperationMetadata();
        Operation op = om.getOperation( new QualifiedName( "GetMap" ) );
        if ( op == null )
            om.getOperation( new QualifiedName( "map" ) );
        DomainType parameter = (DomainType) op.getParameter( new QualifiedName( "Format" ) );

        // the request needs a String[], not a String
        List<TypedLiteral> values = parameter.getValues();
        String[] valueArray = new String[values.size()];
        for ( int i = 0; i < values.size(); ++i )
            valueArray[i] = values.get( i ).getValue();
        getRequest().setAttribute( "FORMATS", valueArray );
    }

    /**
     * @param rpc
     * @throws PortalException
     */
    private void validate( RPCWebEvent rpc )
                            throws PortalException {
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();
        RPCMember address = struct.getMember( "WMSURL" );
        if ( address == null ) {
            throw new PortalException( "missing parameter WMSURL in RPC for GetWMSLayer" );
        }
        RPCMember version = struct.getMember( "VERSION" );
        if ( version != null
             && ( ( (String) version.getValue() ).compareTo( "1.3.0" ) > 0 || ( (String) version.getValue() ).compareTo( "1.1.0" ) < 0 ) ) {
            throw new PortalException( "VERSION must be >= 1.0.0 and <= 1.3.0 but it is " + version.getValue() );
        }
    }

    /**
     * 
     * @param rpc
     * @return wms url
     * @throws MalformedURLException
     */
    private String getURL( RPCWebEvent rpc )
                            throws MalformedURLException {
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();
        String address = RPCUtils.getRpcPropertyAsString( struct, "WMSURL" );

        // according to OGC WMS 1.3 Testsuite a URL to a service operation
        // via HTTPGet must end with '?' or '&'
        String href = OWSUtils.validateHTTPGetBaseURL( address );

        StringBuffer sb = new StringBuffer( href );
        String version = RPCUtils.getRpcPropertyAsString( struct, "VERSION" );
        if ( "1.0.0".equals( version ) ) {
            sb.append( "service=WMS&request=capabilities" );
        } else {
            sb.append( "service=WMS&request=GetCapabilities" );
        }
        if ( version != null ) {
            if ( "1.0.0".equals( version ) ) {
                sb.append( "&WMTVER=" ).append( version );
            } else {
                sb.append( "&version=" ).append( version );
            }
        }

        String sessionid = RPCUtils.getRpcPropertyAsString( struct, "SESSIONID" );
        if ( sessionid != null ) {
            sb.append( "&sessionID=" ).append( sessionid );
        }

        String useAuthentification = RPCUtils.getRpcPropertyAsString( struct, "useAuthentification" );
        getRequest().setAttribute( "USEAUTHENTICATION", "true".equals( useAuthentification ) );
        if ( "true".equals( useAuthentification ) ) {
            String user = RPCUtils.getRpcPropertyAsString( struct, "USER" );
            if ( user != null ) {
                String password = RPCUtils.getRpcPropertyAsString( struct, "USER" );
                sb.append( "&user=" ).append( user );
                if ( password != null ) {
                    sb.append( "&password=" ).append( password );
                }
            }
        }
        LOG.logDebug( "get capabilities URL: ", sb );
        return sb.toString();
    }

    private WMSCapabilities getWMSCapabilities( String href )
                            throws PortalException, IOException, SAXException, InvalidCapabilitiesException,
                            XMLParsingException {
        InputStream stream = retrieveCapabilities( href );
        Reader capAsReader = convertToReaderWithDefaultCharset( stream );

        OGCCapabilitiesDocument doc = new WMSCapabilitiesDocument();
        doc.load( capAsReader, XMLFragment.DEFAULT_URL );
        doc = WMSCapabilitiesDocumentFactory.getWMSCapabilitiesDocument( doc.getRootElement() );

        return (WMSCapabilities) doc.parseCapabilities();
    }

    private static InputStream retrieveCapabilities( String href )
                            throws MalformedURLException, IOException, HttpException {
        // consider that the reference to the capabilities may has been
        // made by a file URL to a local copy
        if ( href.toLowerCase().startsWith( "http://" ) || href.toLowerCase().startsWith( "https://" ) ) {
            HttpClient httpclient = new HttpClient();
            httpclient = WebUtils.enableProxyUsage( httpclient, new URL( href ) );

            GetMethod httpget = new GetMethod( href );
            LOG.logDebug( "GetCapabilities: ", href );

            httpclient.executeMethod( httpget );
            return httpget.getResponseBodyAsStream();
        } else {
            URL url = null;
            if ( href.endsWith( "?" ) ) {
                url = new URL( href.substring( 0, href.length() - 1 ) );
            } else {
                url = new URL( href );
            }
            return url.openStream();
        }
    }

}
