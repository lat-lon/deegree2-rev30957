//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/model/spatialschema/BoundaryImpl.java $
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
package org.deegree.model.spatialschema;

import java.io.Serializable;

import org.deegree.model.crs.CoordinateSystem;

/**
 * default implementation of the Boundary interface. The class is abstract because there isn't a boundary without a
 * geometry type. Concrete implementations are <tt>CurveBoundary</tt> or <tt>SurfaceBoundary</tt> for example.
 *
 * <p>
 * ------------------------------------------------------------
 * </p>
 *
 * @version 5.6.2001
 * @author Andreas Poth
 *         <p>
 */
public abstract class BoundaryImpl extends GeometryImpl implements Boundary, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -6057663115928108209L;

    /**
     * @param crs
     *            the reference system
     */
    protected BoundaryImpl( CoordinateSystem crs ) {
        super( crs );
    }

    public boolean isCycle() {
        return true;
    }
}
