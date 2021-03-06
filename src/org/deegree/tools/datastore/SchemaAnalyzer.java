//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/base/trunk/src/org/deegree/tools/datastore/SchemaAnnotator.java $
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
package org.deegree.tools.datastore;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.schema.XMLSchemaException;
import org.deegree.io.datastore.schema.MappedGMLSchemaDocument;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.schema.GMLSchema;
import org.xml.sax.SAXException;

/**
 * Prints a summary of a GML application schema.
 * <p>
 * NOTE: this only works for schemas that deegree understands
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: schneider $
 *
 * @version $Revision: 1.2 $, $Date: 2007-09-05 17:56:26 $
 */
public class SchemaAnalyzer {

    /**
     * @param args
     * @throws SAXException
     * @throws IOException
     * @throws MalformedURLException
     * @throws XMLParsingException
     * @throws XMLSchemaException
     * @throws UnknownCRSException
     */
    public static void main( String[] args )
                            throws MalformedURLException, IOException, SAXException, XMLSchemaException,
                            XMLParsingException, UnknownCRSException {

        if ( args.length != 1 ) {
            System.out.println( "Usage: SchemaAnalyzer <schemafile>" );
            System.exit( 0 );
        }

        File file = new File( args[0] );
        System.out.println( "Loading input schema: '" + file + "'" );
        MappedGMLSchemaDocument doc = new MappedGMLSchemaDocument();
        doc.load( file.toURI().toURL() );

        GMLSchema schema = doc.parseGMLSchema();
        System.out.println( "Schema info: " );
        System.out.println( schema );
    }
}
