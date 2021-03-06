//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/wps/describeprocess/SupportedCRSs.java $
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
package org.deegree.ogcwebservices.wps.describeprocess;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * SupportedCRSs.java
 *
 * Created on 09.03.2006. 22:54:24h
 *
 * List of supported Coordinate Reference Systems.
 *
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 */
public class SupportedCRSs {

    /**
     * Unordered list of references to the coordinate reference systems supported. This element
     * shall not be included if there is only one (i.e., the default) CRS supported.
     */
    protected List<URI> crs;

    /**
     * Reference to the CRS that will be used unless the Execute operation request specifies another
     * supported CRS.
     */
    protected URI defaultCRS;

    /**
     * @param crs
     * @param defaultCRS
     */
    public SupportedCRSs( List<URI> crs, URI defaultCRS ) {
        this.crs = crs;
        this.defaultCRS = defaultCRS;
    }

    /**
     *
     * @return CRS
     */
    public List<URI> getCRS() {
        if ( crs == null ) {
            crs = new ArrayList<URI>();
        }
        return this.crs;
    }

    /**
     * @return Returns the defaultCRS.
     */
    public URI getDefaultCRS() {
        return defaultCRS;
    }

    /**
     * @param defaultCRS
     *            The defaultCRS to set.
     */
    public void setDefaultCRS( URI defaultCRS ) {
        this.defaultCRS = defaultCRS;
    }

}
