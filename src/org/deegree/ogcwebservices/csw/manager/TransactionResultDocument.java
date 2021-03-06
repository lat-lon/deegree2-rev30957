//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/csw/manager/TransactionResultDocument.java $
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
package org.deegree.ogcwebservices.csw.manager;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 *
 *
 * @version $Revision: 18195 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.0. $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 *
 * @since 2.0
 */
public class TransactionResultDocument extends XMLFragment {

    private static final long serialVersionUID = 2354194484320566707L;

    private static final ILogger LOG = LoggerFactory.getLogger( TransactionDocument.class );

    private NamespaceContext nsc = CommonNamespaces.getNamespaceContext();

    /**
     * initializes an empty TransactionDocument
     *
     */
    public TransactionResultDocument() {
        try {
            setSystemId( XMLFragment.DEFAULT_URL );
        } catch ( MalformedURLException e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    /**
     * @param version
     *
     */
    public void createEmptyDocument( String version) {
        Document doc = XMLTools.create();
        URI namespaceURI = (version.equals( "2.0.2" )) ? CommonNamespaces.CSW202NS : CommonNamespaces.CSWNS;
        Element root = doc.createElementNS( namespaceURI.toASCIIString(),
                                            "csw:TransactionResponse" );
        setRootElement( root );

    }

    /**
     * parses a CS-W TransactionResponse document and creates a jave class representation from it.
     * @param transaction
     *
     * @return the result
     * @throws XMLParsingException
     */
    public TransactionResult parseTransactionResponse( Transaction transaction )
                            throws XMLParsingException {

        Element root = getRootElement();
        int inserted = XMLTools.getNodeAsInt( root, "./csw:TransactionSummary/csw:totalInserted",
                                              nsc, 0 );
        int deleted = XMLTools.getNodeAsInt( root, "./csw:TransactionSummary/csw:totalDeleted",
                                             nsc, 0 );
        int updated = XMLTools.getNodeAsInt( root, "./csw:TransactionSummary/csw:totalUpdated",
                                             nsc, 0 );

        List<Node> list = XMLTools.getNodes( root, "./csw:InsertResult/child::*", nsc );
        List<Node> records = new ArrayList<Node>( list.size() );
        for ( int i = 0; i < list.size(); i++ ) {
            records.add( list.get( i ) );
        }
        InsertResults ir = new InsertResults( records );

        return new TransactionResult( transaction, inserted, deleted, updated, ir );

    }

}
