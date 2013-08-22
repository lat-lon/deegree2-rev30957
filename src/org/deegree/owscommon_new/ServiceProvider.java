//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/owscommon_new/ServiceProvider.java $
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
package org.deegree.owscommon_new;

import org.deegree.model.metadata.iso19115.CitedResponsibleParty;
import org.deegree.model.metadata.iso19115.OnlineResource;

/**
 * <code>ServiceProvider</code> stores metadata contained within a ServiceProvider element
 * according to the OWS common specification version 1.0.0.
 *
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 2.0, $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 2.0
 */

public class ServiceProvider {

    private String providerName = null;

    private OnlineResource providerSite = null;

    private CitedResponsibleParty serviceContact = null;

    /**
     * Standard constructor that initializes all encapsulated data.
     *
     * @param providerName
     * @param providerSite
     * @param serviceContact
     */
    public ServiceProvider( String providerName, OnlineResource providerSite, CitedResponsibleParty serviceContact ) {
        this.providerName = providerName;
        this.providerSite = providerSite;
        this.serviceContact = serviceContact;
    }

    /**
     * @return Returns the providerName.
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * @return Returns the providerSite.
     */
    public OnlineResource getProviderSite() {
        return providerSite;
    }

    /**
     * @return Returns the serviceContact.
     */
    public CitedResponsibleParty getServiceContact() {
        return serviceContact;
    }

}
