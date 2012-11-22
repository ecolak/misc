/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import dao.CLCar;
import dao.CarMake;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import task.CLCarCrawler;

/**
 *
 * @author ecolak
 */
public class MainTest {

    public static final DateFormat CL_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd");
    String test = "<h4 class=\"ban\">Thu Feb 24</h4>"
                + "<p class=\"row\"> "
                + "<span class=\"ih\">&nbsp;</span> "
                + "<a href=\"http://sfbay.craigslist.org/eby/ctd/2231456705.html\">"
                + "2007 Chevrolet Equinox SUV LS</a> - $14995<font size=\"-1\"> (Gold River)</font>"
                + " <span class=\"p\"> img</span> <small class=\"gc\"><a href=\"/ctd/\">dealer</a>"
                + "</small><br class=\"c\"> </p> "
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

    public MainTest() {
    }


    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    //@Test
    public void testStuff() throws Exception {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        String dateRegex = "<h4 class=\"ban\">(.+?)</h4>";
        Pattern d = Pattern.compile(dateRegex);
        Matcher dMatcher = d.matcher(test);
        while(dMatcher.find()) {
            System.out.println("Date: " + dMatcher.group(1));
            Date date = CL_DATE_FORMAT.parse(dMatcher.group(1));
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
            date = cal.getTime();
            System.out.println(date.toString());
        }

        String regex = "<p class=\"row\">.*?<a href=\"(.*?)\">(.*?)</a>.*?(\\$\\d{1,7}).*?(\\(.+?\\)).*?</p>";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(test);

        int i = 0;
        while(m.find()) {
            i++;
            System.out.println("Link: " + m.group(1));
            System.out.println("Listing: " + m.group(2));
            System.out.println("Price: " + m.group(3));
            System.out.println("Location: " + m.group(4));
        }
        System.out.println(i + " matches found");

        //regex = "<a href=\"([^<]*?)\">next 100 postings</a>";
        regex = "<a href=\"([^<]*?)\">next 100 postings</a>";
        p = Pattern.compile(regex);
        m = p.matcher(test);
        if(m.find()) {
            System.out.println("next url: " + m.group(1));
        }
    }

    //@Test
    public void getLocation() {
        String listing = "<p class=\"row\">"
                + "<span class=\"ih\">&nbsp;</span>"
                + "<a href=\"http://sfbay.craigslist.org/eby/ctd/2240311532.html\">"
                + "2003 Acura TL Sedan 3.2</a> - $7988<font size=\"-1\"> (Walnut Creek)</font> "
                + "<span class=\"p\"> img</span> <small class=\"gc\"><a href=\"/ctd/\">dealer</a></small>"
                + "<br class=\"c\"></p>";

        Matcher m = CLCarCrawler.LOCATION_PATTERN.matcher(listing);
        if(m.find()) {
            System.out.println("Location: " + m.group(1));
        }
    }

    //@Test
    public void getEmailAddr() {
        String stuff = "Reply to: <a href=\"mailto:sale-9ke87-2242306315@craigslist.org?"
                + "subject=Beautiful%202001%20Audi%20S4%20-%20%2410000%20(san%20jose%20west)"
                + "&amp;body=%0A%0Ahttp%3A%2F%2Fsfbay.craigslist.org%2Fsby%2Fcto%2F2242306315.html%0A\">"
                + "sale-9ke87-2242306315@craigslist.org</a> <sup>"
                + "[<a href=\"http://www.craigslist.org/about/help/replying_to_posts\" "
                + "target=\"_blank\">Errors when replying to ads?</a>]</sup><br> <hr> ";

        Matcher m = CLCarCrawler.CONTACT_EMAIL_PATTERN.matcher(stuff);
        String email = null;
        if(m.find()) {
            System.out.println("Email: " + m.group(1));
            //email = m.group(1);
        }
        //assertEquals("sale-9ke87-2242306315@craigslist.org", email);
    }

    //@Test
    public void getPhoneNumber() {
        String stuff = "<br> Her title is clean…NO SALVAGE…NO ACCIDENTS at just about 51k miles. <br>"
                + "<br> If you want a test drive, email me. Phone: 716-903-7835.  "
                + "I’m a Federal Officer so don’t even think of trying "
                + "to pull a fast one. <br> <br> ";

        CLCarCrawler c = new CLCarCrawler();
        String phone = c.getPhoneFromListing(stuff);
        System.out.println("Phone: " + phone);
        
        //assertEquals("716-903-7835", phone);
    }

    @Test
    public void getMileage() {
        String stuff = "<br> Her title is clean…NO SALVAGE…NO ACCIDENTS at just about 111XXX miles </b>. <br>"
                + "<br> If you want a test drive, email me.  You must have a copy of your driver’s "
                + "license and insurance to do so. I’m a Federal Officer so don’t even think of trying "
                + "to pull a fast one. <br> <br> "
                + "<tr>"
		+ "<th valign=\"top\"><b>Mileage:</b> </th>"
		+ "<td>Color:</td>"
		+ "<td>Body:</td>"
		+ "<td>Transmission:</td>"
		+ "</tr>"
		+ "<tr>"
		//+ "<td valign=\"top\"><b>77795</b></td>"
		//+ "<td valign=\"top\">Exterior: CHAMPAGNE";                                                                                                                                                                                             <br>Interior: CREAM                                                                                                                                                                                                   </td>
		+ "<td valign=\"top\">SEDAN 4-DR</td>"
		+ "<td valign=\"top\">5-Speed Automatic </td>"
		+ "</tr>";
                
        CLCarCrawler c = new CLCarCrawler();
        int mileage = c.getMileageFromListing(stuff);
        System.out.println("Mileage: " + mileage);
    }

    //@Test
    public void getAllCarMakes() {
        List<String> makes = CarMake.getAllMakes();
        for(String m : makes)
            System.out.println(m);
    }

    //@Test
    public void getModelsByMake() {
        List<String> models = CarMake.getModelsByMake("Audi");
        for(String m : models)
            System.out.println(m);
    }

    @Test
    public void crawlPosting() throws Exception {
        String url = "http://sfbay.craigslist.org/eby/cto/2280866512.html";
        CLCarCrawler c = new CLCarCrawler();
        c.initParams();
        CLCar car = new CLCar();
        c.crawlPosting(car, url);
        System.out.println("Make: " + (car.getMake() != null ? car.getMake() : ""));
        System.out.println("Model: " + (car.getModel() != null ? car.getModel() : ""));
        System.out.println("Mileage: " + (car.getMileage() != null ? car.getMileage() : ""));
        System.out.println("Year: " + (car.getYear() != null ? car.getYear() : ""));
        System.out.println("Price: " + (car.getPrice() != null ? car.getPrice() : ""));

    }

}