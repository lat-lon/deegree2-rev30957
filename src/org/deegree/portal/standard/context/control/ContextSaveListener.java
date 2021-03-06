//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/standard/context/control/ContextSaveListener.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn
 and
 - lat/lon GmbH

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

package org.deegree.portal.standard.context.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCUtils;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.i18n.Messages;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.portal.Constants;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.LayerExtension;
import org.deegree.portal.context.LayerGroup;
import org.deegree.portal.context.LayerList;
import org.deegree.portal.context.MMLayer;
import org.deegree.portal.context.MapModel;
import org.deegree.portal.context.MapModelEntry;
import org.deegree.portal.context.Style;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.XMLFactory;
import org.w3c.dom.Document;

/**
 * This class saves a new context based on changes made by the user (on the client) and based on the original context
 * xml. <br/>
 * Files are saved under .../WEB-INF/xml/users/some_user, where some_user is passed as an RPC parameter. Files should be
 * saved with .xml extension because the default load context listener class looks up those files. <br/>
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: jmays $
 * 
 * @version $Revision: 21347 $, $Date: 2009-12-09 17:04:39 +0100 (Mi, 09. Dez 2009) $
 */
public class ContextSaveListener extends AbstractContextListener {

    private static final ILogger LOG = LoggerFactory.getLogger( ContextSaveListener.class );

    private static String userDir = "WEB-INF/conf/igeoportal/users/";

    private static String contextDir = "WEB-INF/conf/igeoportal/";

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    @Override
    public void actionPerformed( FormEvent event ) {

        RPCWebEvent rpc = (RPCWebEvent) event;
        try {
            validate( rpc );
        } catch ( PortalException e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( Messages.getMessage( "IGEO_STD_CNTXT_INVALID_RPC", "ContextSave", e.getMessage() ) );
            return;
        }

        String newContext = null;
        try {
            newContext = storeContext( rpc );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( Messages.getMessage( "IGEO_STD_CNTXT_ERROR_SAVE_CNTXT" ) );
            return;
        }

        // forward to new page
        this.getRequest().setAttribute( Constants.MESSAGE,
                                        Messages.getMessage( "IGEO_STD_CNTXT_SUCCESS_SAVE_CNTXT", newContext ) );
        this.getRequest().setAttribute( "newContext", newContext );
    }

    /**
     * stores the current context of the user with a defined name
     * 
     * @param event
     * @return name of the context that has been stored
     * @throws PortalException
     * @throws ContextException
     * @throws XMLParsingException
     * @throws IOException
     * @throws UnknownCRSException
     * @throws ParserConfigurationException
     */
    private String storeContext( RPCWebEvent event )
                            throws PortalException, ParserConfigurationException, UnknownCRSException, IOException,
                            XMLParsingException, ContextException {

        RPCMethodCall mc = event.getRPCMethodCall();
        RPCParameter[] pars = mc.getParameters();
        RPCStruct struct = (RPCStruct) pars[0].getValue();

        // read base context
        StringBuffer path2Dir = new StringBuffer( getHomePath() );
        path2Dir.append( contextDir );

        // access base context
        HttpSession session = ( (HttpServletRequest) getRequest() ).getSession();
        ViewContext vc = (ViewContext) session.getAttribute( Constants.CURRENTMAPCONTEXT );

        String sid = RPCUtils.getRpcPropertyAsString( struct, "sessionID" );
        vc = vc.clone( null, sid );

        changeLayerList( vc );

        // change values: BBOX and Layer List
        Envelope bbox = extractBBox( (RPCStruct) struct.getMember( Constants.RPC_BBOX ).getValue(), null );
        changeBBox( vc, bbox );
        // RPCMember[] layerList = ( (RPCStruct) struct.getMember( "layerList" ).getValue() ).getMembers();
        // changeLayerList( vc, layerList );

        // save new context
        // get map context value
        String username = "default";
        try {

            LOG.logDebug( "sessionID ", sid );
            username = getUserName( sid );
            if ( username == null ) {
                username = "default";
            }
            LOG.logDebug( "username ", username );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }

        String newContext = RPCUtils.getRpcPropertyAsString( struct, "newContext" );

        // check for init params, adjusting the file name input of the user
        if ( getInitParameter( "RESTRICT_CHARS" ) != null || getInitParameter( "ADD_XML_SUFFIX" ) != null ) {
            newContext = fixFileName( newContext, getInitParameter( "RESTRICT_CHARS" ),
                                      getInitParameter( "ADD_XML_SUFFIX" ) );
        }

        path2Dir = new StringBuffer( getHomePath() );
        path2Dir.append( userDir );
        path2Dir.append( username );
        File file = new File( path2Dir.toString() );
        if ( !file.exists() ) {
            // create directory if not exists
            file.mkdir();
        }
        path2Dir.append( "/" );
        path2Dir.append( newContext );

        saveDocument( vc, path2Dir.toString() );

        return newContext;
    }

    /**
     * This method replaces all forbidden characters with underscores ("_") and sets the file extension to ".xml".
     * Accepted characters are: [a-zA-Z_0-9-.]
     * 
     * @param newContext
     * @param restrictedChars
     *            may be null
     * @param addXmlSuffix
     *            may be null
     * @return updated version of the passed String
     */
    private String fixFileName( String newContext, String restrictedChars, String addXmlSuffix ) {

        if ( restrictedChars != null ) {
            String regex = null;
            if ( restrictedChars.length() == 0 ) {
                // replace everything that is not a word character [a-zA-Z_0-9]
                regex = "[^\\w]";
            } else {
                // replace everything that is not a word character [a-zA-Z_0-9] or one of the restrictedChars
                regex = "[^\\w" + restrictedChars + "]";
            }

            // replace forbidden characters with underscore
            newContext = newContext.replaceAll( regex, "_" );
            // trimm double underscores
            while ( newContext.contains( "__" ) ) {
                newContext = newContext.replace( "__", "_" );
            }
        }

        if ( "true".equals( addXmlSuffix ) ) {
            // add proper file ending ".xml"
            if ( !newContext.endsWith( ".xml" ) ) {
                newContext += ".xml";
            }
        }
        return newContext;
    }

    /**
     * saves the new context as xml
     * 
     * @param vc
     * @param filename
     * @throws PortalException
     */
    public static final void saveDocument( ViewContext vc, String filename )
                            throws PortalException {
        try {
            XMLFragment xml = XMLFactory.export( vc );
            FileOutputStream fos = new FileOutputStream( filename );
            xml.write( fos );
            fos.close();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new PortalException( Messages.getMessage( "IGEO_STD_CNTXT_ERROR_SAVE_FILE", filename ) );
        }
    }

    /**
     * validates the incoming RPC event
     * 
     * @param rpc
     * @throws PortalException
     */
    private void validate( RPCWebEvent rpc )
                            throws PortalException {
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();
        RPCMember username = struct.getMember( "sessionID" );
        if ( username == null ) {
            throw new PortalException( Messages.getMessage( "IGEO_STD_CNTXT_MISSING_PARAM", "sessionID", "ContextSave" ) );
        }
        RPCMember newContext = struct.getMember( "newContext" );
        if ( newContext == null ) {
            throw new PortalException(
                                       Messages.getMessage( "IGEO_STD_CNTXT_MISSING_PARAM", "newContext", "ContextSave" ) );
        }
        RPCMember layerList = struct.getMember( "layerList" );
        if ( layerList == null ) {
            throw new PortalException( Messages.getMessage( "IGEO_STD_CNTXT_MISSING_PARAM", "layerList", "ContextSave" ) );
        }
        // TODO validate box: should do this in a common (static) method
        // for many listeners that need a bbox
    }

    /**
     * common method to save xml
     * 
     * @param os
     * @param doc
     * @throws PortalException
     */
    protected static void internalSave( OutputStream os, Document doc )
                            throws PortalException {
        try {
            Source source = new DOMSource( doc );
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform( source, new StreamResult( os ) );
        } catch ( Exception e ) {
            throw new PortalException( Messages.getMessage( "IGEO_STD_CNTXT_ERROR_INTERNAL_SAVE",
                                                            StringTools.stackTraceToString( e.getStackTrace() ) ) );
        }
    }

    private void changeLayerList( ViewContext vc )
                            throws ContextException {
        LayerList layerList = createLayersList( vc );
        vc.setLayerList( layerList );
    }

    private LayerList createLayersList( ViewContext vc ) {
        List<Layer> layers = new ArrayList<Layer>();
        MapModel mapModelEntries = vc.getGeneral().getExtension().getMapModel();
        addLayersFromMapModel( vc, mapModelEntries.getMapModelEntries(), layers );
        Layer[] layersArray = layers.toArray( new Layer[layers.size()] );
        LayerList layerList = new LayerList( layersArray );
        return layerList;
    }

    private void addLayersFromMapModel( ViewContext vc, List<MapModelEntry> mapModelEntries, List<Layer> layers ) {
        for ( MapModelEntry mapModelEntry : mapModelEntries ) {
            if ( mapModelEntry instanceof LayerGroup ) {
                LayerGroup group = (LayerGroup) mapModelEntry;
                List<MapModelEntry> subMapModelEntries = group.getMapModelEntries();
                addLayersFromMapModel( vc, subMapModelEntries, layers );
            } else if ( mapModelEntry instanceof MMLayer ) {
                String identifier = mapModelEntry.getIdentifier();
                Layer layer = editLayer( vc, identifier );
                layers.add( layer );
            }
        }
    }

    private Layer editLayer( ViewContext vc, String identifier ) {
        Layer layer = findLayerWithIdentifier( vc, identifier );
        layer.setAbstract( null );
        Style[] styles = layer.getStyleList().getStyles();
        for ( Style style : styles ) {
            style.setLegendURL( null );
        }
        layer.getExtension().setAuthentication( LayerExtension.SESSIONID );
        return layer;
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

}
