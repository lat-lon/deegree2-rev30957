//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/standard/gazetteer/AbstractGazetteerListener.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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
package org.deegree.portal.standard.gazetteer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.deegree.enterprise.control.ajax.AbstractListener;
import org.deegree.framework.util.Parameter;
import org.deegree.framework.util.ParameterList;
import org.deegree.framework.xml.XMLFragment;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version $Revision: 21565 $, $Date: 2009-12-21 15:02:38 +0100 (Mo, 21. Dez 2009) $
 */
abstract class AbstractGazetteerListener extends AbstractListener {

    /**
     * load gazetteer featuretype/locationtype hierarchy definitions
     * 
     * @param initParams
     * @throws Exception
     */
    protected List<Hierarchy> loadHierarchiesDefinitions( ParameterList initParams )
                            throws Exception {

        Parameter[] parameters = initParams.getParameters();

        List<Hierarchy> hierarchies = new ArrayList<Hierarchy>( parameters.length );
        for ( Parameter parameter : parameters ) {
            String value = ( (String) parameter.getValue() ).trim();
            hierarchies.add( loadHierarchy( value ) );
        }
        return hierarchies;
    }

    protected Hierarchy loadHierarchy( String value )
                            throws Exception {
        // remove CDATA
        if ( value.startsWith( "<![CDATA[" ) ) {
            value = value.substring( 9, value.length() - 3 ).trim();
        }
        XMLFragment xml = new XMLFragment();
        xml.load( new StringReader( value ), XMLFragment.DEFAULT_URL );
        return new Hierarchy( xml );

    }

}
