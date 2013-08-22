//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/security/owsrequestvalidator/GeneralPolicyValidator.java $
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
package org.deegree.security.owsrequestvalidator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.deegree.framework.util.StringTools;
import org.deegree.i18n.Messages;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.OperationParameter;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 */

public class GeneralPolicyValidator {

    // known condition parameter
    private static final String GETCONTENTLENGTH = "getContentLength";

    private static final String POSTCONTENTLENGTH = "postContentLength";

    private static final String HTTPHEADER = "httpHeader";

    private static final String REQUESTTYPE = "requestType";

    // message strings
    // TODO: read from resource bundle
    private static final String contentLengthMESSAGE1 = "contentLength condition isn't defined";

    private static final String contentLengthMESSAGE2 = "contentLength exceeds defined maximum length";

    private Condition generalCondition = null;

    /**
     * @param generalCondition
     */
    public GeneralPolicyValidator( Condition generalCondition ) {
        this.generalCondition = generalCondition;
    }

    /**
     * validates if the passed length of a request content doesn't exceeds the defined maximum length. If the
     * OperationParameter indicates that the condition is coupled to specific user rights, these rights will be read
     * from the rights management system
     *
     * @param contentLength
     * @throws InvalidParameterValueException
     *
     */
    public void validateGetContentLength( int contentLength )
                            throws InvalidParameterValueException {

        OperationParameter op = generalCondition.getOperationParameter( GETCONTENTLENGTH );
        if ( op == null ) {
            // if no policy for a value is defined the condition
            // never will be fullfilled --> rights are granted not limited
            throw new InvalidParameterValueException( contentLengthMESSAGE1 );
        }

        if ( op.isAny() ) {
            return;
        }

        int compareValue = op.getFirstAsInt();
        if ( op.isUserCoupled() ) {
            // TODO
            // get compareValue from the rights management system
        }
        if ( compareValue < contentLength ) {
            throw new InvalidParameterValueException( contentLengthMESSAGE2 );
        }
    }

    /**
     * validates if the passed length of a request content doesn't exceeds the defined maximum length. If the
     * OperationParameter indicates that the condition is coupled to specific user rights, these rights will be read
     * from the rights management system
     *
     * @param contentLength
     * @throws InvalidParameterValueException
     */
    public void validatePostContentLength( int contentLength )
                            throws InvalidParameterValueException {
        OperationParameter op = generalCondition.getOperationParameter( POSTCONTENTLENGTH );
        if ( op == null ) {
            // if no policy for a value is defined the condition
            // never will be fulfilled --> rights are granted not limited
            throw new InvalidParameterValueException( contentLengthMESSAGE1 );
        }

        if ( op.isAny() ) {
            return;
        }

        int compareValue = op.getFirstAsInt();
        if ( op.isUserCoupled() ) {
            // TODO
            // get compareValue from the rights management system
        }
        if ( compareValue < contentLength ) {
            throw new InvalidParameterValueException( contentLengthMESSAGE2 + ": " + contentLength );
        }
    }

    /**
     * @param headerFields
     * @param user
     * @throws InvalidParameterValueException
     * @throws UnauthorizedException
     */
    public void validateHeader( Map<String, Object> headerFields, User user )
                            throws InvalidParameterValueException, UnauthorizedException {
        OperationParameter op = generalCondition.getOperationParameter( HTTPHEADER );
        if ( op == null ) {
            // if no policy for a value is defined the condition
            // never will be fullfilled --> rights are granted, not limited
            throw new InvalidParameterValueException( contentLengthMESSAGE1 );
        }

        if ( op.isUserCoupled() && user == null ) {
            String s = Messages.getMessage( "OWSPROXY_NO_ANONYMOUS_ACCESS" );
            throw new UnauthorizedException( s );
        }
        // TODO

    }

    /**
     * validates if the current request type (e.g. POST, GET ...) is granted to be performed
     *
     * @param type
     * @throws InvalidParameterValueException
     */
    public void validateRequestMethod( String type )
                            throws InvalidParameterValueException {
        OperationParameter op = generalCondition.getOperationParameter( REQUESTTYPE );
        if ( op == null ) {
            // if no policy for a value is defined the condition
            // never will be fullfilled --> rights are granted not limited
            throw new InvalidParameterValueException( contentLengthMESSAGE1 );
        }

        if ( op.isAny() ) {
            return;
        }

        String[] tmp = StringTools.toArray( op.getFirstAsString(), ",", true );
        List<String> compareValue = Arrays.asList( tmp );
        if ( op.isUserCoupled() ) {
            // TODO
            // get compareValue from the rights management system
        }
        if ( !compareValue.contains( type ) ) {
            throw new InvalidParameterValueException( contentLengthMESSAGE2 );
        }
    }

}
