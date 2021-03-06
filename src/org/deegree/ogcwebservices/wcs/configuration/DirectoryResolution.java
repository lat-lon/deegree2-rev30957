// $HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/wcs/configuration/DirectoryResolution.java $
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
package org.deegree.ogcwebservices.wcs.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.deegree.model.spatialschema.Envelope;

/**
 * models a <tt>Resolution</tt> by describing the access to the assigned coverages through named
 * directories containing a well defined collection of coverages.
 *
 * @version $Revision: 18195 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.0. $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 2.0
 */
public class DirectoryResolution extends AbstractResolution {

    private List<Directory> directories = null;

    /**
     * @param minScale
     * @param maxScale
     * @param range
     * @throws IllegalArgumentException
     */
    public DirectoryResolution( double minScale, double maxScale, Range[] range, Directory[] directories )
                            throws IllegalArgumentException {
        super( minScale, maxScale, range );
        setDirectories( directories );
    }

    /**
     * @return Returns the directories.
     *
     */
    public Directory[] getDirectories() {
        return directories.toArray( new Directory[directories.size()] );
    }

    /**
     * returns the <tt>Directories</tt> of a <tt>Resolution</tt> that intersects with the passed
     * <tt>Envelope</tt>
     *
     * @return Returns the directories.
     */
    public Directory[] getDirectories( Envelope envelope ) {
        List<Directory> list = new ArrayList<Directory>( directories.size() );
        for ( Iterator iter = directories.iterator(); iter.hasNext(); ) {
            Directory dir = (Directory) iter.next();
            if ( dir.getEnvelope().intersects( envelope ) ) {
                list.add( dir );
            }
        }
        Directory[] dirs = new Directory[list.size()];
        return list.toArray( dirs );
    }

    /**
     * @param directories
     *            The directories to set.
     */
    public void setDirectories( Directory[] directories ) {
        this.directories = new ArrayList<Directory>( Arrays.asList( directories ) );
    }

    /**
     * adds a <tt>Directory</tt> to the <tt>Resolution</tt>
     *
     * @param directory
     */
    public void addDirectory( Directory directory ) {
        directories.add( directory );
    }

    /**
     * removes a <tt>Directory</tt> from the <tt>Resolution</tt>
     *
     * @param directory
     */
    public void removeDirectory( Directory directory ) {
        directories.remove( directory );
    }

}
