//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/wass/wss/operation/WSSGetCapabilitiesDocument.java $
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

package org.deegree.ogcwebservices.wass.wss.operation;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;

/**
 * A WSSGetCapabilitiesClass xml request parser.
 *
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 2.0, $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 */

public class WSSGetCapabilitiesDocument extends XMLFragment {

    private static final long serialVersionUID = 2601051525408253639L;

    private static Pattern pattern = Pattern.compile( "(application|audio|image|text|video|message|multipart|model)/.+(;\\s*.+=.+)*" );

    private static final String PRE = CommonNamespaces.OWS_PREFIX;

    protected WSSGetCapabilities parseCapabilities( String id, Element root )
                            throws XMLParsingException {

        Element acceptVersionsNode = (Element) XMLTools.getNode( root, PRE + "AcceptVersions",
                                                                 nsContext );
        String[] acceptVersions = null;
        if ( acceptVersionsNode != null )
            acceptVersions = XMLTools.getRequiredNodesAsStrings( acceptVersionsNode, PRE
                                                                                     + "Version",
                                                                 nsContext );

        Element sectionsNode = (Element) XMLTools.getNode( root, PRE + "Sections", nsContext );
        String[] sections = null;
        if ( sectionsNode != null )
            sections = XMLTools.getNodesAsStrings( sectionsNode, PRE + "Section", nsContext );

        Element acceptFormatsNode = (Element) XMLTools.getNode( root, PRE + "AcceptFormats",
                                                                nsContext );
        String[] acceptFormats = null;
        if ( acceptFormatsNode != null ) {
            acceptFormats = XMLTools.getNodesAsStrings( acceptFormatsNode, PRE + "OutputFormat",
                                                        nsContext );
            for ( String str : acceptFormats )
                if ( !pattern.matcher( str ).matches() )
                    throw new XMLParsingException(
                                                   Messages.getMessage( "WASS_ERROR_VALUE_NO_MIMETYPE" ) );
        }

        String updateSequence = XMLTools.getNodeAsString( root, "@updateSequence", nsContext, null );

        String service = XMLTools.getRequiredNodeAsString( root, "@service", nsContext );
        if ( !service.equals( "WSS" ) )
            throw new XMLParsingException( Messages.getMessage( "WASS_ERROR_NOT_WSS" ) );

        return new WSSGetCapabilities( id, "1.0", updateSequence, acceptFormats, acceptVersions,
                                       sections, new HashMap<String,String>() );
    }

}
