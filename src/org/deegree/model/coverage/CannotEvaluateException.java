//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/model/coverage/CannotEvaluateException.java $
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

// OpenGIS direct dependencies

/**
 * The base class for exceptions thrown when a quantity can't be evaluated. This exception is
 * usually invoked by a <code>Coverage.evaluate(PT_CoordinatePoint)</code> method, for example
 * when a point is outside the coverage.
 *
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 20326 $, $Date: 2009-10-22 18:41:56 +0200 (Do, 22. Okt 2009) $
 *
 * @see "org.opengis.coverage.Coverage"
 */
public class CannotEvaluateException extends RuntimeException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 506793649975583062L;

    /**
     * Creates an exception with no message.
     */
    public CannotEvaluateException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param message
     *            The detail message. The detail message is saved for later retrieval by the
     *            {@link #getMessage()} method.
     */
    public CannotEvaluateException( String message ) {
        super( message );
    }
}
