//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/security/UnauthorizedException.java $
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
package org.deegree.security;

import org.deegree.framework.util.StringTools;

/**
 * Marks that the requested operation is not permitted.
 * <p>
 *
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 18195 $
 */
public class UnauthorizedException extends GeneralSecurityException {

    private static final long serialVersionUID = -6320460964082230120L;
    /**
     *
     */
    String message = null;

    /**
     *
     */
    public UnauthorizedException() {
        super();
    }

    /**
     * @param message
     */
    public UnauthorizedException( String message ) {
        this.message = message;
    }

    /**
     * @param message
     * @param arg1
     */
    public UnauthorizedException( String message, Throwable arg1 ) {
        super( message, arg1 );
        this.message = message + StringTools.stackTraceToString( arg1 );
    }

    /**
     * @param arg0
     */
    public UnauthorizedException( Throwable arg0 ) {
        super( StringTools.stackTraceToString( arg0 ) );
    }

    /**
     * @return message
     */
    public String getMessage() {
        return message;
    }

}
