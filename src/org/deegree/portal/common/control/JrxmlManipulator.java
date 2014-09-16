package org.deegree.portal.common.control;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.portal.context.LayerGroup;
import org.deegree.portal.context.MMLayer;
import org.deegree.portal.context.MapModel;
import org.deegree.portal.context.MapModelVisitor;
import org.deegree.portal.context.ViewContext;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Manipulates the jrxml template.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class JrxmlManipulator {

    private static final NamespaceContext nsc = CommonNamespaces.getNamespaceContext();

    XMLFragment manipulateJrxml( PrintMetadata printMetadata, String path, ViewContext vc,
                                 Map<String, String> parameterName2Legends )
                            throws MalformedURLException, IOException, SAXException, XMLParsingException, Exception {
        File file = new File( path );
        XMLFragment xml = new XMLFragment( file );

        // manipulate Jasper template DOM tree to add a list of all visible
        // layers and their parents
        String xpath = "jasper:detail/jasper:band/jasper:frame/jasper:reportElement[./@key = 'layerList']";
        Element element = (Element) XMLTools.getNode( xml.getRootElement(), xpath, nsc );
        if ( element != null ) {
            // get frame element
            element = (Element) element.getParentNode();
            List<Element> textFields = XMLTools.getElements( element, "jasper:staticText", nsc );
            // remove child node because they are just place holders
            for ( Element e : textFields ) {
                element.removeChild( e );
            }
            MapModel mapModel = vc.getGeneral().getExtension().getMapModel();
            // add visible layers and their parents

            mapModel.walkLayerTree( new Visitor( element, textFields.get( 0 ), textFields.get( 1 ),
                                                 printMetadata.getLegendMetadata().getSpacing() ) );
        }
        if ( !parameterName2Legends.isEmpty() ) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating( true );
            factory.setExpandEntityReferences( false );

            String xpathToParameter = "/jasper:jasperReport/jasper:parameter[./@name = 'LEGEND']";
            Element templateParamElement = (Element) XMLTools.getNode( xml.getRootElement(), xpathToParameter, nsc );
            Node paramParentNode = templateParamElement.getParentNode();

            String xpathToLegendBand = "/jasper:jasperReport/jasper:detail/jasper:band[jasper:image/jasper:reportElement/@key = 'legendTemplateBand']";
            Element templateLegendBandElement = (Element) XMLTools.getNode( xml.getRootElement(), xpathToLegendBand,
                                                                            nsc );
            Node legendBandParentNode = templateLegendBandElement.getParentNode();

            SortedSet<String> sortedLegendParameters = new TreeSet<String>( parameterName2Legends.keySet() );
            for ( String legendParameterName : sortedLegendParameters ) {
                manipulateParameter( templateParamElement, paramParentNode, legendParameterName );
                manipulateBand( templateLegendBandElement, legendBandParentNode, legendParameterName );
            }
            paramParentNode.removeChild( templateParamElement );
            legendBandParentNode.removeChild( templateLegendBandElement );
        }
        return xml;
    }

    private void manipulateBand( Element templateLegendBandElement, Node legendBandParentNode,
                                 String legendParameterName )
                            throws XMLParsingException {
        Element clonedLegendBandElement = (Element) templateLegendBandElement.cloneNode( true );
        String xpathToLegendBandParameter = "jasper:image/jasper:imageExpression";
        Element imgExpressionElement = (Element) XMLTools.getNode( clonedLegendBandElement, xpathToLegendBandParameter,
                                                                   nsc );
        Document ownerDocument = imgExpressionElement.getOwnerDocument();
        CDATASection newCData = ownerDocument.createCDATASection( "$P{" + legendParameterName + "}" );
        Node oldCData = imgExpressionElement.getFirstChild();
        imgExpressionElement.replaceChild( newCData, oldCData );
        legendBandParentNode.insertBefore( clonedLegendBandElement, templateLegendBandElement );
    }

    private void manipulateParameter( Element templateParamElement, Node paramParentNode, String legendParameterName ) {
        Element clonedParameterElement = (Element) templateParamElement.cloneNode( true );
        clonedParameterElement.setAttribute( "name", legendParameterName );
        paramParentNode.insertBefore( clonedParameterElement, templateParamElement );
    }

    /**
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author: apoth $
     * 
     * @version $Revision: 30945 $, $Date: 2011-05-30 10:44:58 +0200 (Mo, 30. Mai 2011) $
     */
    private static class Visitor implements MapModelVisitor {

        private Element parent;

        private Element groupTemplate;

        private Element layerTemplate;

        private int count = 0;

        private int spaceing;

        /**
         * 
         * @param parent
         * @param groupTemplate
         * @param layerTemplate
         */
        Visitor( Element parent, Element groupTemplate, Element layerTemplate, int spaceing ) {
            this.parent = parent;
            this.groupTemplate = groupTemplate;
            this.layerTemplate = layerTemplate;
            this.spaceing = spaceing;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.portal.context.MapModelVisitor#visit(org.deegree.portal.context.LayerGroup)
         */
        public void visit( LayerGroup layerGroup )
                                throws Exception {
            List<MMLayer> list = layerGroup.getLayers();
            for ( MMLayer mmLayer : list ) {
                if ( !mmLayer.isHidden() ) {
                    Element newGroup = (Element) groupTemplate.cloneNode( true );
                    Node n = XMLTools.getNode( newGroup, "jasper:reportElement/@y", nsc );
                    n.setNodeValue( Integer.toString( count * spaceing ) );
                    count++;
                    n = XMLTools.getNode( newGroup, "jasper:text/text()", nsc );
                    n.setNodeValue( layerGroup.getTitle() );
                    parent.appendChild( newGroup );
                    break;
                }
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.portal.context.MapModelVisitor#visit(org.deegree.portal.context.MMLayer)
         */
        public void visit( MMLayer layer )
                                throws Exception {
            if ( !layer.isHidden() ) {
                Element newLayer = (Element) layerTemplate.cloneNode( true );
                Node n = XMLTools.getNode( newLayer, "jasper:reportElement/@y", nsc );
                n.setNodeValue( Integer.toString( count * spaceing ) );
                count++;
                n = XMLTools.getNode( newLayer, "jasper:text/text()", nsc );
                n.setNodeValue( layer.getTitle() );
                parent.appendChild( newLayer );
            }
        }

    }

}