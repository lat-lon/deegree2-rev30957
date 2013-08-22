//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/model/metadata/iso19115/RoleCode.java $
/*
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

package org.deegree.model.metadata.iso19115;

/**
 *
 *
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 */
public class RoleCode {

    String value = null;

    /**
     * Creates a new instance of RoleCode
     *
     * @param value
     */
    public RoleCode( String value ) {
        setValue( value );
    }

    /**
     * returns the value-attribute. use="required". Possible value-values are:
     * <ul>
     * <li>contentProvider
     * <li>custodianSteward
     * <li>owner
     * <li>user
     * <li>distributor
     * <li>metadataProvider
     * <li>originator
     * <li>pointOfContact
     * <li>principalInvestigator
     * <li>processor
     * <li>publisher
     * </ul>
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * @see #getValue()
     * @param value
     */
    public void setValue( String value ) {
        this.value = value;
    }

    /**
     * to String method
     *
     * @return string representation
     */
    public String toString() {
        String ret = null;
        ret = "value = " + value + "\n";
        return ret;
    }

}
