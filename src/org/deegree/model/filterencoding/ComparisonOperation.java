//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/model/filterencoding/ComparisonOperation.java $
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
package org.deegree.model.filterencoding;

import org.w3c.dom.Element;

/**
 * Encapsulates the information of a comparison_ops entity (as defined in the Filter DTD).
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: apoth $
 *
 * @version $Revision: 29963 $, $Date: 2011-03-09 15:01:36 +0100 (Mi, 09. Mär 2011) $
 */
public abstract class ComparisonOperation extends AbstractOperation {

    ComparisonOperation( int operationId ) {
        super( operationId );
    }

    

    /**
     * Given a DOM-fragment, a corresponding Operation-object is built. This method recursively
     * calls other buildFromDOM () - methods to validate the structure of the DOM-fragment.
     *
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    public static Operation buildFromDOM( Element element, boolean useVersion_1_0_0 )
                            throws FilterConstructionException {

        // check if root element's name is a known operator
        String name = element.getLocalName();
        int operatorId = OperationDefines.getIdByName( name );
        ComparisonOperation operation = null;

        switch ( operatorId ) {
        case OperationDefines.PROPERTYISEQUALTO:
        case OperationDefines.PROPERTYISNOTEQUALTO:
        case OperationDefines.PROPERTYISLESSTHAN:
        case OperationDefines.PROPERTYISGREATERTHAN:
        case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
        case OperationDefines.PROPERTYISGREATERTHANOREQUALTO: {
            operation = (ComparisonOperation) PropertyIsCOMPOperation.buildFromDOM( element );
            break;
        }
        case OperationDefines.PROPERTYISLIKE: {
            operation = (ComparisonOperation) PropertyIsLikeOperation.buildFromDOM( element );
            break;
        }
        case OperationDefines.PROPERTYISNULL: {
            operation = (ComparisonOperation) PropertyIsNullOperation.buildFromDOM( element, useVersion_1_0_0 );
            break;
        }
        case OperationDefines.PROPERTYISBETWEEN: {
            operation = (ComparisonOperation) PropertyIsBetweenOperation.buildFromDOM( element );
            break;
        }
        case OperationDefines.PROPERTYISINSTANCEOF: {
            operation = (ComparisonOperation) PropertyIsInstanceOfOperation.buildFromDOM( element );
            break;
        }
        default: {
            throw new FilterConstructionException( "'" + name + "' is not a comparison operator!" );
        }
        }
        return operation;
    }
}
