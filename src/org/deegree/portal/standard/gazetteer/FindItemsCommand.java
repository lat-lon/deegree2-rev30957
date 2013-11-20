//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/standard/gazetteer/FindItemsCommand.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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
package org.deegree.portal.standard.gazetteer;

import static java.lang.String.valueOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.StringTools;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsLikeOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wfs.operation.GetFeature.RESULT_TYPE;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version $Revision: 23405 $, $Date: 2010-04-07 10:31:17 +0200 (Mi, 07. Apr 2010) $
 */
public class FindItemsCommand extends AbstractGazetteerCommand {

    static final char DEFAULT_ESCAPE_CHAR = '/';

	static final char DEFAULT_SINGLE_CHAR = '?';

	static final char DEFAULT_WILD_CARD = '*';

	private String searchString;

    private boolean searchOnAltName;

    @SuppressWarnings("unused")
    private boolean phonetic;

    private boolean matchCase;

    private char escapeChar;

    private char wildCard;

    private char singleChar;
    
    /**
     * 
     * @param gazetteerAddress
     * @param featureType
     * @param properties
     * @param searchString
     * @param searchOnAltName
     *            if true alternativeGeographicIdentifier will be included into search
     * @param strict
     *            if false wild cards will be added to search string
     * @param phonetic
     *            if true soundex will be used for searching (gazetteer/featuetype must support this)
     * @param matchCase
     */
    FindItemsCommand( String gazetteerAddress, QualifiedName featureType, Map<String, String> properties,
                      String searchString, boolean searchOnAltName, boolean strict, boolean phonetic, boolean matchCase ) {
        this( gazetteerAddress, featureType, properties, searchString, searchOnAltName, strict, phonetic, matchCase,
              DEFAULT_ESCAPE_CHAR, DEFAULT_SINGLE_CHAR, DEFAULT_WILD_CARD );
    }
    
    /**
     * 
     * @param gazetteerAddress
     * @param featureType
     * @param properties
     * @param searchString
     * @param searchOnAltName
     *            if true alternativeGeographicIdentifier will be included into search
     * @param strict
     *            if false wild cards will be added to search string
     * @param phonetic
     *            if true soundex will be used for searching (gazetteer/featuetype must support this)
     * @param matchCase
     * @param escapeChar
     * @param singleChar
     * @param wildCard
     */
    FindItemsCommand( String gazetteerAddress, QualifiedName featureType, Map<String, String> properties,
                      String searchString, boolean searchOnAltName, boolean strict, boolean phonetic,
                      boolean matchCase, char escapeChar, char singleChar, char wildCard ) {
        this.gazetteerAddress = gazetteerAddress;
        this.featureType = featureType;
        this.searchString = searchString;
        this.searchOnAltName = searchOnAltName;
        this.properties = properties;
        this.phonetic = phonetic;
        this.matchCase = matchCase;
        this.escapeChar = escapeChar;
        this.singleChar = singleChar;
        this.wildCard = wildCard;
        escapeChar = singleChar;
        if ( !strict && !searchString.trim().equalsIgnoreCase( valueOf( wildCard ) ) ) {
            String escapedWildCard = valueOf( escapeChar ) + valueOf( wildCard );
            this.searchString = StringTools.replace( searchString, valueOf( wildCard ), escapedWildCard, true );
            this.searchString = wildCard + this.searchString + wildCard;
        }
    }

    /**
     * 
     * @return list of matching location instances (items)
     * @throws Exception
     */
    List<GazetteerItem> execute()
                            throws Exception {
        if ( !capabilitiesMap.containsKey( gazetteerAddress ) ) {
            loadCapabilities();
        }
        WFSCapabilities capabilities = capabilitiesMap.get( gazetteerAddress );

        // TODO
        // consider soundex if defined

        // create query filter
        PropertyName propertyName = new PropertyName( createPropertyPath( properties.get( "GeographicIdentifier" ) ) );

        Literal literal = new Literal( searchString );
        Operation operation = new PropertyIsLikeOperation( propertyName, literal, wildCard, singleChar, escapeChar,
                                                           matchCase );

        if ( searchOnAltName && properties.get( "AlternativeGeographicIdentifier" ) != null ) {
            // if search should be performed on alternativeGeographicIdentifier too
            // a logical OR operation must be created
            List<Operation> opList = new ArrayList<Operation>();
            opList.add( operation );
            propertyName = new PropertyName( createPropertyPath( properties.get( "AlternativeGeographicIdentifier" ) ) );
            literal = new Literal( searchString );
            operation = new PropertyIsLikeOperation( propertyName, literal, wildCard, singleChar, escapeChar, matchCase );
            opList.add( operation );
            operation = new LogicalOperation( OperationDefines.OR, opList );
        }
        ComplexFilter filter = new ComplexFilter( operation );

		// sort result by sort property
		SortProperty[] sp = null;
		String sortPropertyName = properties.get("SortProperty");
		if (sortPropertyName != null && sortPropertyName.length() > 0) {
			PropertyPath sortProperty = createPropertyPath(sortPropertyName);
			sp = new SortProperty[] { SortProperty.create(sortProperty, "ASC") };
		}
        
        // select just properties needed to fill result list
        PropertyPath[] propertyNames = getResultProperties( properties );

        // create Query and GetFeature request
        Query query = Query.create( propertyNames, null, sp, null, null, new QualifiedName[] { featureType }, null,
                                    null, filter, 500, 0, RESULT_TYPE.RESULTS );
        GetFeature getFeature = GetFeature.create( capabilities.getVersion(), UUID.randomUUID().toString(),
                                                   RESULT_TYPE.RESULTS, GetFeature.FORMAT_GML3, null, 500, 0, -1, -1,
                                                   new Query[] { query } );

        // perform GetFeature request and create resulting GazetteerItems list
        createItemsList( performGetFeature( capabilities, getFeature ) );
        return items;
    }

}
