//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/standard/wms/control/layertree/SetVisibilityTreeNodeListener.java $
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deegree.enterprise.control.ajax.AbstractListener;
import org.deegree.enterprise.control.ajax.ResponseHandler;
import org.deegree.enterprise.control.ajax.WebEvent;
import org.deegree.portal.Constants;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.LayerGroup;
import org.deegree.portal.context.MapModel;
import org.deegree.portal.context.MapModelEntry;
import org.deegree.portal.context.ViewContext;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version $Revision: 28707 $, $Date: 2010-12-10 13:45:12 +0100 (Fr, 10. Dez 2010) $
 */
public class SetVisibilityTreeNodeListener extends AbstractListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.enterprise.control.ajax.AbstractListener#actionPerformed(org.deegree.enterprise.control.ajax.WebEvent
     * , org.deegree.enterprise.control.ajax.ResponseHandler)
     */
    @SuppressWarnings("unchecked")
    public void actionPerformed( WebEvent event, ResponseHandler responseHandler )
                            throws IOException {
        Map<Object, Object> parameter = event.getParameter();
        String node = (String) parameter.get( "node" );
        boolean hidden = (Boolean) parameter.get( "hidden" );
        ViewContext vc = (ViewContext) event.getSession().getAttribute( Constants.CURRENTMAPCONTEXT );
        MapModel mapModel = vc.getGeneral().getExtension().getMapModel();
        MapModelEntry mme = mapModel.getMapModelEntryByIdentifier( node );
        if ( mme != null ) {
            mme.setHidden( hidden );
            if ( mme instanceof LayerGroup ) {
                recurseDown( hidden, (LayerGroup) mme );
            }
            recurseUp( mme );
        }
        List<String[]> visibleLayers = new ArrayList<String[]>();
        Layer[] layers = vc.getLayerList().getLayers();
        for ( Layer layer : layers ) {
            if ( !layer.isHidden() ) {
                visibleLayers.add( new String[] {layer.getName(), layer.getExtension().getIdentifier(), layer.getServer().getOnlineResource().toExternalForm()} );
            }
        }
        String[][] tmp = visibleLayers.toArray( new String[visibleLayers.size()][] );
        responseHandler.writeAndClose( false, tmp );
    }

    /**
     * @param mme
     */
    private void recurseUp( MapModelEntry mme ) {
        LayerGroup layerGroup = mme.getParent();
        if ( layerGroup != null ) {
            List<MapModelEntry> list = layerGroup.getMapModelEntries();
            boolean visible = false;
            for ( MapModelEntry mapModelEntry : list ) {
                if ( !mapModelEntry.isHidden() ) {
                    visible = true;
                    break;
                }
            }
            layerGroup.setHidden( !visible );
            recurseUp( layerGroup );
        }
    }

    /**
     * 
     * @param hidden
     * @param layerGroup
     */
    private void recurseDown( boolean hidden, LayerGroup layerGroup ) {
        List<MapModelEntry> list = layerGroup.getMapModelEntries();
        for ( int i = 0; i < list.size(); i++ ) {
            MapModelEntry mapModelEntry = list.get( i );
            mapModelEntry.setHidden( hidden );
            if ( mapModelEntry instanceof LayerGroup ) {
                recurseDown( hidden, (LayerGroup) mapModelEntry );
            }
        }
    }

}
