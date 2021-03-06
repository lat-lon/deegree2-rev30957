//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/io/shpapi/SHPPoint3D.java $
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
package org.deegree.io.shpapi;

import org.deegree.model.spatialschema.ByteUtils;
import org.deegree.model.spatialschema.Position;

/**
 *
 *
 *
 * @version $Revision: 18195 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.0. $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 2.0
 */
public class SHPPoint3D extends SHPPoint {

    /**
     *
     */
    public double z;

    /**
     *
     */
    public SHPPoint3D() {
        super();
    }

    /**
     * @param recBuf
     * @param xStart
     * @param numPoints
     */
    public SHPPoint3D( byte[] recBuf, int xStart, int numPoints ) {
        super( recBuf, xStart );
        // TODO is it possible to read z values w/o telling the no of points?

        this.z = ByteUtils.readLEDouble( recBuffer, xStart + 16 * numPoints );
        // System.out.println(x + ", " + y +" _ " + z);

    }

    /**
     * @param point
     */
    public SHPPoint3D( Position point ) {
        super( point );
        this.z = point.getZ();
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public SHPPoint3D( double x, double y, double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "SHPPOINT" + "[" + this.x + "; " + this.y + "; " + this.z + "]";
    }

}
