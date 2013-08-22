//$$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/tools/shape/AVLPointSymbolCodeList.java $$
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
package org.deegree.tools.shape;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.1, $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 1.1
 */
class AVLPointSymbolCodeList {

    private static final String CODELIST = "AVLPointSymbolCodeList.xml";

    private Map<String, String> map = new HashMap<String, String>();

    /**
     *
     */
    public AVLPointSymbolCodeList() throws SAXException, IOException, XMLParsingException {

        XMLFragment frag = new XMLFragment( AVLPointSymbolCodeList.class.getResource( CODELIST ) );
        Document doc = frag.getRootElement().getOwnerDocument();

        /* ************************* OLD ********************************* */
        // Node nsNode = XMLTools.getNamespaceNode( new String[] {} );
        // NodeList nl = XMLTools.getXPath( "Symbols", element, nsNode );
        NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();
        List nl = XMLTools.getNodes( doc, "Symbols", nsContext );

        for ( Object o : nl ) {
            if ( o instanceof Node ) {
                Node n = (Node) o;
                String code = XMLTools.getRequiredNodeAsString( n, "@code", nsContext );
                String sym = XMLTools.getRequiredNodeAsString( n, "@symbol", nsContext );
                map.put( code, sym );
            }
        }
    }

    public String getSymbol( String code ) {
        return map.get( code );
    }
}
