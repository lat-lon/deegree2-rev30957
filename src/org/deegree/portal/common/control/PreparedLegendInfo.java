package org.deegree.portal.common.control;

/**
 * Contains infos about the prepared legends
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class PreparedLegendInfo {

    private final String legendId;

    private final String urlToLegend;

    private final int posX;

    private final int posY;

    private final int width;

    private final int height;

    public PreparedLegendInfo( String legendId, String urlToLegend ) {
        this( legendId, urlToLegend, 0, 0, -1, -1 );
    }

    public PreparedLegendInfo( String legendId, String urlToLegend, int posX, int posY, int width, int height ) {
        this.legendId = legendId;
        this.urlToLegend = urlToLegend;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public String getLegendId() {
        return legendId;
    }

    public String getUrlToLegend() {
        return urlToLegend;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}