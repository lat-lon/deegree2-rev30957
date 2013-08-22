//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/model/coverage/MetadataNameNotFoundException.java $
/*----------------------------------------------------------------------------
 This file originated as a part of GeoAPI.

 GeoAPI is free software. GeoAPI may be used, modified and
 redistributed by anyone for any purpose requring only maintaining the
 copyright and license terms on the source code and derivative files.
 See the OGC legal page for details.

 The copyright to the GeoAPI interfaces is held by the Open Geospatial
 Consortium, see http://www.opengeospatial.org/ogc/legal
----------------------------------------------------------------------------*/
package org.deegree.model.coverage;

/**
 * Thrown when a requested metadata is not found.
 *
 * @UML exception CV_MetadataNameNotFound
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 *
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 20326 $, $Date: 2009-10-22 18:41:56 +0200 (Do, 22. Okt 2009) $
 *
 * @see SampleDimension#getMetadataValue
 * @see Coverage#getMetadataValue
 * @see "org.opengis.coverage.grid.GridCoverageReader#getMetadataValue"
 * @see "org.opengis.coverage.processing.GridCoverageProcessor#getMetadataValue"
 */
public class MetadataNameNotFoundException extends IllegalArgumentException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 3217010469714161299L;

    /**
     * Creates an exception with no message.
     */
    public MetadataNameNotFoundException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param message
     *            The detail message. The detail message is saved for later retrieval by the
     *            {@link #getMessage()} method.
     */
    public MetadataNameNotFoundException( String message ) {
        super( message );
    }
}
