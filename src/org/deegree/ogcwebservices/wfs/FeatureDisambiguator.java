//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/ogcwebservices/wfs/FeatureDisambiguator.java $
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
package org.deegree.ogcwebservices.wfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.idgenerator.FeatureIdAssigner;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.XLinkedFeatureProperty;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;

/**
 * Responsible for the normalization of feature collections that are going to be persisted (i.e. inserted into a
 * {@link org.deegree.io.datastore Datastore}). This is necessary, because it is up to WFS clients whether feature ids
 * (gml:id attribute) are provided in an insert/update request or not.
 * <p>
 * After processing, the resulting feature collection meets the following requirements:
 * <ul>
 * <li>Every member feature (and subfeature) has a unique feature id that uniquely identifies it. Note that this id is
 * only momentarily valid, and that the final feature id used for storing it is generated by the
 * {@link FeatureIdAssigner} class in a later step.</li>
 * <li>Features that are equal according to the annotated schema (deegreewfs:IdentityPart declarations) are represented
 * by the same feature instance.</li>
 * <li>Complex feature properties use xlink to specify their content if necessary.</li>
 * <li>Root features in the feature collection never use xlinks.</li>
 * </ul>
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 */
class FeatureDisambiguator {

    private static final ILogger LOG = LoggerFactory.getLogger( FeatureDisambiguator.class );

    private FeatureCollection fc;

    private Map<MappedFeatureType, Set<Feature>> ftMap;

    // key: feature id, value: feature instance (representer for all features with same id)
    private Map<String, Feature> representerMap = new HashMap<String, Feature>();

    /**
     * Creates a new <code>FeatureDisambiguator</code> to disambiguate the given feature collection.
     *
     * @param fc
     *            feature collection to disambiguate
     */
    FeatureDisambiguator( FeatureCollection fc ) {
        this.fc = fc;
        this.ftMap = buildFeatureTypeMap( fc );
    }

    /**
     * Checks if any anonymous features (without id) are present in the feature collection.
     *
     * @return true, if one or more anonymous features are present, false otherwise
     */
    boolean checkForAnonymousFeatures() {
        for ( MappedFeatureType ft : this.ftMap.keySet() ) {
            for ( Feature feature : this.ftMap.get( ft ) ) {
                if ( feature.getId() == null || feature.getId().equals( "" ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Merges all "equal" feature (and subfeature) instances in the associated feature collection that do not have a
     * feature id. Afterwards, every feature (and subfeature) in the collection has a unique feature id.
     * <p>
     * It is considered an error if there is more than root feature with the same id after the identification of equal
     * features.
     * <p>
     * Furthermore, there is always only one feature instance with a certain id, i.e. "equal" features are represented
     * by the same feature instance.
     *
     * @return "merged" feature collection
     * @throws DatastoreException
     */
    FeatureCollection mergeFeatures()
                            throws DatastoreException {

        for ( MappedFeatureType ft : this.ftMap.keySet() ) {
            LOG.logDebug( ftMap.get( ft ).size() + " features of type: " + ft.getName() );
        }

        assignFIDsAndRepresenters();
        checkForEqualRootFeatures();
        replaceDuplicateFeatures();
        return this.fc;
    }

    /**
     * Assigns a (temporarily) feature id to every anonymous feature (or subfeature) of the given type in the feature
     * collection.
     * <p>
     * Also builds the <code>representerMap</code>, so each feature id is mapped to the feature instance that is used as
     * the single representer for all features instances with this id.
     * <p>
     * It is ensured that for each feature id that is associated with a root feature of the collection, the root feature
     * is used as the representing feature instance. This is important to guarantee that the root features in the
     * collection represent themselves and need not to be replaced in {@link #replaceDuplicateFeatures()}.
     *
     * @throws DatastoreException
     */
    private void assignFIDsAndRepresenters()
                            throws DatastoreException {

        for ( MappedFeatureType ft : this.ftMap.keySet() ) {
            assignFIDs( ft );
        }

        // ensure that every root feature is the "representer" for it's feature id
        for ( int i = 0; i < this.fc.size(); i++ ) {
            Feature rootFeature = this.fc.getFeature( i );
            String fid = rootFeature.getId();
            this.representerMap.put( fid, rootFeature );
        }
    }

    /**
     * Assigns a (temporarily) feature id to every anonymous feature (or subfeature) of the given type in the feature
     * collection.
     * <p>
     * Also builds the <code>representerMap</code>, so every feature id is mapped to a single feature instance that will
     * represent it everywhere in the collection.
     *
     * @param ft
     * @throws DatastoreException
     */
    private void assignFIDs( MappedFeatureType ft )
                            throws DatastoreException {

        Collection<Feature> features = this.ftMap.get( ft );

        LOG.logDebug( "Identifying " + features.size() + " features of type '" + ft.getName() + "'." );

        for ( Feature feature : features ) {
            // only find features "equal" to feature, if feature does not have an id yet
            if ( feature.getId() == null || "".equals( feature.getId() ) ) {
                if ( !ft.getGMLId().isIdentityPart() ) {
                    List<Feature> equalFeatures = new ArrayList<Feature>();
                    equalFeatures.add( feature );

                    for ( Feature otherFeature : features ) {
                        if ( compareFeatures( feature, otherFeature, new HashMap<Feature, List<Feature>>() ) ) {
                            LOG.logDebug( "Found matching features of type: '" + ft.getName() + "'." );
                            equalFeatures.add( otherFeature );
                        }
                    }
                    assignSameFID( equalFeatures );
                } else {
                    // don't test for equal features, just assign a new fid
                    feature.setId( UUID.randomUUID().toString() );
                }
            }
        }

        for ( Feature feature : features ) {
            String fid = feature.getId();
            if ( this.representerMap.get( fid ) == null ) {
                this.representerMap.put( fid, feature );
            }
        }
    }

    /**
     * Assigns the same feature id to every feature in the given list of "equal" features.
     * <p>
     * If any feature in the list has a feature id assigned to it already, this feature id is used. If no feature has a
     * feature id, a new feature id (a UUID) is generated.
     *
     * @param equalFeatures
     * @throws DatastoreException
     */
    private void assignSameFID( List<Feature> equalFeatures )
                            throws DatastoreException {

        LOG.logDebug( "Found " + equalFeatures.size() + " 'equal' features of type "
                      + equalFeatures.get( 0 ).getFeatureType().getName() );

        String fid = null;

        // check if any feature has a fid already
        for ( Feature feature : equalFeatures ) {
            String otherFid = feature.getId();
            if ( otherFid != null && !otherFid.equals( "" ) ) {
                if ( fid != null && !fid.equals( otherFid ) ) {
                    String msg = Messages.getMessage( "WFS_IDENTICAL_FEATURES", feature.getFeatureType().getName(),
                                                      fid, otherFid );
                    throw new DatastoreException( msg );
                }
                fid = otherFid;
            }
        }

        if ( fid == null ) {
            fid = UUID.randomUUID().toString();
            this.representerMap.put( fid, equalFeatures.get( 0 ) );
        }

        // assign fid to every "equal" feature
        for ( Feature feature : equalFeatures ) {
            feature.setId( fid );
        }
    }

    /**
     * Determines whether two feature instances are "equal" according to the annotated schema (deegreewfs:IdentityPart
     * declarations).
     *
     * @param feature1
     * @param feature2
     * @param compareMap
     * @return true, if the two features are "equal", false otherwise
     */
    private boolean compareFeatures( Feature feature1, Feature feature2, Map<Feature, List<Feature>> compareMap ) {

        LOG.logDebug( "feature1: " + feature1.getName() + " id=" + feature1.getId() + " hashCode="
                      + feature1.hashCode() );
        LOG.logDebug( "feature2: " + feature2.getName() + " id=" + feature2.getId() + " hashCode="
                      + feature2.hashCode() );

        // same feature instance? -> equal
        if ( feature1 == feature2 ) {
            return true;
        }

        // same feature id -> equal / different feature id -> not equal
        String fid1 = feature1.getId();
        String fid2 = feature2.getId();
        if ( fid1 != null && fid2 != null && !"".equals( fid1 ) && !"".equals( fid2 ) ) {
            return fid1.equals( fid2 );
        }

        // different feature types? -> not equal
        MappedFeatureType ft = (MappedFeatureType) feature1.getFeatureType();
        if ( feature2.getFeatureType() != ft ) {
            return false;
        }

        // feature id is identity part? -> not equal (unique ids for all anonymous features)
        if ( ft.getGMLId().isIdentityPart() ) {
            return false;
        }

        // there is already a compareFeatures() call with these features on the stack
        // -> end recursion
        List<Feature> features = compareMap.get( feature1 );
        if ( features == null ) {
            features = new ArrayList<Feature>();
            compareMap.put( feature1, features );
        } else {
            for ( Feature feature : features ) {
                if ( feature2 == feature ) {
                    return true;
                }
            }
        }
        features.add( feature2 );

        features = compareMap.get( feature2 );
        if ( features == null ) {
            features = new ArrayList<Feature>();
            compareMap.put( feature2, features );
        } else {
            for ( Feature feature : features ) {
                if ( feature1 == feature ) {
                    return true;
                }
            }
        }
        features.add( feature1 );

        // check every "relevant" property for equality
        PropertyType[] properties = ft.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            MappedPropertyType propertyType = (MappedPropertyType) properties[i];
            if ( propertyType.isIdentityPart() ) {
                if ( !compareProperties( propertyType, feature1, feature2, compareMap ) ) {
                    LOG.logDebug( "Not equal: values for property '" + propertyType.getName() + " do not match." );
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines whether two feature instances have the same content in the specified property.
     *
     * @param propertyType
     * @param feature1
     * @param feature2
     * @param compareMap
     * @return true, if the properties are "equal", false otherwise
     */
    private boolean compareProperties( MappedPropertyType propertyType, Feature feature1, Feature feature2,
                                       Map<Feature, List<Feature>> compareMap ) {

        FeatureProperty[] props1 = feature1.getProperties( propertyType.getName() );
        FeatureProperty[] props2 = feature2.getProperties( propertyType.getName() );

        if ( props1 != null && props2 != null ) {
            if ( props1.length != props2.length ) {
                return false;
            }
            // TODO handle different orders of multi properties
            for ( int j = 0; j < props1.length; j++ ) {
                Object value1 = props1[j].getValue();
                Object value2 = props2[j].getValue();

                if ( value1 == null && value2 == null ) {
                    continue;
                } else if ( !( value1 != null && value2 != null ) ) {
                    return false;
                }

                if ( value1 instanceof Feature ) {
                    // complex valued property (subfeature)
                    if ( !( value2 instanceof Feature ) ) {
                        return false;
                    }
                    Feature subfeature1 = (Feature) value1;
                    Feature subfeature2 = (Feature) value2;

                    if ( !compareFeatures( subfeature1, subfeature2, compareMap ) ) {
                        return false;
                    }
                } else {
                    if ( value1 instanceof Geometry ) {
                        String msg = "Check for equal geometry properties is not implemented yet. "
                                     + "Do not set 'identityPart' to true in geometry property " + "definitions.";
                        throw new RuntimeException( msg );
                    }
                    // simple valued property
                    if ( !value1.equals( value2 ) ) {
                        return false;
                    }
                }
            }
        } else if ( !( props1 == null && props2 == null ) ) {
            return false;
        }
        return true;
    }

    /**
     * Checks that there are no root features in the collection that are "equal".
     * <p>
     * After disambiguation these features have the same feature id.
     *
     * @throws DatastoreException
     */
    private void checkForEqualRootFeatures()
                            throws DatastoreException {
        Set<String> rootFIDs = new HashSet<String>();
        for ( int i = 0; i < fc.size(); i++ ) {
            String fid = fc.getFeature( i ).getId();
            if ( rootFIDs.contains( fid ) ) {
                String msg = Messages.getMessage( "WFS_SAME_ROOT_FEATURE_ID" );
                throw new DatastoreException( msg );
            }
            rootFIDs.add( fid );
        }
    }

    /**
     * Determines the feature type of all features (and subfeatures) in the given feature collection and builds a lookup
     * map.
     *
     * @param fc
     * @return lookup map that maps each feature instance to it's feature type
     */
    private Map<MappedFeatureType, Set<Feature>> buildFeatureTypeMap( FeatureCollection fc ) {
        LOG.logDebug( "Building feature map." );
        Map<MappedFeatureType, Set<Feature>> ftMap = new HashMap<MappedFeatureType, Set<Feature>>();
        for ( int i = 0; i < fc.size(); i++ ) {
            addToFeatureTypeMap( fc.getFeature( i ), ftMap );
        }
        return ftMap;
    }

    /**
     * Recursively adds the given feature (and it's subfeatures) to the given map. To cope with cyclic features, the
     * recursion ends if the feature instance is already present in the map.
     *
     * @param feature
     *            feature instance to add
     * @param ftMap
     */
    private void addToFeatureTypeMap( Feature feature, Map<MappedFeatureType, Set<Feature>> ftMap ) {

        MappedFeatureType ft = (MappedFeatureType) feature.getFeatureType();
        Set<Feature> features = ftMap.get( ft );
        if ( features == null ) {
            features = new HashSet<Feature>();
            ftMap.put( ft, features );
        } else {
            if ( features.contains( feature ) ) {
                return;
            }
        }
        features.add( feature );

        // recurse into subfeatures
        FeatureProperty[] properties = feature.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            Object propertyValue = properties[i].getValue();
            if ( propertyValue instanceof Feature ) {
                Feature subFeature = (Feature) propertyValue;
                addToFeatureTypeMap( subFeature, ftMap );
            }
        }
    }

    /**
     * Ensures that all features with the same feature id refer to the same feature instance.
     * <p>
     * Xlinked content is used for every subfeature that has been encountered already (or is a root feature of the
     * collection).
     * <p>
     * Root features are never replaced, because {@link #assignFIDsAndRepresenters()} ensures that root features always
     * represent themselves.
     */
    private void replaceDuplicateFeatures() {

        Set<String> xlinkFIDs = new HashSet<String>();

        // ensure that root features are always referred to by xlink properties
        for ( int i = 0; i < this.fc.size(); i++ ) {
            Feature feature = this.fc.getFeature( i );
            xlinkFIDs.add( feature.getId() );
        }

        for ( int i = 0; i < this.fc.size(); i++ ) {
            Feature feature = this.fc.getFeature( i );
            replaceDuplicateFeatures( feature, xlinkFIDs );
        }
    }

    /**
     * Replaces all subfeatures of the given feature instance by their "representers".
     * <p>
     * Xlinked content is used for every subfeature that has been encountered already (or that is a root feature of the
     * collection).
     *
     * @param feature
     * @param xlinkFIDs
     */
    private void replaceDuplicateFeatures( Feature feature, Set<String> xlinkFIDs ) {

        LOG.logDebug( "Replacing in feature: '" + feature.getName() + "', " + feature.getId() );
        xlinkFIDs.add( feature.getId() );

        FeatureProperty[] properties = feature.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            Object value = properties[i].getValue();
            if ( value != null && value instanceof Feature ) {

                Feature subfeature = (Feature) value;
                String fid = subfeature.getId();
                subfeature = this.representerMap.get( fid );

                FeatureProperty oldProperty = properties[i];
                FeatureProperty newProperty = null;

                if ( !xlinkFIDs.contains( fid ) ) {
                    // first occurence in feature collection
                    LOG.logDebug( "Not-XLink feature property: " + fid );
                    newProperty = FeatureFactory.createFeatureProperty( oldProperty.getName(), subfeature );
                    replaceDuplicateFeatures( subfeature, xlinkFIDs );
                } else {
                    // successive occurence in feature collection (use XLink)
                    LOG.logDebug( "XLink feature property: " + fid );
                    newProperty = new XLinkedFeatureProperty( oldProperty.getName(), fid );
                    newProperty.setValue( subfeature );
                }
                feature.replaceProperty( oldProperty, newProperty );
            }
        }
    }
}
