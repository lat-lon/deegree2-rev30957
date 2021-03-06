// $HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/VersionNegotiationFailedException.java $
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
package org.deegree.ogcwebservices;

import org.deegree.ogcbase.ExceptionCode;

/**
 * List of versions in �AcceptVersions� parameter value in GetCapabilities operation request did not
 * include any version supported by this server.
 * <p>
 * locator value = None, omit �locator� parameter
 *
 * @version $Revision: 26213 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 *
 * @version 1.0. $Revision: 26213 $, $Date: 2010-08-27 14:01:04 +0200 (Fr, 27. Aug 2010) $
 *
 * @since 2.0
 */
public class VersionNegotiationFailedException extends OGCWebServiceException {


    private static final long serialVersionUID = -4628696763712274617L;

    /**
     * @param message
     */
    public VersionNegotiationFailedException( String message ) {
        super( message );
        this.code = ExceptionCode.VERSIONNEGOTIATIONFAILED;
    }

    /**
     * @param locator
     * @param message
     */
    public VersionNegotiationFailedException( String locator, String message ) {
        super( locator, message );
        this.code = ExceptionCode.VERSIONNEGOTIATIONFAILED;
    }

    /**
     * @param locator
     * @param message
     * @param code
     */
    public VersionNegotiationFailedException( String locator, String message, ExceptionCode code ) {
        super( locator, message, code );
    }

}
