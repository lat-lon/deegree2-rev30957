//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/csw/AbstractCSWRequest.java $
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
package org.deegree.ogcwebservices.csw;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.deegree.framework.util.StringTools;
import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.InvalidParameterValueException;

/**
 * Abstract base class for requests to catalogue services (CSW).
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 *
 * @author last edited by: $Author: mschneider $
 *
 * @version 2.0, $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 2.0
 */
public class AbstractCSWRequest extends AbstractOGCWebServiceRequest {

    private static final long serialVersionUID = 726077635012162899L;

    /**
     * @param version
     * @param id
     * @param vendorSpecificParameter
     */
    public AbstractCSWRequest( String version, String id, Map<String, String> vendorSpecificParameter ) {
        super( version, id, vendorSpecificParameter );
    }

    /**
     * returns 'CSW' as service name
     */
    public String getServiceName() {
        return "CSW";
    }

    /**
     * Extracts the namespace-mappings from the given parameter as specified for the
     * NAMESPACE-parameter in the KVP-encoding.
     * <p>
     * Please note that the expected syntax of the CSW NAMESPACE parameter differs from the
     * NAMESPACE parameter used in the WFS specification.
     *
     * @param nsString
     *            contains a list of [prefix:]uri-entries, the entries of the list are separated by
     *            the ','-character
     * @return keys are Strings (prefixes), values are URIs
     * @throws InvalidParameterValueException
     */
    protected static Map<String, URI> getNSMappings( String nsString )
                            throws InvalidParameterValueException {
        Map<String, URI> map = new HashMap<String, URI>();
        if ( nsString != null ) {
            String[] mappings = StringTools.toArray( nsString, ",", false );
            for ( int i = 0; i < mappings.length; i++ ) {
                int idx = mappings[i].indexOf( ":" );
                String prefix = "";
                String value = null;
                if ( idx == -1 ) {
                    value = mappings[i];
                } else {
                    prefix = mappings[i].substring( 0, idx );
                    if ( idx == mappings[i].length() - 1 ) {
                        throw new InvalidParameterValueException( "Value '" + nsString
                                                                  + "' for parameter NAMESPACE is invalid: Prefix "
                                                                  + prefix + " is mapped to an empty namespace." );
                    }
                    value = mappings[i].substring( idx + 1, mappings[i].length() );
                }
                URI uri = null;
                try {
                    uri = new URI( value );
                } catch ( URISyntaxException e ) {
                    throw new InvalidParameterValueException( "Value '" + nsString
                                                              + "' for parameter NAMESPACE is invalid: Prefix "
                                                              + prefix + " is not mapped to a valid URI." );
                }
                map.put( prefix, uri );
            }
        }
        return map;
    }
}
