//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/graphics/sld/Symbolizer.java $
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
package org.deegree.graphics.sld;

/**
 * This is the basis of all symbolizers. It defines the method <tt>getGeometry</tt> that's common
 * to all symbolizers.
 * <p>
 * ----------------------------------------------------------------------
 * </p>
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 18195 $ $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 */

public interface Symbolizer {

    /**
     * The ScaleDenominator-information is optional and determines whether a rule (and thus a
     * Symbolizer) is a to be applied at a certain scale.
     *
     * @return the MinScaleDenominator
     */
    double getMinScaleDenominator();

    /**
     * Sets the MinScaleDenominator
     *
     * @param minScaleDenominator
     *            the MinScaleDenominator
     */
    void setMinScaleDenominator( double minScaleDenominator );

    /**
     * The ScaleDenominator-information is optional and determines whether a rule (and thus a
     * Symbolizer) is a to be applied at a certain scale.
     *
     * @return the MaxScaleDenominator
     */
    double getMaxScaleDenominator();

    /**
     * Sets the MaxScaleDenominator
     *
     * @param maxScaleDenominator
     *            the MaxScaleDenominator
     */
    void setMaxScaleDenominator( double maxScaleDenominator );

    /**
     * The Geometry element is optional and if it is absent then the default geometry property of
     * the feature type that is used in the containing FeatureStyleType is used. The precise meaning
     * of default geometry property is system-dependent. Most frequently, feature types will have
     * only a single geometry property.
     *
     * @return the geometry of the symbolizer
     */
    Geometry getGeometry();

    /**
     * Sets the Geometry.
     *
     * @param geometry
     *            the geometry of the symbolizer
     *
     */
    void setGeometry( Geometry geometry );

}
