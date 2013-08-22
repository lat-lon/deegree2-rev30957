//$HeadURL: $
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

package org.deegree.ogcwebservices.wcts.operation;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.i18n.Messages;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wcts.WCTService;
import org.w3c.dom.Element;

/**
 * <code>WCTSRequestBaseDocument</code> supplies the pasreVersion and parseService methods which are mandatory for all
 * WCTSRequests.
 *
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 *
 * @author last edited by: $Author:$
 *
 * @version $Revision:$, $Date:$
 *
 */
public class WCTSRequestBaseDocument extends XMLFragment {

    private static final long serialVersionUID = 8095321883665451630L;

    /**
     * The default prefix of the wcts namespace.
     */
    protected static final String PRE = CommonNamespaces.WCTS_PREFIX + ":";

    /**
     * @param rootElement of this request.
     * @throws IllegalArgumentException
     *             if the rootElement is <code>null</code>
     */
    public WCTSRequestBaseDocument( Element rootElement ) throws IllegalArgumentException {
        if ( rootElement == null ) {
            throw new IllegalArgumentException( Messages.getMessage( "WCTS_ROOT_ELEMENT_NOT_SET" ) );
        }
        setRootElement( rootElement );
    }

    /**
     * @return the mandatory service string.
     * @throws OGCWebServiceException
     *             with code INVALIDPARAMETERVALUE, if the attribute was not given.
     */
    public String parseService()
                                throws OGCWebServiceException {
        Element root = getRootElement();
        String result = new String();
        if ( root != null ) {
            result = root.getAttribute( "service" );
            if ( result == null || "".equals( result.trim() ) ) {
                throw new OGCWebServiceException( Messages.getMessage( "WCTS_ILLEGAL_SERVICE" ),
                                                  ExceptionCode.INVALIDPARAMETERVALUE );
            }
        }

        if ( !"WCTS".equalsIgnoreCase( result ) ) {
            throw new OGCWebServiceException( Messages.getMessage( "WCTS_ILLEGAL_SERVICE" ),
                                              ExceptionCode.INVALIDPARAMETERVALUE );
        }
        return result;

    }

    /**
     * @return the mandatory version string.
     * @throws OGCWebServiceException
     *             with code INVALIDPARAMETERVALUE, if the attribute was not given.
     */
    public String parseVersion()
                                throws OGCWebServiceException {
        Element root = getRootElement();
        String result = new String();
        if ( root != null ) {
            result = root.getAttribute( "version" );
            if ( result == null || "".equals( result.trim() ) ) {
                throw new OGCWebServiceException( Messages.getMessage( "WCTS_ILLEGAL_VERSION", WCTService.version ),
                                                  ExceptionCode.INVALIDPARAMETERVALUE );
            }
        }

        if ( !WCTService.version.equalsIgnoreCase( result ) ) {
            throw new OGCWebServiceException( Messages.getMessage( "WCTS_ILLEGAL_VERSION", WCTService.version ),
                                              ExceptionCode.INVALIDPARAMETERVALUE );
        }
        return result;

    }
}
