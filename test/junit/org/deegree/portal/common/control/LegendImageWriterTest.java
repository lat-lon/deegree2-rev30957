package org.deegree.portal.common.control;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deegree.portal.common.control.LegendImageWriter.LegendImage;

/**
 * Description of the test scenarios:
 * 
 * <pre>
 * Scenario 1:
 * 
 * Tests using this scenario: 
 *  * testLayoutOnMultiplePagesWithOneLegendFittingOnOnePage
 * 
 * Page dimension: 100 x 83
 * 
 *  _______ 0
 * | |1 |   
 * | |__|   
 * |_|_____ 83
 * 
 *   10 14
 * 
 * Legend dimensions (width x height):
 *  1: 4x5
 * </pre>
 * 
 * <pre>
 * Scenario 2:
 * 
 * Tests using this scenario: 
 *  * testLayoutOnMultiplePagesWithMultipleLegendsFittingOnOnePage
 * 
 * Page dimension: 100 x 83
 * 
 *  ____________________   0
 * | |    |   | 2 |   | |
 * | | 3  | 1 |___|   | |
 * | |    |___|       | |
 * | |____|4 |        | |  
 * | |    |__|        | |
 * | |                | |
 * |_|________________|_|  83
 *   10   35 55   75 90 100
 * 
 * Legend dimensions (width x height):
 *  1: 20x45
 *  2: 20x20
 *  3: 25x50
 *  4: 13x23
 * </pre>
 * 
 * <pre>
 * Scenario 3:
 * 
 * Tests using this scenario: 
 *  * testLayoutOnMultiplePagesWithMultipleLegendsOneToHeightFittingOnOnePage
 * 
 * Page dimension: 100 x 83
 * 
 *  ____________________   0
 * | |   |   | 3|     | |
 * | | 2 | 1 |__|     | |
 * | |   |   |        | |
 * | |   |   |        | |  
 * | |   |___|        | |
 * | |___|            | |
 * |_|________________|_|   83
 *   10   30 55   75 90 100
 * 
 * Legend dimensions (width x height):
 *  1: 25x60
 *  2: 20x126
 *  3: 14x20
 * 
 * </pre>
 * 
 * <pre>
 * Scenario 4:
 * 
 * Tests using this scenario: 
 *  * testLayoutOnMultiplePagesWithMultipleLegendsFittingOnTwoPages
 * 
 * Page dimension: 100 x 83
 * 
 *  ____________________   0
 * | |      |     |   | |
 * | |  1   |  2  |   | |
 * | |      |     |   | |
 * | |      |     |   | |  
 * | |      |_____|   | |
 * | |______|         | |
 * |_|________________|_|  83 
 *   10     45   82  90 100
 * 100
 *  ____________________   0
 * | |   |            | |
 * | | 3 |            | |
 * | |   |            | |
 * | |___|            | |  
 * | |                | |
 * | |                | |
 * |_|________________|_|  83
 *   10  20         90 100
 *   
 * Legend dimensions (width x height):
 *  1: 25x60
 *  2: 20x126
 *  3: 14x20
 * 
 * </pre>
 * 
 * <pre>
 * Scenario 5:
 * 
 * Tests using this scenario: 
 *  * testLayoutOnMultiplePagesWithMultipleLegendsTooHeightFittingOnTwoPages
 * 
 * Page dimension: 100 x 83
 * 
 *  ________________  0
 * |      |     |   |
 * |  1   |  2  |   |
 * |      |     |   |
 * |      |     |   |  
 * |      |     |   |
 * |      |     |   |
 * |______|_____|___| 83
 * 0               100
 * 
 *  ________________  0
 * |      |         |
 * |  3   |         |
 * |      |         |
 * |      |         |  
 * |      |         |
 * |      |         |
 * |______|_________| 83
 * 0               100
 *   
 * Legend dimensions (width x height):
 *  1: 160x280
 *  2: 120x212
 *  3: 80x141
 * 
 * </pre>
 * 
 * <pre>
 * Scenario 5:
 * 
 * Tests using this scenario: 
 *  * testLayoutOnMultiplePagesWithMultipleLegendsTooHeightFittingOnThreePages
 * 
 * Page dimension: 100 x 83
 * 
 *  ________________  0
 * |      |         |
 * |  1   |         |
 * |      |         |
 * |      |         |  
 * |      |         |
 * |      |         |
 * |______|_________| 83
 * 0               100
 * 
 *  _________________ 0
 * |      |         |
 * |  2   |         |
 * |      |         |
 * |      |         |  
 * |      |         |
 * |      |         |
 * |______|_________|  83
 * 0               100
 *   
 *  _________________  0
 * |      |         |
 * |  3   |         |
 * |      |         |
 * |      |         |  
 * |      |         |
 * |      |         |
 * |______|_________| 83
 * 0               100
 *   
 * Legend dimensions (width x height):
 *  1: 160x252
 *  2: 120x189
 *  3: 80x126
 * 
 * </pre>
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class LegendImageWriterTest extends TestCase {

    private static final int SPACING = 10;

    private static final int PAGE_WIDTH = 100;

    private static final int PAGE_HEIGHT = 83;

    private final LegendImageWriter legendComponent = new LegendImageWriter( null, null );

    public void testLayoutOnMultiplePagesWithOneLegendFittingOnOnePage()
                            throws Exception {
        int childWidth = 4;
        int childHeight = 5;
        String[] legend = createLegend( "test1", childWidth, childHeight );
        List<String[]> legends = createLegends( legend );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata(), legends );
        assertEquals( 1, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 1, childsOnPageOne.size() );

        LegendImage firstChildOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0, firstChildOnPageOne.getX() );
        assertEquals( 0, firstChildOnPageOne.getY() );
        assertEquals( childWidth, firstChildOnPageOne.getWidth() );
        assertEquals( childHeight, firstChildOnPageOne.getHeight() );
    }

    public void testLayoutOnMultiplePagesWithMultipleLegendsFittingOnOnePage()
                            throws Exception {
        String[] legend1 = createLegend( "test1", 20, 45 );
        String[] legend2 = createLegend( "test2", 20, 20 );
        String[] legend3 = createLegend( "test3", 25, 50 );
        String[] legend4 = createLegend( "test4", 13, 23 );

        List<String[]> legends = createLegends( legend1, legend2, legend3, legend4 );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata(), legends );
        assertEquals( 1, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 4, childsOnPageOne.size() );

        LegendImage childThreeOnPageOne = findChildByName( childsOnPageOne, "test3" );
        assertEquals( 0, childThreeOnPageOne.getX() );
        assertEquals( 0, childThreeOnPageOne.getY() );
        assertEquals( 25, childThreeOnPageOne.getWidth() );
        assertEquals( 50, childThreeOnPageOne.getHeight() );

        LegendImage childOneOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0 + 25, childOneOnPageOne.getX() );
        assertEquals( 0, childOneOnPageOne.getY() );
        assertEquals( 20, childOneOnPageOne.getWidth() );
        assertEquals( 45, childOneOnPageOne.getHeight() );

        LegendImage childFourOnPageOne = findChildByName( childsOnPageOne, "test4" );
        assertEquals( 0 + 25, childFourOnPageOne.getX() );
        assertEquals( 0 + 45, childFourOnPageOne.getY() );
        assertEquals( 13, childFourOnPageOne.getWidth() );
        assertEquals( 23, childFourOnPageOne.getHeight() );

        LegendImage childTwoOnPageOne = findChildByName( childsOnPageOne, "test2" );
        assertEquals( 0 + 25 + 20, childTwoOnPageOne.getX() );
        assertEquals( 0, childTwoOnPageOne.getY() );
        assertEquals( 20, childTwoOnPageOne.getWidth() );
        assertEquals( 20, childTwoOnPageOne.getHeight() );
    }

    public void testLayoutOnMultiplePagesWithMultipleLegendsOneToHeightFittingOnOnePage()
                            throws Exception {
        String[] legend1 = createLegend( "test1", 25, 60 );
        String[] legend2 = createLegend( "test2", 20, 90 );
        String[] legend3 = createLegend( "test3", 20, 45 );

        List<String[]> legends = createLegends( legend1, legend2, legend3 );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata(), legends );
        assertEquals( 1, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 3, childsOnPageOne.size() );

        LegendImage childTwoOnPageOne = findChildByName( childsOnPageOne, "test2" );
        assertEquals( 0, childTwoOnPageOne.getX() );
        assertEquals( 0, childTwoOnPageOne.getY() );
        assertEquals( 18, childTwoOnPageOne.getWidth() );
        assertEquals( 83, childTwoOnPageOne.getHeight() );

        LegendImage childOneOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0 + 18, childOneOnPageOne.getX() );
        assertEquals( 0, childOneOnPageOne.getY() );
        assertEquals( 25, childOneOnPageOne.getWidth() );
        assertEquals( 60, childOneOnPageOne.getHeight() );

        LegendImage childThreeOnPageOne = findChildByName( childsOnPageOne, "test3" );
        assertEquals( 0 + 18 + 25, childThreeOnPageOne.getX() );
        assertEquals( 0, childThreeOnPageOne.getY() );
        assertEquals( 20, childThreeOnPageOne.getWidth() );
        assertEquals( 45, childThreeOnPageOne.getHeight() );
    }

    public void testLayoutOnMultiplePagesWithMultipleLegendsTooHeightFittingOnThreePages()
                            throws Exception {
        String[] legend1 = createLegend( "test1", 160, 280 );
        String[] legend2 = createLegend( "test2", 120, 212 );
        String[] legend3 = createLegend( "test3", 80, 141 );

        List<String[]> legends = createLegends( legend1, legend2, legend3 );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata(), legends );
        assertEquals( 2, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 2, childsOnPageOne.size() );

        List<LegendImage> childsOnPageTwo = legendPages.get( 1 );
        assertEquals( 1, childsOnPageTwo.size() );

        LegendImage childOneOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0, childOneOnPageOne.getX() );
        assertEquals( 0, childOneOnPageOne.getY() );
        assertEquals( 47, childOneOnPageOne.getWidth() );
        assertEquals( 83, childOneOnPageOne.getHeight() );

        LegendImage childTwoOnPageOne = findChildByName( childsOnPageOne, "test2" );
        assertEquals( 0 + 47, childTwoOnPageOne.getX() );
        assertEquals( 0, childTwoOnPageOne.getY() );
        assertEquals( 46, childTwoOnPageOne.getWidth() );
        assertEquals( 83, childTwoOnPageOne.getHeight() );

        LegendImage childOneOnPageTwo = findChildByName( childsOnPageTwo, "test3" );
        assertEquals( 0, childOneOnPageTwo.getX() );
        assertEquals( 0, childOneOnPageTwo.getY() );
        assertEquals( 47, childOneOnPageTwo.getWidth() );
        assertEquals( 83, childOneOnPageTwo.getHeight() );
    }

    public void testLayoutOnMultiplePagesWithMultipleLegendsFittingOnTwoPages()
                            throws Exception {
        String[] legend1 = createLegend( "test1", 25, 60 );
        String[] legend2 = createLegend( "test2", 37, 55 );
        String[] legend3 = createLegend( "test3", 80, 30 );

        List<String[]> legends = createLegends( legend1, legend2, legend3 );

        List<List<LegendImage>> legendPages = legendComponent.createLegendPages( createLegendMetadata(), legends );
        assertEquals( 2, legendPages.size() );

        List<LegendImage> childsOnPageOne = legendPages.get( 0 );
        assertEquals( 2, childsOnPageOne.size() );

        List<LegendImage> childsOnPageTwo = legendPages.get( 1 );
        assertEquals( 1, childsOnPageTwo.size() );

        LegendImage childOneOnPageOne = findChildByName( childsOnPageOne, "test1" );
        assertEquals( 0, childOneOnPageOne.getX() );
        assertEquals( 0, childOneOnPageOne.getY() );
        assertEquals( 25, childOneOnPageOne.getWidth() );
        assertEquals( 60, childOneOnPageOne.getHeight() );

        LegendImage childTwoOnPageOne = findChildByName( childsOnPageOne, "test2" );
        assertEquals( 0 + 25, childTwoOnPageOne.getX() );
        assertEquals( 0, childTwoOnPageOne.getY() );
        assertEquals( 37, childTwoOnPageOne.getWidth() );
        assertEquals( 55, childTwoOnPageOne.getHeight() );

        LegendImage childOneOnPageTwo = findChildByName( childsOnPageTwo, "test3" );
        assertEquals( 0, childOneOnPageTwo.getX() );
        assertEquals( 0, childOneOnPageTwo.getY() );
        assertEquals( 80, childOneOnPageTwo.getWidth() );
        assertEquals( 30, childOneOnPageTwo.getHeight() );
    }

    private LegendMetadata createLegendMetadata() {
        return new LegendMetadata( true, PAGE_WIDTH, PAGE_HEIGHT, null, SPACING );
    }

    private List<String[]> createLegends( String[]... legendsToAdd ) {
        List<String[]> legends = new ArrayList<String[]>();
        for ( String[] legendToAdd : legendsToAdd ) {
            legends.add( legendToAdd );
        }
        return legends;
    }

    private String[] createLegend( String name, int childWidth, int childHeight ) {
        String imgName = "legendImage_" + childWidth + "_" + childHeight + ".png";
        URL legendUrl = LegendImageWriterTest.class.getResource( imgName );
        return new String[] { name, legendUrl.toExternalForm() };
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