//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/context/AuthentificationSettings.java $
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
package org.deegree.portal.context;

import org.deegree.ogcbase.BaseURL;

/**
 * This exception shall be thrown when a session(ID) will be used that has been expired.
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.1, $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 1.1
 */
public class AuthentificationSettings {

    private BaseURL authentificationURL = null;

    /**
     * @param authentificationURL
     */
    public AuthentificationSettings( BaseURL authentificationURL ) {
        this.authentificationURL = authentificationURL;
    }

    /**
     * @return the url where to get an authentication.
     */
    public BaseURL getAuthentificationURL() {
        return authentificationURL;
    }

    /**
     * @param authenticationURL to set.
     */
    public void setAuthentificationURL( BaseURL authenticationURL ) {
        this.authentificationURL = authenticationURL;
    }
}
