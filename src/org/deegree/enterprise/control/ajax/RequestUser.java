//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/enterprise/control/ajax/RequestUser.java $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2008 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53115 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de


 ---------------------------------------------------------------------------*/

// $Id: RequestUser.java 20969 2009-11-23 15:51:29Z apoth $
package org.deegree.enterprise.control.ajax;

// JDK 1.3
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

/**
 * Encapsulates all client information.
 * <P>
 * 
 * @author <a href="mailto:friebe@gmx.net">Torsten Friebe</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @version $Revision: 20969 $ $Date: 2009-11-23 16:51:29 +0100 (Mo, 23. Nov 2009) $
 */
class RequestUser {
    private Properties userData;

    /**
     * Creates an empty object.
     */
    protected RequestUser() {
        this.userData = new Properties();
    }

    /**
     * Creates a request user object with client information retrieved out of the request object.
     * 
     * @param request
     *            the request object containing user and client data
     */
    public RequestUser( HttpServletRequest request ) {
        this.userData = new Properties();
        this.parseRequest( request );
    }

    /**
     * 
     * @return Remote user
     */
    public String getRemoteUser() {
        return this.userData.getProperty( "RemoteUser" );
    }

    /**
     * @return Remote address
     */
    public String getRemoteAddr() {
        return this.userData.getProperty( "RemoteAddr" );
    }

    /**
     * @return Remote host
     */
    public String getRemoteHost() {
        return this.userData.getProperty( "RemoteHost" );
    }

    /**
     * @return Authorization scheme
     */
    public String getAuthType() {
        return this.userData.getProperty( "AuthType" );
    }

    /**
     * @return the user principal
     */
    public String getUserPrincipal() {
        Object _obj = userData.get( "UserPrincipal" );

        if ( _obj instanceof java.security.Principal ) {
            java.security.Principal _principal = (java.security.Principal) _obj;
            return _principal.getName();
        } else if ( _obj instanceof String ) {
            return (String) _obj;
        }

        return _obj.toString();
    }

    /**
     * Parse request object for user specific attributes.
     * 
     * @param request
     *            to parse
     */
    protected void parseRequest( HttpServletRequest request ) {
        try {
            this.userData.setProperty( "RemoteUser", (String) getRequestValue( request, "getRemoteUser", "[unknown]" ) );

            this.userData.setProperty( "RemoteAddr", (String) getRequestValue( request, "getRemoteAddr", "[unknown]" ) );

            this.userData.setProperty( "RemoteHost", (String) getRequestValue( request, "getRemoteHost", "[unknown]" ) );

            this.userData.setProperty( "AuthType", (String) getRequestValue( request, "getAuthType", "[unknown]" ) );

            this.userData.put( "UserPrincipal", getRequestValue( request, "getUserPrincipal", "[unknown]" ) );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * 
     * 
     * @param request
     * @param methodName
     * @param defaultValue
     * 
     * @return the value of a request or if the request is null, the methodName is null or empty, <code>null</code>
     *         will be returned.
     * 
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected Object getRequestValue( HttpServletRequest request, String methodName, Object defaultValue )
                            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if ( ( request != null ) && ( methodName != null ) && !methodName.equals( "" ) ) {
            // System.err.println( "looking for :" + methodName );
            // use refection for method
            Method _objmethod = request.getClass().getMethod( methodName, new Class[] {} );

            // System.err.println( "got :" + _objmethod.getName() );
            // get the result of the method invocation
            Object _result = _objmethod.invoke( request, new Object[] {} );

            // System.err.println( "returns :" + _result );
            if ( _result != null ) {
                return _result;
            }
            return defaultValue;
        }

        return null;
    }
}