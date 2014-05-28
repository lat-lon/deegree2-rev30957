//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/context/MMLayer.java $
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


/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version $Revision: 20973 $, $Date: 2009-11-23 16:54:36 +0100 (Mo, 23. Nov 2009) $
 */
public class MMLayer extends MapModelEntry {

    private Layer layer;

    /**
     * @param identifier
     * @param parent
     * @param owner
     * @param layer
     */
    public MMLayer( String identifier, LayerGroup parent, MapModel owner, Layer layer ) {
        super( identifier, null, true, parent, owner );
        this.layer = layer;
    }

    /**
     * @return the layer
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * @param layer
     */
    void setLayer( Layer layer ) {
        this.layer = layer;
    }

    @Override
    public String getTitle() {
        return layer.getTitle();
    }
    
    @Override
    public void setTitle( String title ) {
        try {
            layer.setTitle( title );
        } catch ( ContextException e ) {
            // only thrown if title is null...
        }
    }

    @Override
    public boolean isHidden() {
        return layer.isHidden();
    }

    @Override
    public void setHidden( boolean hidden ) {
        super.setHidden( hidden );
        layer.setHidden( hidden );
    }
}
