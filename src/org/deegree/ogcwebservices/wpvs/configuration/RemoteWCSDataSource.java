//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/wpvs/configuration/RemoteWCSDataSource.java $
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

package org.deegree.ogcwebservices.wpvs.configuration;

import java.awt.Color;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.spatialschema.Surface;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.deegree.ogcwebservices.wpvs.capabilities.OWSCapabilities;

/**
 * This class represents a remote WCS dataSource object, but doesn't actually do anything .
 *
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 */
public class RemoteWCSDataSource extends LocalWCSDataSource {

	/**
	 * Creates a new <code>RemoteWCSDataSource</code> object from the given parameters.
	 *
	 * @param name
	 * @param owsCapabilities
	 * @param validArea
	 * @param minScaleDenominator
	 * @param maxScaleDenominator
	 * @param filterCondition a base request //TODO give an example
	 * @param transparentColors
	 */
	public RemoteWCSDataSource( QualifiedName name, OWSCapabilities owsCapabilities, Surface validArea,
								double minScaleDenominator, double maxScaleDenominator,
								GetCoverage filterCondition, Color[] transparentColors ) {

		super( name, owsCapabilities, validArea, minScaleDenominator,
			   maxScaleDenominator, filterCondition, transparentColors );
        this.setServiceType( AbstractDataSource.REMOTE_WCS );
	}
}
