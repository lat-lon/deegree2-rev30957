//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/wmps/operation/DescribeTemplateResponse.java $
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
package org.deegree.ogcwebservices.wmps.operation;

import java.util.List;

import org.deegree.framework.util.Pair;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version $Revision: 21101 $, $Date: 2009-11-27 13:51:01 +0100 (Fr, 27. Nov 2009) $
 */
public class DescribeTemplateResponse {

    private List<Pair<String, String>> paramter;

    /**
     * @param paramter
     */
    public DescribeTemplateResponse( List<Pair<String, String>> paramter ) {
        this.paramter = paramter;
    }

    /**
     * @return the paramter
     */
    public List<Pair<String, String>> getParamter() {
        return paramter;
    }

}
