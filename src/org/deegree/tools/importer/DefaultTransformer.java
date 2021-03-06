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
package org.deegree.tools.importer;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 *
 * The <code>DefaultTransformer</code> can be used, when no transformation is neccessary.
 *
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author: buesching $
 *
 * @version $Revision: 1.1 $, $Date: 2007-10-10 14:21:26 $
 *
 */
public class DefaultTransformer implements Transformer {

    private static final ILogger LOG = LoggerFactory.getLogger( DefaultTransformer.class );

    /**
     * returns the inpt parameter withot changes
     *
     * @param objectToTransform
     * @return the input parameter object without changes
     */
    public Object transform( Object objectToTransform ) {
        LOG.logInfo( Messages.getString( "DefaultTransformer.TRANSFORMATION_SEUCCESFUL" ) ); //$NON-NLS-1$
        return objectToTransform;

    }

}
