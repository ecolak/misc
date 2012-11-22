/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import task.CLCarCrawler;

/**
 *
 * @author ecolak
 */
public class StringTest {

    private String response = "<h4 class=\"ban\">Thu Feb 24</h4>"
                + "<p class=\"row\"> "
                + "<span class=\"ih\">&nbsp;</span> "
                + "<a href=\"http://sfbay.craigslist.org/eby/ctd/2231456705.html\">"
                + "<div id=\"userbody\">"
                + "<font color=\"red\">&nbsp;2007 Chevrolet Equinox SUV LS</font></a> - $14995<font size=\"-1\"> (Gold River)</font>"
                + "<div>Test div in here</div>"
                + " <span class=\"p\"> img</span> <small class=\"gc\"><a href=\"/ctd/\">dealer</a>"
                + "</small><br class=\"c\"> </p> "
                + "</div>"
                + "<p class=\"row\"> "
                + "<span class=\"ih\">&nbsp;</span> "
                + "<a href=\"http://sfbay.craigslist.org/nby/ctd/2231456713.html\">"
                + "2001 Nissan Pathfinder SE 4x4 3.5L V6 ** Automatic ** Loaded w/Options? This is </a> - "
                + "$8999<font size=\"-1\"> (santa rosa)</font> <span class=\"p\"> img</span> "
                + "<small class=\"gc\"><a href=\"/ctd/\">dealer</a></small><br class=\"c\"> "
                + "</p> "
                + "<p class=\"row\">"
                + "<span class=\"ih\">&nbsp;</span>"
                + "<a href=\"http://sfbay.craigslist.org/eby/ctd/2240311532.html\">"
                + "2003 Acura TL Sedan 3.2</a> - $7988<font size=\"-1\"> (Walnut Creek)</font> "
                + "<span class=\"p\"> img</span> <small class=\"gc\"><a href=\"/ctd/\">dealer</a></small>"
                + "<br class=\"c\"></p>"
                + "<p align=\"center\"><font size=\"4\"><a href=\"index100.html\">next 100 postings</a></font>";

    public StringTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void stripHtml() {
        response = response.replaceAll("(\\</?(b|i|font|small|span|strong|div|ul|ol|li|h1|h2|h3|h4|h5|h6|h7).*?\\>|&nbsp;)", "");
        //response = response.replaceAll("\\<[^a].*?\\>", "");
        System.out.println(response);
    }

    //@Test
    public void testUserBody() {
        String userBody = null;
        Matcher m = CLCarCrawler.USERBODY_PATTERN.matcher(response);
        if(m.find()) {
            userBody = m.group(1);
        }
        System.out.println(userBody);
    }

    //@Test
    public void testLDistance() {
        String listing = "2009 Toyota Rav4 Sport ";
        CLCarCrawler c = new CLCarCrawler();
        Map<String, String> map = c.tokenizeListing(listing);
        String lookFor = c.findToken(map, "rav4");
        System.out.println("loook for: " + lookFor);
    }

    //@Test
    public void testNeighbors() {
        String testStr = "115k Mercedes 300 SEL with automatic transmission";
        Map<String, String> map = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(testStr);
        String s1 = "";
        String s2 = "";
        while (st.hasMoreTokens()) {
            if(StringUtils.isBlank(s1)) {
                s1 = st.nextToken().toLowerCase();
            }
                
            try {
                s2 = st.nextToken().toLowerCase();
            } catch(Exception e) {}
           
            map.put(s1,s2);
            s1 = s2;
        }
        map.put(s1, s2);
        
        for(String k : map.keySet()) {
            System.out.println(k + "," + map.get(k));
        }
    }

}