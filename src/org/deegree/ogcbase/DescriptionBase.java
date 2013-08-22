//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcbase/DescriptionBase.java $
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
package org.deegree.ogcbase;

import org.deegree.ogcwebservices.MetadataLink;

/**
 * @version $Revision: 18195 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.0. $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 2.0
 */

public class DescriptionBase implements Cloneable {

    private String name = null;

    private String description = null;

    private MetadataLink metadataLink = null;

    /**
     * just <tt>name</tt> is mandatory
     *
     * @param name
     * @throws OGCException
     */
    public DescriptionBase( String name ) throws OGCException {
        setName( name );
    }

    /**
     * @param description
     * @param name
     * @param metadataLink
     * @throws OGCException
     */
    public DescriptionBase( String name, String description, MetadataLink metadataLink ) throws OGCException {
        this.description = description;
        setName( name );
        this.metadataLink = metadataLink;
    }

    /**
     * @return Returns the description.
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            The description to set.
     *
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * @return Returns the metadataLink.
     *
     */
    public MetadataLink getMetadataLink() {
        return metadataLink;
    }

    /**
     * @param metadataLink
     *            The metadataLink to set.
     *
     */
    public void setMetadataLink( MetadataLink metadataLink ) {
        this.metadataLink = metadataLink;
    }

    /**
     * @return Returns the name.
     *
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     * @throws OGCException
     *
     */
    public void setName( String name )
                            throws OGCException {
        if ( name == null ) {
            throw new OGCException( "name must be <> null for DescriptionBase" );
        }
        this.name = name;
    }

    @Override
    public Object clone() {
        try {
            MetadataLink ml = null;
            if ( metadataLink != null ) {
                ml = (MetadataLink) metadataLink.clone();
            }
            return new DescriptionBase( name, description, ml );
        } catch ( Exception e ) {
            // just return null
        }
        return null;
    }

}
