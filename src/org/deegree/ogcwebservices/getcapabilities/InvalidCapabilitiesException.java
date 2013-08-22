// $HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/getcapabilities/InvalidCapabilitiesException.java $
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
package org.deegree.ogcwebservices.getcapabilities;

import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.OGCWebServiceException;

/**
 *
 *
 * @version $Revision: 18195 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.0. $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 1.1
 */
public class InvalidCapabilitiesException extends OGCWebServiceException {

    private static final long serialVersionUID = -9112338680077668980L;

    /**
     *
     */
    public InvalidCapabilitiesException() {
        super( "Invalid OWS capabilities excepetion" );
    }

    /**
     * @param message
     */
    public InvalidCapabilitiesException( String message ) {
        super( message );
    }

    /**
     * @param locator
     * @param message
     */
    public InvalidCapabilitiesException( String locator, String message ) {
        super( locator, message );
    }

    /**
     * @param locator
     * @param message
     * @param code
     */
    public InvalidCapabilitiesException( String locator, String message, ExceptionCode code ) {
        super( locator, message, code );
    }

    /**
     * @param message
     * @param code
     */
    public InvalidCapabilitiesException( String message, ExceptionCode code ) {
        super( message, code );
    }

}
