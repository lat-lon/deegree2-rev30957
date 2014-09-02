package org.deegree.portal.common.control;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import org.deegree.framework.util.Pair;
import org.deegree.portal.common.control.LegendImageWriter.LegendImage;

/**
 * Description of the test scenarios:
 * 
 * <pre>
 * Scenario 1:
 * 
 * Tests using this scenario: 
 *  * testSzenario1
 * 
 * Page dimension: 100 x 100
 * 
 *  ____ 0
 * |1 | |  
 * |__| |  
 * |____| 100
 * 0    100
 * 
 * Legend dimensions (width x height):
 *  1: 4x5
 * </pre>
 * 
 * <pre>
 * Scenario 2:
 * 
 * Tests using this scenario: 
 *  * testSzenario2
 * 
 * Page dimension: 100 x 50
 * 
 *  ________________   0
 * |    ||    || 4  |
 * | 1  || 2  ||____|
 * |    ||____||    |
 * |____||3 | ||    |  
 * |    ||__| ||    |
 * |    ||    ||    |
 * |____||____||____|  50
 * 0   30 35 65 70   100
 * 
 * Legend dimensions (width x height):
 *  1: 20x45
 *  2: 20x20
 *  3: 13x23
 *  4: 25x50
 * </pre>
 * 
 * <pre>
 * Scenario 3:
 * 
 * Tests using this scenario: 
 *  * testSzenario3
 * 
 * Page dimension: 100 x 85
 * 
 *  ________________   0
 * |    ||    || 3  |
 * | 1  || 2  ||____|
 * |    ||    ||    |
 * |    ||    ||    |  
 * |____||    ||    |
 * |    ||    ||    |
 * |____||____||____|  85
 * 0   30 35 65 70   100
 *  
 * Legend dimensions (width x height):
 *  1: 25x60
 *  2: 20x90 (must be scaled!)
 *  3: 14x20
 * 
 * </pre>
 * 
 * <pre>
 * Scenario 4:
 * 
 * Tests using this scenario: 
 *  * testSzenario4
 * 
 * Page dimension: 100 x 60
 * 
 *  ________________   0
 * |    ||    || 3  |
 * | 1  || 2  ||____|
 * |    ||    ||    |
 * |    ||    ||    |  
 * |    ||    ||    |
 * |    ||____||    |
 * |____||____||____|  60
 * 0   30 35 65 70   100
 * 
 *  ________________   0
 * |    ||    ||    |
 * | 4  ||    ||    |
 * |    ||    ||    |
 * |    ||    ||    |  
 * |    ||    ||    |
 * |____||    ||    |
 * |____||____||____|  60
 * 0   30 35 65 70   100
 *   
 * Legend dimensions (width x height):
 *  1: 25x60
 *  2: 20x45
 *  3: 14x20
 *  4: 25x50
 * 
 * </pre>
 * 
 * <pre>
 * Scenario 5:
 * 
 * Tests using this scenario: 
 *  * testSzenario5
 * 
 * Page dimension: 100 x 100
 * 
 *  ________________   0
 * |    ||    ||    |
 * | 1  ||  2 ||    |
 * |    ||____||    |
 * |    ||    ||    |  
 * |    ||__3_||    |
 * |____||    ||    |
 * |____||____||____|  100
 * 0   30 35 65 70   100
 *    
 * Legend dimensions (width x height):
 *  1: 25x60
 *  2: 20x45
 *  3: 80x141 (does not fit!)
 * 
 * </pre>
 * 
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class LegendImageWriterTest extends TestCase {

    private static final int SPACING = 5;

    private final LegendImageWriter legendComponent = new LegendImageWriter( null, null );

    public void testSzenario1()
                            throws Exception {
        int childWidth = 4;
        int childHeight = 5;
        Pair<String, URL> legend = createLegend( "test1", childWidth, childHeight );
        List<Pair<String, URL>> legends = createLegends( legend );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata( 100, 100 ),
                                                                                 legends );
        assertEquals( 1, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 1, childsOnPageOne.size() );

        LegendImage firstChildOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0, firstChildOnPageOne.getX() );
        assertEquals( 0, firstChildOnPageOne.getY() );
        assertEquals( childWidth, firstChildOnPageOne.getWidth() );
        assertEquals( childHeight, firstChildOnPageOne.getHeight() );
    }

    public void testSzenario2()
                            throws Exception {
        Pair<String, URL> legend1 = createLegend( "test1", 20, 45 );
        Pair<String, URL> legend2 = createLegend( "test2", 20, 20 );
        Pair<String, URL> legend3 = createLegend( "test3", 13, 23 );
        Pair<String, URL> legend4 = createLegend( "test4", 25, 50 );

        List<Pair<String, URL>> legends = createLegends( legend1, legend2, legend3, legend4 );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata( 100, 50 ),
                                                                                 legends );
        assertEquals( 1, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 4, childsOnPageOne.size() );

        LegendImage childOneOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0, childOneOnPageOne.getX() );
        assertEquals( 0, childOneOnPageOne.getY() );
        assertEquals( 20, childOneOnPageOne.getWidth() );
        assertEquals( 45, childOneOnPageOne.getHeight() );

        LegendImage childTwoOnPageOne = findChildByName( childsOnPageOne, "test2" );
        assertEquals( 0 + 30 + SPACING, childTwoOnPageOne.getX() );
        assertEquals( 0, childTwoOnPageOne.getY() );
        assertEquals( 20, childTwoOnPageOne.getWidth() );
        assertEquals( 20, childTwoOnPageOne.getHeight() );

        LegendImage childThreeOnPageOne = findChildByName( childsOnPageOne, "test3" );
        assertEquals( 0 + 30 + SPACING, childThreeOnPageOne.getX() );
        assertEquals( 0 + 20, childThreeOnPageOne.getY() );
        assertEquals( 13, childThreeOnPageOne.getWidth() );
        assertEquals( 23, childThreeOnPageOne.getHeight() );

        LegendImage childFourOnPageOne = findChildByName( childsOnPageOne, "test4" );
        assertEquals( 0 + 30 + SPACING + 30 + SPACING, childFourOnPageOne.getX() );
        assertEquals( 0, childFourOnPageOne.getY() );
        assertEquals( 25, childFourOnPageOne.getWidth() );
        assertEquals( 50, childFourOnPageOne.getHeight() );
    }

    public void testSzenario3()
                            throws Exception {
        Pair<String, URL> legend1 = createLegend( "test1", 25, 60 );
        Pair<String, URL> legend2 = createLegend( "test2", 20, 90 );
        Pair<String, URL> legend3 = createLegend( "test3", 14, 20 );

        List<Pair<String, URL>> legends = createLegends( legend1, legend2, legend3 );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata( 100, 85 ),
                                                                                 legends );
        assertEquals( 1, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 3, childsOnPageOne.size() );

        LegendImage childOneOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0, childOneOnPageOne.getX() );
        assertEquals( 0, childOneOnPageOne.getY() );
        assertEquals( 25, childOneOnPageOne.getWidth() );
        assertEquals( 60, childOneOnPageOne.getHeight() );

        LegendImage childTwoOnPageOne = findChildByName( childsOnPageOne, "test2" );
        assertEquals( 0 + 30 + SPACING, childTwoOnPageOne.getX() );
        assertEquals( 0, childTwoOnPageOne.getY() );
        assertEquals( 18, childTwoOnPageOne.getWidth() );
        assertEquals( 85, childTwoOnPageOne.getHeight() );

        LegendImage childThreeOnPageOne = findChildByName( childsOnPageOne, "test3" );
        assertEquals( 0 + 30 + SPACING + 30 + SPACING, childThreeOnPageOne.getX() );
        assertEquals( 0, childThreeOnPageOne.getY() );
        assertEquals( 14, childThreeOnPageOne.getWidth() );
        assertEquals( 20, childThreeOnPageOne.getHeight() );
    }

    public void testSzenario4()
                            throws Exception {
        Pair<String, URL> legend1 = createLegend( "test1", 25, 60 );
        Pair<String, URL> legend2 = createLegend( "test2", 20, 45 );
        Pair<String, URL> legend3 = createLegend( "test3", 14, 20 );
        Pair<String, URL> legend4 = createLegend( "test4", 25, 50 );

        List<Pair<String, URL>> legends = createLegends( legend1, legend2, legend3, legend4 );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata( 100, 60 ),
                                                                                 legends );
        assertEquals( 2, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 3, childsOnPageOne.size() );

        List<LegendImage> childsOnPageTwo = legendPages.get( 1 );
        assertEquals( 1, childsOnPageTwo.size() );

        LegendImage childOneOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0, childOneOnPageOne.getX() );
        assertEquals( 0, childOneOnPageOne.getY() );
        assertEquals( 25, childOneOnPageOne.getWidth() );
        assertEquals( 60, childOneOnPageOne.getHeight() );

        LegendImage childTwoOnPageOne = findChildByName( childsOnPageOne, "test2" );
        assertEquals( 0 + 30 + SPACING, childTwoOnPageOne.getX() );
        assertEquals( 0, childTwoOnPageOne.getY() );
        assertEquals( 20, childTwoOnPageOne.getWidth() );
        assertEquals( 45, childTwoOnPageOne.getHeight() );

        LegendImage childThreeOnPageOne = findChildByName( childsOnPageOne, "test3" );
        assertEquals( 0 + 30 + SPACING + 30 + SPACING, childThreeOnPageOne.getX() );
        assertEquals( 0, childThreeOnPageOne.getY() );
        assertEquals( 14, childThreeOnPageOne.getWidth() );
        assertEquals( 20, childThreeOnPageOne.getHeight() );

        LegendImage childOneOnPageTwo = findChildByName( childsOnPageTwo, "test4" );
        assertEquals( 0, childOneOnPageTwo.getX() );
        assertEquals( 0, childOneOnPageTwo.getY() );
        assertEquals( 25, childOneOnPageTwo.getWidth() );
        assertEquals( 50, childOneOnPageTwo.getHeight() );
    }

    public void testSzenario5()
                            throws Exception {
        Pair<String, URL> legend1 = createLegend( "test1", 25, 60 );
        Pair<String, URL> legend2 = createLegend( "test2", 20, 45 );
        Pair<String, URL> legend3 = createLegend( "test3", 80, 141 );

        List<Pair<String, URL>> legends = createLegends( legend1, legend2, legend3 );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata( 100, 100 ),
                                                                                 legends );
        assertEquals( 1, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 3, childsOnPageOne.size() );

        LegendImage childOneOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0, childOneOnPageOne.getX() );
        assertEquals( 0, childOneOnPageOne.getY() );
        assertEquals( 25, childOneOnPageOne.getWidth() );
        assertEquals( 60, childOneOnPageOne.getHeight() );

        LegendImage childTwoOnPageOne = findChildByName( childsOnPageOne, "test2" );
        assertEquals( 0 + 30 + SPACING, childTwoOnPageOne.getX() );
        assertEquals( 0, childTwoOnPageOne.getY() );
        assertEquals( 20, childTwoOnPageOne.getWidth() );
        assertEquals( 45, childTwoOnPageOne.getHeight() );

        LegendImage childThreeOnPageOne = findChildByName( childsOnPageOne, "test3" );
        assertEquals( 0 + 30 + SPACING + 30 + SPACING, childThreeOnPageOne.getX() );
        assertEquals( 0, childThreeOnPageOne.getY() );
    }

    public void testLegendFitsInColumnFits()
                            throws Exception {
        BufferedImage legendImage = createBufferedImage( 20, 20 );
        boolean legendFitsInColumn = legendComponent.legendFitsInColumn( createLegendMetadata( 100, 100 ), legendImage,
                                                                         30 );
        assertTrue( legendFitsInColumn );
    }

    public void testLegendFitsInColumnFitsExactHeight()
                            throws Exception {
        BufferedImage legendImage = createBufferedImage( 25, 50 );
        boolean legendFitsInColumn = legendComponent.legendFitsInColumn( createLegendMetadata( 100, 50 ), legendImage,
                                                                         30 );
        assertTrue( legendFitsInColumn );
    }

    public void testLegendFitsInColumnFitsExactWidth()
                            throws Exception {
        BufferedImage legendImage = createBufferedImage( 35, 60 );
        boolean legendFitsInColumn = legendComponent.legendFitsInColumn( createLegendMetadata( 125, 70 ), legendImage,
                                                                         39 );
        assertTrue( legendFitsInColumn );
    }

    public void testLegendFitsInColumnFitsScaleHeight()
                            throws Exception {
        BufferedImage legendImage = createBufferedImage( 35, 60 );
        boolean legendFitsInColumn = legendComponent.legendFitsInColumn( createLegendMetadata( 150, 56 ), legendImage,
                                                                         46 );
        assertTrue( legendFitsInColumn );
    }

    public void testLegendFitsInColumnFitsScaleWidth()
                            throws Exception {
        BufferedImage legendImage = createBufferedImage( 35, 60 );
        boolean legendFitsInColumn = legendComponent.legendFitsInColumn( createLegendMetadata( 120, 60 ), legendImage,
                                                                         37 );
        assertTrue( legendFitsInColumn );
    }

    public void testLegendFitsInColumnFitsNotInHeight()
                            throws Exception {
        BufferedImage legendImage = createBufferedImage( 20, 45 );
        boolean legendFitsInColumn = legendComponent.legendFitsInColumn( createLegendMetadata( 100, 30 ), legendImage,
                                                                         30 );
        assertFalse( legendFitsInColumn );
    }

    public void testLegendFitsInColumnFitsNotInWidth()
                            throws Exception {
        BufferedImage legendImage = createBufferedImage( 35, 60 );
        boolean legendFitsInColumn = legendComponent.legendFitsInColumn( createLegendMetadata( 100, 100 ), legendImage,
                                                                         30 );
        assertFalse( legendFitsInColumn );
    }

    private LegendMetadata createLegendMetadata( int width, int height ) {
        return new LegendMetadata( true, width, height, null, 3, SPACING, 90,
                                   "Die Legende des Themas %s ist zu groß für den PDF-Druck und wird nicht angezeigt." );
    }

    private List<Pair<String, URL>> createLegends( Pair<String, URL>... legendsToAdd ) {
        List<Pair<String, URL>> legends = new ArrayList<Pair<String, URL>>();
        for ( Pair<String, URL> legendToAdd : legendsToAdd ) {
            legends.add( legendToAdd );
        }
        return legends;
    }

    private Pair<String, URL> createLegend( String name, int childWidth, int childHeight ) {
        URL legendUrl = loadImage( childWidth, childHeight );
        return new Pair<String, URL>( name, legendUrl );
    }

    private URL loadImage( int childWidth, int childHeight ) {
        String imgName = "legendImage_" + childWidth + "_" + childHeight + ".png";
        return LegendImageWriterTest.class.getResource( imgName );
    }

    private BufferedImage createBufferedImage( int childWidth, int childHeight )
                            throws IOException {
        URL image = loadImage( childWidth, childHeight );
        return ImageIO.read( image );
    }

    private LegendImage findChildByName( List<LegendImage> childsOnPageOne, String name ) {
        for ( LegendImage legendImage : childsOnPageOne ) {
            if ( name.equals( legendImage.getName() ) ) {
                return legendImage;
            }
        }
        return null;
    }

}