//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/model/spatialschema/MultiPoint.java $
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

package org.deegree.model.spatialschema;

/**
 *
 * The interface defines the access to a aggregations of <tt>Point</tt> objects.
 *
 * <p>
 * -----------------------------------------------------
 * </p>
 *
 * @author Andreas Poth
 * @version $Revision: 18195 $ $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *          <p>
 */
public interface MultiPoint extends MultiPrimitive {
    /**
     * @link aggregation
     * @clientCardinality 0..*
     */

    /**
     * adds a Point to the aggregation
     *
     * @param point
     */
    public void addPoint( Point point );

    /**
     * inserts a Point into the aggregation. all elements with an index equal or larger index will be moved. if index is
     * larger then getSize() - 1 or smaller then 0 or point equals null an exception will be thrown.
     *
     * @param point
     *            Point to insert.
     * @param index
     *            position where to insert the new Point
     * @throws GeometryException
     */
    public void insertPointAt( Point point, int index )
                            throws GeometryException;

    /**
     * sets the submitted Point at the submitted index. the element at the position <code>index</code> will be
     * removed. if index is larger then getSize() - 1 or smaller then 0 or point equals null an exception will be
     * thrown.
     *
     * @param point
     *            Point to set.
     * @param index
     *            position where to set the new Point
     * @throws GeometryException
     */
    public void setPointAt( Point point, int index )
                            throws GeometryException;

    /**
     * removes the submitted Point from the aggregation
     *
     * @param point
     *            to remove
     *
     * @return the removed Point
     */
    public Point removePoint( Point point );

    /**
     * removes the Point at the submitted index from the aggregation. if index is larger then getSize() - 1 or smaller
     * then 0 an exception will be thrown.
     *
     * @param index
     *
     * @return the removed Point
     * @throws GeometryException
     */
    public Point removePointAt( int index )
                            throws GeometryException;

    /**
     * returns the Point at the submitted index.
     *
     * @param index
     * @return the Point at the submitted index.
     */
    public Point getPointAt( int index );

    /**
     * returns all Points as array
     *
     * @return all Points as array
     */
    public Point[] getAllPoints();

}
