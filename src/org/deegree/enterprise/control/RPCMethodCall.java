//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/enterprise/control/RPCMethodCall.java $
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
package org.deegree.enterprise.control;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 18195 $ $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 */

public class RPCMethodCall {

    private String methodeName = null;

    private RPCParameter[] parameters = null;

    /**
     *
     * @param methodeName
     *            name of the remote procedure
     * @param parameters
     *            parameters that are passed to the remote procedure
     */
    RPCMethodCall( String methodeName, RPCParameter[] parameters ) {
        this.methodeName = methodeName;
        this.parameters = parameters;
    }

    /**
     * returns the name of the remote procedure
     *
     * @return name of the remote procedure
     */
    public String getMethodName() {
        return methodeName;
    }

    /**
     * returns an array of <tt>RPCParameter<tt> parameter that are passed
     * to the remote procedure. The parameters are returned in the same order
     * as they are passed to the remote procedure
     *
     * @return parameters that are passed to the remote procedure
     */
    public RPCParameter[] getParameters() {
        return parameters;
    }

}
