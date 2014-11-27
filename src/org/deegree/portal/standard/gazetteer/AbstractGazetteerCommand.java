//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/portal/standard/gazetteer/AbstractGazetteerCommand.java $
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

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.ogcbase.ElementStep;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathStep;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.HTTP;
import org.deegree.ogcwebservices.wfs.XMLFactory;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: apoth $
 * 
 * @version $Revision: 26184 $, $Date: 2010-08-26 14:36:05 +0200 (Do, 26. Aug 2010) $
 */
abstract class AbstractGazetteerCommand {

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractGazetteerCommand.class );

    protected List<GazetteerItem> items;

    protected String gazetteerAddress;

    protected QualifiedName featureType;

    protected static Map<String, WFSCapabilities> capabilitiesMap;

    protected Map<String, String> properties;

    static {
        if ( capabilitiesMap == null ) {
            capabilitiesMap = new HashMap<String, WFSCapabilities>();
        }
    }

    protected void loadCapabilities()
                            throws Exception {
        InputStream is = HttpUtils.performHttpGet( gazetteerAddress, "request=GetCapabilities&service=WFS", 60000,
                                                   null, null, null ).getResponseBodyAsStream();
        WFSCapabilitiesDocument doc = new WFSCapabilitiesDocument();
        doc.load( is, gazetteerAddress );
        WFSCapabilities capa = (WFSCapabilities) doc.parseCapabilities();
        capabilitiesMap.put( gazetteerAddress, capa );
    }

    protected FeatureCollection performGetFeature( WFSCapabilities capabilities, GetFeature getFeature )
                            throws Exception {

        // find a valid URL for performing GetFeature requests
        URL wfs = null;
        org.deegree.ogcwebservices.getcapabilities.Operation[] op = capabilities.getOperationsMetadata().getOperations();
        for ( org.deegree.ogcwebservices.getcapabilities.Operation operation : op ) {
            if ( "GetFeature".equalsIgnoreCase( operation.getName() ) ) {
                DCPType[] dcp = operation.getDCPs();
                for ( DCPType dcpType : dcp ) {
                    if ( dcpType.getProtocol() instanceof HTTP ) {
                        wfs = ( (HTTP) dcpType.getProtocol() ).getPostOnlineResources()[0];
                    }
                }
            }
        }

        String encoding = Charset.defaultCharset().displayName();
        String gf = XMLFactory.export( getFeature ).getAsString( encoding );

        LOG.logDebug( "GetFeature request: ", gf );
        LOG.logDebug( "Sending against: ", wfs );
        Map<String, String> header = new HashMap<String, String>();
        header.put( "Content-Type", "text/xml; charset=" + encoding );
        InputStream is = HttpUtils.performHttpPost( wfs.toURI().toASCIIString(), gf, 60000, null, null, "text/xml",
                                                    encoding, header ).getResponseBodyAsStream();

        GMLFeatureCollectionDocument gml = new GMLFeatureCollectionDocument();
        gml.load( is, wfs.toURI().toASCIIString() );
        LOG.logDebug( "Response: " + gml.getAsPrettyString() );
        return gml.parse();
    }

    protected void createItemsList( FeatureCollection fc )
                            throws PropertyPathResolvingException {
        LOG.logDebug( "Collection contains " + fc.size() + " features" );
        items = new ArrayList<GazetteerItem>( fc.size() );
        PropertyPath gi = createPropertyPath( properties.get( "GeographicIdentifier" ) );
        PropertyPath gai = parseAsPropertyPath( properties.get( "AlternativeGeographicIdentifier" ) );
        PropertyPath tooltip = parseAsPropertyPath( properties.get( "TooltipName" ) );
        PropertyPath disp = createPropertyPath( properties.get( "DisplayName" ) );

        Iterator<Feature> iterator = fc.iterator();
        while ( iterator.hasNext() ) {
            Feature feature = (Feature) iterator.next();
            String gmlID = feature.getId();
            LOG.logDebug( "Parse item with id '" + gmlID + "'" );
            String geoId = parseProperty( gi, feature );
            if ( geoId == null )
                throw new IllegalArgumentException( "GeographicIdentifier in feature must not be null!" );
            String displayName = parseProperty( disp, feature );
            if ( displayName == null )
                throw new IllegalArgumentException( "Display in feature must not be null!" );
            String altGeoId = parseAlternativeGeographicIdentifiers( feature, gai, tooltip );
            items.add( new GazetteerItem( gmlID, geoId, altGeoId, displayName ) );
        }
    }

    protected PropertyPath[] getResultProperties( Map<String, String> properties ) {
        List<PropertyPath> pathes = new ArrayList<PropertyPath>();

        pathes.add( createPropertyPath( properties.get( "GeographicIdentifier" ) ) );

        String tmp = properties.get( "DisplayName" );
        if ( tmp != null && !tmp.equals( properties.get( "GeographicIdentifier" ) ) ) {
            pathes.add( createPropertyPath( tmp ) );
        }

        tmp = properties.get( "AlternativeGeographicIdentifier" );
        if ( tmp != null && !tmp.equals( properties.get( "GeographicIdentifier" ) ) ) {
            pathes.add( createPropertyPath( tmp ) );
        }

        tmp = properties.get( "TooltipName" );
        if ( tmp != null && !tmp.equals( properties.get( "GeographicIdentifier" ) )
             && !tmp.equals( properties.get( "AlternativeGeographicIdentifier" ) ) ) {
            pathes.add( createPropertyPath( tmp ) );
        }

        return pathes.toArray( new PropertyPath[pathes.size()] );
    }

    protected PropertyPath createPropertyPath( String name ) {
        List<String> l1 = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean opened = false;
        for ( int i = 0; i < name.length(); i++ ) {
            char c = name.charAt( i );
            if ( c == '{' ) {
                opened = true;
            }
            if ( c == '/' && !opened ) {
                l1.add( sb.toString() );
                sb.delete( 0, sb.length() );
            } else {
                sb.append( c );
            }
            if ( c == '}' ) {
                opened = false;
            }
        }
        l1.add( sb.toString() );

        String[] tmp = l1.toArray( new String[l1.size()] );
        List<PropertyPathStep> steps = new ArrayList<PropertyPathStep>();
        for ( String string : tmp ) {
            QualifiedName qn = null;
            if ( name.startsWith( "{" ) ) {
                qn = new QualifiedName( string );
            } else {
                qn = new QualifiedName( string, featureType.getNamespace() );
            }
            steps.add( new ElementStep( qn ) );
        }

        return new PropertyPath( steps );
    }

    private String parseAlternativeGeographicIdentifiers( Feature feature, PropertyPath alternativeIdentifier,
                                                          PropertyPath tooltip )
                            throws PropertyPathResolvingException {
        if ( tooltip != null ) {
            LOG.logDebug( "Parse alternative identifier from property TooltipName." );
            return parseAlternativeIdentifiers( feature, tooltip );
        } else if ( alternativeIdentifier != null ) {
            LOG.logDebug( "Parse alternative identifier from property AlternativeGeographicIdentifier." );
            return parseSingleAlternativeIdentifierFromPath( feature, alternativeIdentifier );
        }
        return null;
    }

    private String parseAlternativeIdentifiers( Feature feature, PropertyPath pathToParse )
                            throws PropertyPathResolvingException {
        LOG.logDebug( "Number of steps in the PropertyPath: " + pathToParse.getSteps() );
        if ( pathToParse.getSteps() == 1 ) {
            LOG.logDebug( "Parse multiple alternative identifiers." );
            return parseMultipleAlternativeIdentifierFromQualifiedName( feature, pathToParse );
        } else {
            LOG.logDebug( "Parse single alternative identifier." );
            return parseSingleAlternativeIdentifierFromPath( feature, pathToParse );
        }
    }

    private String parseSingleAlternativeIdentifierFromPath( Feature feature, PropertyPath pathToParse )
                            throws PropertyPathResolvingException {
        FeatureProperty fp = feature.getDefaultProperty( pathToParse );
        if ( fp != null ) {
            return (String) fp.getValue();
        }
        return null;
    }

    private String parseMultipleAlternativeIdentifierFromQualifiedName( Feature feature, PropertyPath pathToParse ) {
        QualifiedName qualifiedName = pathToParse.getStep( 0 ).getPropertyName();
        FeatureProperty[] altProps = feature.getProperties( qualifiedName );
        if ( altProps != null ) {
            StringBuilder alternativeIdentifier = new StringBuilder();
            for ( FeatureProperty featureProperty : altProps ) {
                String propValue = extractPropertyValue( featureProperty.getValue() );
                appendPropertyValue( alternativeIdentifier, propValue );
            }
            return alternativeIdentifier.toString();
        }
        return null;
    }

    private String parseProperty( PropertyPath gi, Feature feature )
                            throws PropertyPathResolvingException {
        LOG.logDebug( "Parse property " + gi );
        FeatureProperty defaultProperty = feature.getDefaultProperty( gi );
        if ( defaultProperty == null ) {
            LOG.logWarning( "Property is null!" );
            return null;
        }
        Object value = defaultProperty.getValue();
        if ( value == null ) {
            LOG.logWarning( "Property value is null!" );
            return null;
        }
        return value.toString();
    }

    private void appendPropertyValue( StringBuilder alternativeIdentifier, String propValue ) {
        if ( propValue != null ) {
            if ( !"".equals( alternativeIdentifier.toString() ) )
                alternativeIdentifier.append( ", " );
            alternativeIdentifier.append( propValue );
        }
    }

    private PropertyPath parseAsPropertyPath( String path ) {
        if ( path != null ) {
            return createPropertyPath( path );
        }
        return null;
    }

    private String extractPropertyValue( Object value ) {
        if ( value instanceof Feature ) {
            return extractPropertyValue( (Feature) value );
        } else if ( value instanceof FeatureProperty ) {
            return extractPropertyValue( (FeatureProperty) value );
        }
        return null;
    }

    private String extractPropertyValue( Feature feature ) {
        FeatureProperty[] properties = feature.getProperties();
        if ( properties != null && properties.length == 1 )
            return extractPropertyValue( properties[0] );
        return null;
    }

    private String extractPropertyValue( FeatureProperty featureProperty ) {
        if ( featureProperty instanceof FeatureProperty )
            return featureProperty.getValue().toString();
        return null;
    }

}