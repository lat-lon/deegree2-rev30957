//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.12.16 at 05:01:18 PM GMT 
//


package org.deegree.portal.cataloguemanager.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.isotc211.org/2005/gmd}MD_Distribution"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "mdDistribution"
})
@XmlRootElement(name = "distributionInfo")
public class DistributionInfo {

    @XmlElement(name = "MD_Distribution", required = true)
    protected MDDistribution mdDistribution;

    /**
     * Gets the value of the mdDistribution property.
     * 
     * @return
     *     possible object is
     *     {@link MDDistribution }
     *     
     */
    public MDDistribution getMDDistribution() {
        return mdDistribution;
    }

    /**
     * Sets the value of the mdDistribution property.
     * 
     * @param value
     *     allowed object is
     *     {@link MDDistribution }
     *     
     */
    public void setMDDistribution(MDDistribution value) {
        this.mdDistribution = value;
    }

}
