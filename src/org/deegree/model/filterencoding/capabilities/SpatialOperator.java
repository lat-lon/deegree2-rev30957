// $HeadURL:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/getcapabilities/Contents.java,v
// 1.1 2004/06/23 11:55:40 mschneider Exp $
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
package org.deegree.model.filterencoding.capabilities;

import org.deegree.datatypes.QualifiedName;

/**
 * SpatialOperatorBean
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 *
 * @author last edited by: $Author: mschneider $
 *
 * @version 2.0, $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 2.0
 */
public class SpatialOperator extends Operator {

    QualifiedName[] geometryOperands;

    /**
     * Constructs a new <code>SpatialOperator</code> instance with the given
     * name. Especially used for creating an instance from an "OpenGIS Filter
     * Encoding Specification 1.0.0" compliant representation.
     *
     * @param name
     */
    public SpatialOperator(String name) {
        super(name);
    }

    /**
     * Constructs a new <code>SpatialOperator</code> instance with the given
     * name and operands.
     * Especially used for creating an instance from an "OpenGIS Filter
     * Encoding Specification 1.1.0" compliant representation.
     *
     * @param name
     * @param geometryOperands
     */
    public SpatialOperator(String name, QualifiedName[] geometryOperands) {
        super(name);
        this.geometryOperands = geometryOperands;
    }

    /**
     * @return Returns the geometryOperands.
     */
    public QualifiedName[] getGeometryOperands() {
        return geometryOperands;
    }

    /**
     * @param geometryOperands
     *            The geometryOperands to set.
     */
    public void setGeometryOperands(QualifiedName[] geometryOperands) {
        this.geometryOperands = geometryOperands;
    }
}
