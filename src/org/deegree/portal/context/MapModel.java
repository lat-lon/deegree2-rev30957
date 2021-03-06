//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/context/MapModel.java $
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
package org.deegree.portal.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.portal.PortalException;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version $Revision: 21671 $, $Date: 2009-12-29 09:43:44 +0100 (Di, 29. Dez 2009) $
 */
public class MapModel {

    private List<MapModelEntry> mapModelEntries;

    /**
     * 
     * @param layerGroups
     */
    public void setMapModelEntries( List<MapModelEntry> layerGroups ) {
        this.mapModelEntries = layerGroups;
    }

    /**
     * @return the layerGroups
     */
    public List<MapModelEntry> getMapModelEntries() {
        return mapModelEntries;
    }

    /**
     * 
     * @param action
     * @return list of {@link Layer} selected for the passed action
     */
    public List<MMLayer> getLayersSelectedForAction( String action ) {
        List<MMLayer> tmp = new ArrayList<MMLayer>();
        getLayersForAction( mapModelEntries, action, tmp );
        return Collections.unmodifiableList( tmp );
    }

    private void getLayersForAction( List<MapModelEntry> lgs, String action, List<MMLayer> collector ) {
        for ( MapModelEntry mapModelEntry : lgs ) {
            if ( mapModelEntry instanceof LayerGroup ) {
                LayerGroup layerGroup = (LayerGroup) mapModelEntry;
                List<MMLayer> mapModelEntries = layerGroup.getLayers();
                for ( MMLayer mapModelEntrry : mapModelEntries ) {
                    if ( mapModelEntrry.getSelectedFor().contains( action ) ) {
                        collector.add( mapModelEntrry );
                    }
                    getLayersForAction( layerGroup.getMapModelEntries(), action, collector );
                }
            } else if ( mapModelEntry instanceof MMLayer ) {
                if ( mapModelEntry.getSelectedFor().contains( action ) ) {
                    collector.add( (MMLayer) mapModelEntry );
                }
            }
        }
    }

    /**
     * 
     * @param mapModelEntry
     *            {@link MapModelEntry} to be inserted
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     * @param first
     *            if true layer will be inserted as first layer of a group if antecessor == null
     * @throws Exception
     */
    public void insert( final MapModelEntry mapModelEntry, LayerGroup parent, MapModelEntry antecessor, boolean first )
                            throws Exception {
        // check if layer already exists in map model
        walkLayerTree( new MapModelVisitor() {

            public void visit( MMLayer layer )
                                    throws Exception {
                if ( layer.getIdentifier().equals( mapModelEntry.getIdentifier() ) ) {
                    throw new PortalException( "layer: " + layer.getTitle() + " already contained in tree" );
                }
            }

            public void visit( LayerGroup layerGroup )
                                    throws Exception {
                if ( layerGroup.getIdentifier().equals( mapModelEntry.getIdentifier() ) ) {
                    throw new PortalException( "layergroup: " + layerGroup.getTitle() + " already contained in tree" );
                }
            }

        } );

        if ( mapModelEntry instanceof MMLayer ) {
            insertLayer( (MMLayer) mapModelEntry, parent, antecessor, first );
        } else if ( mapModelEntry instanceof LayerGroup ) {
            insertLayerGroup( (LayerGroup) mapModelEntry, parent, antecessor, first );
        }
    }

    /**
     * 
     * @param layer
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     * @param first
     *            if true layer will be inserted as first layer of a group if antecessor == null
     */
    private void insertLayer( MMLayer layer, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        /*
         * if ( parent == null ) { int i = 0;
         * 
         * if ( layerGroups.size() > 0 ) { while ( i < layerGroups.size() && !layerGroups.get( i ).equals( antecessor )
         * ) { i++; } } if ( i >= layerGroups.size() - 1 ) { if ( first && antecessor == null ) { layerGroups.add( 0,
         * layer ); } else { layerGroups.add( layer ); } } else { if ( first && antecessor == null ) { layerGroups.add(
         * 0, layer ); } else { layerGroups.add( i + 1, layer ); } } } else {
         */
        boolean insertedAsChildLayer = insertLayerInChildLayer( layer, parent, antecessor, mapModelEntries, first );
        if ( !insertedAsChildLayer ) {
            int index = mapModelEntries.indexOf( antecessor ) + 1;
            mapModelEntries.add( index, layer );
        }
        // }
    }

    private boolean insertLayerInChildLayer( MMLayer layer, LayerGroup parent, MapModelEntry antecessor,
                                             List<MapModelEntry> lgs, boolean first ) {
        for ( MapModelEntry mapModelEntry : lgs ) {
            if ( mapModelEntry instanceof LayerGroup ) {
                LayerGroup layerGroup = (LayerGroup) mapModelEntry;
                if ( parent != null && parent.equals( mapModelEntry ) ) {
                    layerGroup.insert( layer, antecessor, first );
                    return true;
                } else {
                    boolean wasInserted = insertLayerInChildLayer( layer, parent, antecessor,
                                                                   layerGroup.getMapModelEntries(), first );
                    if ( wasInserted )
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param layerGroup
     * @param parent
     *            if <code>null</code> root node of layer tree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     * @param first
     *            if true layer will be inserted as first layer group of a group if antecessor == null
     */
    private void insertLayerGroup( LayerGroup layerGroup, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        if ( parent == null ) {
            int i = 0;
            if ( mapModelEntries.size() > 0 ) {
                while ( i < mapModelEntries.size() && !mapModelEntries.get( i ).equals( antecessor ) ) {
                    i++;
                }
            }
            if ( i >= mapModelEntries.size() - 1 ) {
                if ( first && antecessor == null ) {
                    mapModelEntries.add( 0, layerGroup );
                } else {
                    mapModelEntries.add( layerGroup );
                }
            } else {
                if ( first && antecessor == null ) {
                    mapModelEntries.add( 0, layerGroup );
                } else {
                    mapModelEntries.add( i + 1, layerGroup );
                }
            }
        } else {
            insertLayerGroup( layerGroup, parent, antecessor, mapModelEntries, first );
        }
    }

    private void insertLayerGroup( LayerGroup lg, LayerGroup parent, MapModelEntry antecessor, List<MapModelEntry> lgs,
                                   boolean first ) {
        for ( MapModelEntry mapModelEntry : lgs ) {
            if ( mapModelEntry instanceof LayerGroup ) {
                LayerGroup layerGroup = (LayerGroup) mapModelEntry;
                if ( parent != null && parent.equals( layerGroup ) ) {
                    layerGroup.insert( lg, antecessor, first );
                    break;
                } else {
                    insertLayerGroup( lg, parent, antecessor, layerGroup.getMapModelEntries(), first );
                }
            } else if ( mapModelEntry instanceof MMLayer ) {
                // TODO
            }
        }
    }

    /**
     * moves the passed layer underneath a new parent and before the passed antecessor.
     * 
     * @param mapModelEntry
     * @param parent
     *            if <code>null</code> root node of layer tree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     * @param first
     */
    public void move( MapModelEntry mapModelEntry, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        if ( mapModelEntry instanceof MMLayer ) {
            move( (MMLayer) mapModelEntry, parent, antecessor, first );
        } else if ( mapModelEntry instanceof LayerGroup ) {
            move( (LayerGroup) mapModelEntry, parent, antecessor, first );
        }
    }

    /**
     * moves the passed layer underneath a new parent and before the passed antecessor.
     * 
     * @param layer
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     * @param first
     *            if true layer will be inserted as first layer of a group if antecessor == null
     */
    private void move( MMLayer layer, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        if ( !layer.equals( antecessor ) ) {
            if ( layer.getParent() != null ) {
                layer.getParent().removeLayer( layer );
                insertLayer( layer, parent, antecessor, first );
            } else {
                mapModelEntries.remove( layer );
                insertLayer( layer, parent, antecessor, first );
            }
        }
    }

    /**
     * moves the passed layergroup underneath a new parent and before the passed antecessor.
     * 
     * @param layerGroup
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layergroup will be inserted directly underneath its parent
     */
    private void move( LayerGroup layerGroup, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        if ( !layerGroup.equals( antecessor ) ) {
            if ( layerGroup.getParent() != null ) {
                layerGroup.getParent().removeLayerGroup( layerGroup );
                if ( parent == null ) {
                    insertLayerGroup( layerGroup, parent, antecessor, first );
                } else {
                    insertLayerGroup( layerGroup, parent, antecessor, mapModelEntries, first );
                }
            } else {
                mapModelEntries.remove( layerGroup );
                insertLayerGroup( layerGroup, parent, antecessor, first );
            }
        }
    }

    /**
     * 
     * @param visitor
     * @throws Exception
     */
    public void walkLayerTree( MapModelVisitor visitor )
                            throws Exception {
        for ( MapModelEntry layerGroup : mapModelEntries ) {
            applyVisitor( layerGroup, visitor );
        }
    }

    private void applyVisitor( MapModelEntry mapModelEntry, MapModelVisitor visitor )
                            throws Exception {
        if ( mapModelEntry instanceof LayerGroup )
            applyVisitor( (LayerGroup) mapModelEntry, visitor );
        if ( mapModelEntry instanceof MMLayer )
            visitor.visit( (MMLayer) mapModelEntry );
    }

    private void applyVisitor( LayerGroup layerGroup, MapModelVisitor visitor )
                            throws Exception {
        visitor.visit( layerGroup );
        List<MapModelEntry> entries = layerGroup.getMapModelEntries();
        for ( MapModelEntry entry : entries ) {
            if ( entry instanceof MMLayer ) {
                visitor.visit( (MMLayer) entry );
            } else {
                applyVisitor( (LayerGroup) entry, visitor );
            }
        }
    }

    /**
     * 
     * @param identifier
     * @return {@link MapModelEntry} matching passed identifier
     */
    public MapModelEntry getMapModelEntryByIdentifier( final String identifier ) {
        final List<MapModelEntry> list = new ArrayList<MapModelEntry>();
        try {
            walkLayerTree( new MapModelVisitor() {

                public void visit( LayerGroup layerGroup )
                                        throws Exception {
                    if ( identifier.equals( layerGroup.getIdentifier() ) ) {
                        list.add( layerGroup );
                    }

                }

                public void visit( MMLayer layer )
                                        throws Exception {
                    if ( identifier.equals( layer.getIdentifier() ) ) {
                        list.add( layer );
                    }

                }
            } );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        if ( list.size() > 0 ) {
            return list.get( 0 );
        } else {
            return null;
        }
    }

    /**
     * 
     * @param mapModelEntry
     *            {@link MapModelEntry} to remove
     */
    public void remove( MapModelEntry mapModelEntry ) {
        if ( mapModelEntry instanceof MMLayer ) {
            LayerGroup parent = ( (MMLayer) mapModelEntry ).getParent();
            if ( parent != null )
                parent.removeLayer( (MMLayer) mapModelEntry );
            else
                mapModelEntries.remove( mapModelEntry );
        } else if ( mapModelEntry instanceof LayerGroup ) {
            LayerGroup layerGroup = (LayerGroup) mapModelEntry;
            if ( layerGroup.getParent() != null ) {
                layerGroup.getParent().removeLayerGroup( layerGroup );
            } else {
                mapModelEntries.remove( layerGroup );
            }
        }
    }
}
