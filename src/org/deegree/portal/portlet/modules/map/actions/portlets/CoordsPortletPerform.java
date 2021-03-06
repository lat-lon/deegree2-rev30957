//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/src/org/deegree/portal/portlet/modules/map/actions/portlets/CRSChooserPortletPerform.java $
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

package org.deegree.portal.portlet.modules.map.actions.portlets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.portal.Portlet;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * This Perform class takes care of changing the WMC's bounding box based on a scale paramter. The
 * parameter is passed in the request. The paramter name is defined by the static member
 * NEW_SCALE_VALUE
 *
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: rbezema $
 *
 * @version $Revision: 9455 $, $Date: 2008-01-08 12:03:30 +0100 (Di, 08 Jan 2008) $
 */

public class CoordsPortletPerform extends IGeoPortalPortletPerform {

    /**
     * private constructor
     *
     * @param request
     * @param portlet
     * @param sc
     */
    CoordsPortletPerform( HttpServletRequest request, Portlet portlet, ServletContext sc ) {
        super( request, portlet, sc );
    }

}
