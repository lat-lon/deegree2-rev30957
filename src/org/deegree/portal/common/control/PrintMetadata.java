package org.deegree.portal.common.control;

/**
 * Wraps metadata about the map print.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class PrintMetadata {

    private final int mapWidth;

    private final int mapHeight;

    private final LegendMetadata legendMetadata;

    public PrintMetadata( int mapWidth, int mapHeight, LegendMetadata legendMetadata ) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.legendMetadata = legendMetadata;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public LegendMetadata getLegendMetadata() {
        return legendMetadata;
    }

}
