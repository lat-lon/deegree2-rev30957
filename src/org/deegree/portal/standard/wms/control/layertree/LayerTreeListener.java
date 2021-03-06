//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/standard/wms/control/layertree/LayerTreeListener.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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
package org.deegree.portal.standard.wms.control.layertree;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.deegree.enterprise.control.ajax.AbstractListener;
import org.deegree.enterprise.control.ajax.ResponseHandler;
import org.deegree.enterprise.control.ajax.WebEvent;
import org.deegree.model.spatialschema.Point;
import org.deegree.portal.Constants;
import org.deegree.portal.context.LayerGroup;
import org.deegree.portal.context.MMLayer;
import org.deegree.portal.context.MapModel;
import org.deegree.portal.context.MapModelEntry;
import org.deegree.portal.context.ViewContext;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version $Revision: 30935 $, $Date: 2011-05-26 17:47:14 +0200 (Do, 26. Mai 2011) $
 */
public class LayerTreeListener extends AbstractListener {

    public void actionPerformed( WebEvent event, ResponseHandler responseHandler )
                            throws IOException {
        ViewContext vc = (ViewContext) event.getSession().getAttribute( Constants.CURRENTMAPCONTEXT );
        Point[] points = vc.getGeneral().getBoundingBox();

        StringBuilder sb = new StringBuilder( 10000 );
        sb.append( '[' );
        // return all nodes for root
        MapModel mapModel = vc.getGeneral().getExtension().getMapModel();
        List<MapModelEntry> layerGroups = mapModel.getMapModelEntries();
        for ( int i = 0; i < layerGroups.size(); i++ ) {
            if ( layerGroups.get( i ) instanceof LayerGroup ) {
                LayerGroup lg = (LayerGroup) layerGroups.get( i );
                List<MapModelEntry> mme = lg.getMapModelEntries();

                sb.append( "{'text' : " );
                sb.append( "'" ).append( encodeWithHtmlEntity( lg.getTitle() ) ).append( "'," );
                sb.append( "'id' : '" ).append( lg.getIdentifier() ).append( "'," );
                sb.append( "'checked': true," );
                sb.append( "'expanded': " ).append( lg.isExpanded() ).append( "," );
                sb.append( "'leaf' : false, 'cls' : 'folder' " );
                if ( mme.size() == 0 ) {
                    sb.append( ", 'children': [] " );
                }
                appendChildren( sb, lg );
                sb.append( "}" );
            } else if ( layerGroups.get( i ) instanceof MMLayer ) {
                appendLayer( sb, (MMLayer) layerGroups.get( i ) );
            }
            if ( i < layerGroups.size() - 1 ) {
                sb.append( ',' );
            }
        }
        sb.append( ']' );
        String charEnc = Charset.defaultCharset().displayName();
        responseHandler.setContentType( "application/json; " + charEnc );
        responseHandler.writeAndClose( sb.toString() );
    }

    /**
     * @param sb
     * @param n
     * @param vc
     */
    private void appendChildren( StringBuilder sb, LayerGroup layerGroup ) {

        List<MapModelEntry> mme = layerGroup.getMapModelEntries();

        if ( mme.size() > 0 ) {

            sb.append( ", 'children' : [" );
            for ( int i = 0; i < mme.size(); i++ ) {
                if ( mme.get( i ) instanceof MMLayer ) {
                    MMLayer layer = (MMLayer) mme.get( i );
                    appendLayer( sb, layer );
                } else {
                    LayerGroup lg = (LayerGroup) mme.get( i );
                    appendLayerGroup( sb, lg );
                }
                if ( i < mme.size() - 1 ) {
                    sb.append( ',' );
                }
            }
            sb.append( "]" );
        }

    }

    private void appendLayer( StringBuilder sb, MMLayer layer ) {
        if ( layer.getLayer().getExtension().isValid() ) {
            URL s = null;
            if ( layer.getLayer().getStyleList().getCurrentStyle().getLegendURL() != null ) {
                s = layer.getLayer().getStyleList().getCurrentStyle().getLegendURL().getOnlineResource();
            }
            sb.append( "{'text' : " );
            sb.append( "'" ).append( encodeWithHtmlEntity( layer.getTitle() ) ).append( "'," );
            sb.append( "'id' : '" ).append( layer.getLayer().getExtension().getIdentifier() ).append( "'," );
            if ( s != null ) {
                sb.append( "'img' : '" ).append( s.toExternalForm() ).append( "'," );
            }
            if ( layer.getLayer().getAbstract() != null ) {
                String encodedAbstract = encodeWithHtmlEntity( layer.getLayer().getAbstract() );
                sb.append( "'qtip': '" ).append( encodedAbstract ).append( "'," );
            } else {
                sb.append( "'qtip': '" ).append( encodeWithHtmlEntity( layer.getTitle() ) ).append( "'," );
            }

            sb.append( "'checked': " ).append( layer.isHidden() ? "false," : "true," );
            sb.append( "'leaf' : true, 'cls' : 'file'}" );
        }
    }

    private void appendLayerGroup( StringBuilder sb, LayerGroup lg ) {
        List<MapModelEntry> children = lg.getMapModelEntries();

        sb.append( "{'text' : " );
        sb.append( "'" ).append( encodeWithHtmlEntity( lg.getTitle() ) ).append( "'," );
        sb.append( "'id' : '" ).append( lg.getIdentifier() ).append( "'," );
        sb.append( "'checked': " ).append( !lg.isHidden() ).append( "," );
        sb.append( "'expanded': " ).append( lg.isExpanded() ).append( "," );
        sb.append( "'leaf' : false, 'cls' : 'folder' " );
        if ( children.size() == 0 ) {
            sb.append( ", 'children': [] " );
        }
        appendChildren( sb, lg );
        sb.append( "}" );
    }

    private String encodeWithHtmlEntity( String s ) {
        StringBuffer buf = new StringBuffer();
        boolean isEntity = false;
        for ( int i = 0; i < s.length(); i++ ) {
            char c = s.charAt( i );
            if ( isEntity ) {
                buf.append( c );
                if ( c == ';' )
                    isEntity = false;
            } else {
                if ( c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' ) {
                    buf.append( c );
                } else if ( c == '&' ) {
                    isEntity = true;
                    buf.append( c );
                } else {
                    buf.append( "&#" + (int) c + ";" );
                }
            }
        }
        return buf.toString();
    }

}