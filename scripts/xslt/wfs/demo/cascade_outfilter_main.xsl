<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:wfs="http://www.opengis.net/wfs" xmlns:ogc="http://www.opengis.net/ogc" 
xmlns:app="http://www.deegree.org/app"
xmlns:gml="http://www.opengis.net/gml"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="NSP">a:a</xsl:param>
	<xsl:template match="wfs:FeatureCollection">
			<xsl:copy-of select="."/>	
	</xsl:template>
	<xsl:template match="ServiceExceptionReport">
		<wfs:FeatureCollection numberOfFeatures="0"/>
	</xsl:template>
	<xsl:template match="ExceptionReport">
		<wfs:FeatureCollection numberOfFeatures="0"/>
	</xsl:template>	
</xsl:stylesheet>
