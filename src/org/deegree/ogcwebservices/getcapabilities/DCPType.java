//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/getcapabilities/DCPType.java $
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

import java.io.Serializable;

/**
 * Available Distributed Computing Platforms (DCPs) are listed here. At present, only HTTP is
 * defined.
 *
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 18195 $ $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 */

public class DCPType implements Serializable {

    private static final long serialVersionUID = 7197702738555576137L;

    private Protocol protocol = null;

    /**
     * default constructor
     */
    public DCPType() {
        // why?
    }

    /**
     * constructor initializing the class with the protocol
     *
     * @param protocol
     *            the used protocol
     */
    public DCPType( Protocol protocol ) {
        setProtocol( protocol );
    }

    /**
     * returns the protocol of the available Distributed Computing Platforms (DCPs)
     *
     * @return the protocol of the available Distributed Computing Platforms (DCPs)
     *
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * sets the protocol of the available Distributed Computing Platforms (DCPs)
     *
     * @param protocol
     *
     */
    public void setProtocol( Protocol protocol ) {
        this.protocol = protocol;
    }

}
