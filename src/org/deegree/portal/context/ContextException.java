//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/context/ContextException.java $
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


/**
 *
 * <code>ContextException</code> an exception.
 *
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 *
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 */
public class ContextException extends java.lang.Exception {

    /**
     *
     */
    private static final long serialVersionUID = 6967412678689311109L;
    private String st = "ContextException";

    /**
     * Creates a new instance of <code>CatalogClientException</code> without detail message.
     */
    public ContextException() {
        //nottin.
    }

    /**
     * Constructs an instance of <code>CatalogClientException</code> with the specified detail
     * message.
     *
     * @param msg
     *            the detail message.
     */
    public ContextException( String msg ) {
        super( msg );
    }

    /**
     * Constructs an instance of <code>CatalogClientException</code> with the specified detail
     * message.
     *
     * @param msg
     *            the detail message.
     * @param e which caused the exception.
     */
    public ContextException( String msg, Exception e ) {
        super( msg, e );
        //st = StringTools.stackTraceToString( e.getStackTrace() );
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + st;
    }

}
