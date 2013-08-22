//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/tools/raster/Tile.java $
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

package org.deegree.tools.raster;

import org.deegree.model.spatialschema.Envelope;

/**
 * This class represents a <code>Tile</code> object, used to hold all information that is needed,
 * when drawing an image onto a tile in <code>MergeRaster</code>.
 *
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 */
class Tile {

    private Envelope tileEnvelope;

    private String postfix;

    /**
     * @param env
     *            The Envelope of the tile.
     * @param postfix
     *            Postfix of the tile file name (e.g. _3_5).
     */
    public Tile( Envelope env, String postfix ) {
        this.tileEnvelope = env;
        this.postfix = postfix;
    }

    /**
     * @return Returns the tileEnvelope.
     */
    public Envelope getTileEnvelope() {
        return tileEnvelope;
    }

    /**
     * @param tileEnvelope
     *            The tileEnvelope to set.
     */
    public void setTileEnvelope( Envelope tileEnvelope ) {
        this.tileEnvelope = tileEnvelope;
    }

    /**
     * @return Returns the postfix.
     */
    public String getPostfix() {
        return postfix;
    }

    /**
     * @param postfix
     *            The postfix to set.
     */
    public void setPostfix( String postfix ) {
        this.postfix = postfix;
    }

}
