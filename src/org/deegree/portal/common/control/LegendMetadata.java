//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
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
package org.deegree.portal.common.control;

/**
 * Contains metadata about the report.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class LegendMetadata {

    private final int maxSizeToFitInPercent;

    private final String toLargeMsg;

    private final boolean isDynamicLegend;

    private final int legendWidth;

    private final int legendHeight;

    private final String legendBgColor;

    private final int columns;

    private final int spacing;

    private final String fontFamily;

    private final float fontSize;
    
    public LegendMetadata( boolean isDynamicLegend, int legendWidth, int legendHeight, String legendBgColor,
                           int columns, int spacing, int maxSizeToFitInPercent, String toLargeMsg, String fontFamily, float fontSize) {
        this.isDynamicLegend = isDynamicLegend;
        this.legendWidth = legendWidth;
        this.legendHeight = legendHeight;
        this.legendBgColor = legendBgColor;
        this.columns = columns;
        this.spacing = spacing;
        this.maxSizeToFitInPercent = maxSizeToFitInPercent;
        this.toLargeMsg = toLargeMsg;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;

    }

    public boolean isDynamicLegend() {
        return isDynamicLegend;
    }

    public int getWidth() {
        return legendWidth;
    }

    public int getHeight() {
        return legendHeight;
    }

    public String getLegendBgColor() {
        return legendBgColor;
    }

    public int getColumns() {
        return columns;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getMaxSizeToFitInPercent() {
        return maxSizeToFitInPercent;
    }

    public String getToLargeMsg() {
        return toLargeMsg;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public float getFontSize() {
        return fontSize;
    }

}