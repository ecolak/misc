/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import dao.CLCar;
import dao.MakeModel;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import util.HttpUtil;
import util.TaskUtil;

/**
 *
 * @author ecolak
 */
public class CLCarCrawler extends AbstractTask {

    public static final String SF_BAY = "sf bay";
    public static final String CA = "CA";
    public static final int DEFAULT_DAYS_BACK = 1;
    public static final String CARS_URL = "http://sfbay.craigslist.org/cta/";

    public static final DateFormat CL_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd");
    public static final DateFormat POSTING_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd, h:mma z");
    public static final String SEARCH_PAGE_DATE_REGEX = "<h4 class=\"ban\">(.+?)</h4>";
    public static final Pattern SEARCH_PAGE_DATE_PATTERN = Pattern.compile(SEARCH_PAGE_DATE_REGEX);

    public static final String LISTING_REGEX =
        "<p class=\"row\">.*?<a href=\"(.*?(\\d{9,11})\\.html)\">(.*?)</a>"
        + ".*?(owner|dealer).*?</p>";
      
    public static final Pattern LISTING_PATTERN = Pattern.compile(LISTING_REGEX);

    public static final Pattern LISTING_TITLE_PATTERN = Pattern.compile("<h2>(.*?)</h2><hr>");

    public static final Pattern PRICE_PATTERN = Pattern.compile("\\$(\\d{1,7})");

    public static final String LOCATION_REGEX = "<font size=\"-1\">\\s*\\((.+?)\\)\\s*</font>";
    public static final Pattern LOCATION_PATTERN = Pattern.compile(LOCATION_REGEX);

    public static final String NEXT_100_REGEX = "<a href=\"([^<]*?)\">next 100 postings</a>";
    public static final Pattern NEXT_100_PATTERN = Pattern.compile(NEXT_100_REGEX);

    public static final String POSTING_PAGE_DATE_REGEX = "Date: ([A-Z0-9 ,:-]+)";
    public static final Pattern POSTING_PAGE_DATE_PATTERN = Pattern.compile(POSTING_PAGE_DATE_REGEX);

    public static final String CONTACT_EMAIL_REGEX = "Reply to: <a href=\"[^<]*?\">(.+@craigslist.org)</a>";
    public static final Pattern CONTACT_EMAIL_PATTERN = Pattern.compile(CONTACT_EMAIL_REGEX);

    public static final String PHONE_REGEX1 = "\\(\\d{3}\\)\\s?\\d{3}-\\d{4}";
    public static final String PHONE_REGEX2 = "\\d{3}[- .]{1}\\d{3}[- .]{1}\\d{4}";
    public static final Pattern PHONE_PATTERN1 = Pattern.compile(PHONE_REGEX1);
    public static final Pattern PHONE_PATTERN2 = Pattern.compile(PHONE_REGEX2);

    public static final String MILEAGE_REGEX1 = "(mileage|miles).{0,5}?((\\d{1,3}k)|(\\d{1,3},?[0-9x]{3}))";
    public static final String MILEAGE_REGEX2 = "((\\d{1,3}k)|(\\d{1,3},?[0-9x]{3})).{0,15}?miles";
    public static final String MILEAGE_REGEX3 = "((<th[^<]*?>.*?(mileage|miles).*?</th>)|"
            + "(<td[^<]*?>.*?(mileage|miles).*?</td>)).*"
            + "<td[^<]*?>.*?((\\d{1,3}k)|(\\d{1,3},?[0-9x]{3})).*?</td>";
    public static final String MILEAGE_REGEX4 = "[< >](\\d{1,3}k)[< >]";
    
    public static final Pattern MILEAGE_PATTERN1 = Pattern.compile(MILEAGE_REGEX1, Pattern.CASE_INSENSITIVE);
    public static final Pattern MILEAGE_PATTERN2 = Pattern.compile(MILEAGE_REGEX2, Pattern.CASE_INSENSITIVE);
    public static final Pattern MILEAGE_PATTERN3 = Pattern.compile(MILEAGE_REGEX3, Pattern.CASE_INSENSITIVE);
    public static final Pattern MILEAGE_PATTERN4 = Pattern.compile(MILEAGE_REGEX4, Pattern.CASE_INSENSITIVE);

    public static final String USERBODY_REGEX = "<div id=\"userbody\">(.*)</div>";
    public static final Pattern USERBODY_PATTERN = Pattern.compile(USERBODY_REGEX);

    public static final Pattern ALL_MAKES_PATTERN = Pattern.compile(MakeModel.getAllMakesRegex(), Pattern.CASE_INSENSITIVE);

    private Date fromDate;
    private Date toDate;
    private int daysBack;
    private String nextUrl;
    private Calendar now = Calendar.getInstance();
    private List<String> allMakes;

    private static final Map<String, String[]> MAKE_SYNONYMS;
    static {
        MAKE_SYNONYMS = new HashMap<String, String[]>();
        MAKE_SYNONYMS.put("mercedes-benz", new String[]{"mercedes"});
        MAKE_SYNONYMS.put("chevrolet", new String[]{"chevy"});
        MAKE_SYNONYMS.put("volkswagen", new String[]{"vw"});
    }

    public CLCarCrawler() {}

    public CLCarCrawler(int delay, int period, String params) {
        super(delay, period, params);
        now.setTime(new Date());
    }

    @Override
    public final void initParams() {
        try {
            fromDate = TaskUtil.TASK_DATE_FORMAT.parse(getParamValue("from-date"));
        } catch(Exception e) {
            fromDate = DateUtils.truncate(new Date(), Calendar.DATE);
        }
        try {
            daysBack = Integer.parseInt(getParamValue("days-back"));
        } catch(Exception e) {
            daysBack = DEFAULT_DAYS_BACK;
        }
        toDate = DateUtils.addDays(fromDate, -daysBack+1);
        allMakes = MakeModel.getAllMakes();
    }

    @Override
    public void run() {
        long taskStatId = TaskUtil.logStart(taskId, new Date());
        String url = CARS_URL;
        Date maxListingDate = CLCar.getMaxListingDate();
        if(maxListingDate == null) {
            maxListingDate = toDate;
        }
        Date controlDate = maxListingDate;
        if(toDate.after(maxListingDate)) {
            controlDate = toDate;
        }
        try {
            while(true) {
                List<CLCar> cars = doOnePage(url);
                CLCar latestCar = Collections.max(cars, new Comparator<CLCar>() {
                    public int compare(CLCar car1, CLCar car2) {
                        int result = 0;
                        if(car1 != null && car2 != null &&
                           car1.getListingDate() != null && car2.getListingDate() != null) {
                           result = car1.getListingDate().compareTo(car2.getListingDate());
                        }
                        return result;
                    }
                });

                Date maxDateOnPage = latestCar.getListingDate();
                if(maxDateOnPage.after(controlDate)) {
                    for(CLCar car : cars) {
                        car.save();
                    }
                    url = nextUrl;
                } else {
                    System.out.println("No new listings. Quitting...");
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        TaskUtil.logEnd(taskStatId, new Date(), null);
    }

    public List<CLCar> doOnePage(String url) throws IOException {
        List<CLCar> cars = new ArrayList<CLCar>();
        if(url == null)
            return cars;
        
        String response = HttpUtil.httpGet(url);

        Date pageDate = null;
        Matcher dateMatcher = SEARCH_PAGE_DATE_PATTERN.matcher(response);
        if(dateMatcher.find()) {
            String dateStr = dateMatcher.group(1);
            try {
                pageDate = CL_DATE_FORMAT.parse(dateStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(pageDate);
                cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
                pageDate = cal.getTime();
                System.out.println("Date: " + dateStr);
            } catch(ParseException pe) {
                System.err.println("Date cannot be parsed for this page");
            }
        }

        Matcher matcher = LISTING_PATTERN.matcher(response);
        int i = 0;
        while(matcher.find()) {
            i++;
            String link = matcher.group(1);
            String listingTitle = matcher.group(3);
            String soldBy = matcher.group(4);
            String row = matcher.group();

            Long craigslistId = null;
            try {
                craigslistId = Long.parseLong(matcher.group(2));
            } catch(Exception e) { }
            CLCar car = CLCar.getByCLId(craigslistId);
            if(car == null) {
                car = new CLCar();
            }

            car.setListingDate(pageDate);
            car.setListingUrl(link);
            car.setCraigslistId(craigslistId);          
            car.setListingTitle(listingTitle);
            MakeModel mm = getMakeModelFromListing(listingTitle);
            if(mm != null) {
                car.setMake(mm.getMake());
                car.setModel(mm.getModel());
            }
            int year = getYearFromListing(listingTitle);
            if(year != -1) {
                car.setYear(year);
            }
            int price = getPriceFromListing(row);
            if(price != -1) {
                car.setPrice(price);
            }
            String location = getLocationFromListing(row);
            if(location != null) {
                car.setLocation(location);
            }
            
            car.setLocation(location);
            car.setClRegion(SF_BAY);
            car.setState(CA);
            car.setSoldBy(soldBy);
            car.setDateCreated(new Date());

            this.crawlPosting(car, car.getListingUrl());
            cars.add(car);
            // Don't save the car to the db yet

            /*System.out.println("Link: " + car.getListingUrl());
            System.out.println("CL id: " + car.getCraigslistId());
            System.out.println("Listing: " + car.getListingTitle());
            System.out.println("Make: " + car.getMake());
            System.out.println("Model: " + car.getModel());
            System.out.println("Year: " + car.getYear());
            System.out.println("Price: " + car.getPrice());
            System.out.println("Mileage: " + car.getMileage());
            System.out.println("Location: " + car.getLocation());
            System.out.println("Sold by: " + car.getSoldBy());
            System.out.println("-----------------------");*/
        }
        System.out.println(i + " matches found");
        Matcher nextMatcher = NEXT_100_PATTERN.matcher(response);
        if(nextMatcher.find()) {
            nextUrl = CARS_URL + nextMatcher.group(1);
        }
        return cars;
    }

    public void crawlPosting(CLCar car, String url) throws IOException {
        String response = null;
        try {
            response = HttpUtil.httpGet(url);
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }

        if(response == null)
            return;

        // remove all HTML tags except <a>
        //response = response.replaceAll("\\<[^a].*?\\>", "");

        Matcher dateMatcher = POSTING_PAGE_DATE_PATTERN.matcher(response);
        if(dateMatcher.find()) {
            String d = dateMatcher.group(1);
            Date timestamp = null;
            try {
                timestamp = POSTING_DATE_FORMAT.parse(d);
            } catch(Exception e) {
                // ignore
            }
            if(timestamp != null) {
                car.setListingDate(timestamp);
            }
        }

        Matcher emailMatcher = CONTACT_EMAIL_PATTERN.matcher(response);
        if(emailMatcher.find()) {
            car.setContactEmail(emailMatcher.group(1));
        }

        String listingTitle = "";
        Matcher titleMatcher = LISTING_TITLE_PATTERN.matcher(response);
        if(titleMatcher.find()) {
            listingTitle = titleMatcher.group(1);
        }

        // get user body
        Matcher userBodyMatcher = USERBODY_PATTERN.matcher(response);
        String userBody = "";
        if(userBodyMatcher.find()) {
            userBody = userBodyMatcher.group(1);
        }

        String stripHtmlPattern = "(\\</?(a|b|i|font|small|span|strong|div|img|ul|"
                + "ol|li|h1|h2|h3|h4|h5|h6|h7)\\s.*?\\>|&nbsp;)";
        userBody = userBody.replaceAll(stripHtmlPattern, "");
        userBody = listingTitle + userBody;

        if(car.getMake() == null || car.getModel() == null) {
            MakeModel mm = getMakeModelFromListing(userBody);
            if(mm != null) {
                car.setMake(mm.getMake());
                car.setModel(mm.getModel());
            }
        }

        if(car.getYear() == null) {
            int year = getYearFromListing(userBody);
            if(year != -1) {
                car.setYear(year);
            }
        }

        if(car.getPrice() == null) {
            int price = getPriceFromListing(userBody);
            if(price != -1) {
                car.setPrice(price);
            }
        }

        if(car.getLocation() == null) {
            String location = getLocationFromListing(userBody);
            if(location != null) {
                car.setLocation(location);
            }
        }

        if(car.getMileage() == null) {
            int mileage = getMileageFromListing(userBody);
            if(mileage != -1) {
                car.setMileage(mileage);
            }
        }

        car.setContactPhone(getPhoneFromListing(userBody));
    }

    public MakeModel getMakeModelFromListing(String listing) {
        MakeModel mm = new MakeModel();
        Map<String, String> listingTokens = tokenizeListing(listing);
        for(String make : allMakes) {
            String foundMake = findToken(listingTokens, make);
            if(foundMake == null) {
                if(MAKE_SYNONYMS.containsKey(make)) {
                    String[] synonyms = MAKE_SYNONYMS.get(make);
                    for(String syn : synonyms) {
                        foundMake = findToken(listingTokens, syn);
                        if(foundMake != null) {
                            foundMake = make;
                            break;
                        }
                    }
                }
            }

            if(foundMake != null) {
                mm.setMake(foundMake);
                break;
            }
        }

        if(mm.getMake() == null) {
            Matcher makeMatcher = ALL_MAKES_PATTERN.matcher(listing);
            if(makeMatcher.find()) {
                mm.setMake(makeMatcher.group(1));
            }
        }

        if(mm.getMake() != null) {
            List<String> models = MakeModel.getModelsByMake(mm.getMake());
            for(String model : models) {
                String foundModel = findToken(listingTokens, model);
                if(foundModel != null) {
                    mm.setModel(model);
                    break;
                }
            }
        }

        // if no make has been found, search for a model and then
        // try to infer make
        if(mm.getMake() == null && mm.getModel() == null) {
            List<String> models = MakeModel.getAllModels();
            for(String model : models) {
                // model with less than 3 chars can be very misleading
                // e.g. Mercedes A
                if(model.matches("[a-zA-Z]{3,10}")) {
                    if(listingTokens.containsKey(model)) {
                        String make = MakeModel.getMakeByModel(model);
                        if(make != null) {
                            mm.setMake(make);
                            mm.setModel(model);
                            break;
                        }
                    }
                }
            }
        }
        return mm;
    }

    /*
     // Construct Map<word, next word>
     public Map<String, String> tokenizeListing(String listing) {
        Map<String, String> map = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(listing);
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
        return map;
    }*/

    public Map<String, String> tokenizeListing(String listing) {
        Map<String, String> result = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(listing);
        while (st.hasMoreTokens()) {
            String next = st.nextToken().toLowerCase();
            result.put(next,next);
        }
        return result;
    }

    /*
     // Uses Levenshtein distance for similar words
     public String findToken(Map<String, String> listingTokens, String lookFor) {
        lookFor = lookFor.toLowerCase();
        for(String k : listingTokens.keySet()) {
            String v = listingTokens.get(k);
            k = k.toLowerCase();
            v = v.toLowerCase();
            if(k.equals(lookFor) || v.equals(lookFor)) {
                return lookFor;
            }

            int lDist = 2;
            try {
                lDist = StringUtils.getLevenshteinDistance(k, lookFor);
                if(lDist < 2) {
                    return lookFor;
                }
            } catch(Exception e) {}

            try {
                lDist = StringUtils.getLevenshteinDistance(v, lookFor);
                if(lDist < 2) {
                    return lookFor;
                }
            } catch(Exception e) {}
            
            String kv = k + v;
            try {
                lDist = StringUtils.getLevenshteinDistance(kv, lookFor);
                if(lDist < 2) {
                    return lookFor;
                }
            } catch(Exception e) {}
        }
        return null; 
    }*/

    public String findToken(Map<String, String> listingTokens, String lookFor) {
       return listingTokens.get(lookFor);
    }

    /*public String findToken(String original, String lookFor) {
        String result = null;
        StringTokenizer st = new StringTokenizer(original);
        while (st.hasMoreTokens()) {
            if(lookFor.equalsIgnoreCase(st.nextToken())) {
                result = lookFor;
                break;
            }
        }
        return result;
    }*/

    public int getYearFromListing(String listing) {
        int result = -1;
        StringTokenizer st = new StringTokenizer(listing);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if(token.length() == 2 || token.length() == 4) {
                try {
                    int iToken = Integer.parseInt(token);
                    if(token.length() == 2) {
                        if(iToken >= 0 && iToken <= 20) {
                            result = 2000 + iToken;
                        } else if(iToken >= 50 && iToken <= 99) {
                            result = 1900 + iToken;
                        }
                    } else if(token.length() == 4) {
                        if((iToken >= 2000 && iToken <= 2020) ||
                           (iToken >= 1950 && iToken <= 1999) ) {
                            result = iToken;
                        } 
                    }
                } catch(Exception e) {
                    // ignore
                }
                if(result != -1) {
                    break;
                }
            }
        }
        return result;
    }

    public int getPriceFromListing(String listing) {
        int result = -1;
        Matcher m = PRICE_PATTERN.matcher(listing);
        if(m.find()) {
            try {
                result = Integer.parseInt(m.group(1));
            } catch(Exception e) {
                // ignore
            }
        }
        return result;
    }

    public String getLocationFromListing(String listing) {
        String result = null;
        Matcher m = LOCATION_PATTERN.matcher(listing);
        if(m.find()) {
            result = m.group(1);
        }
        return result;
    }

    public int getMileageFromListing(String listing) {
        int result = -1;
        Matcher mileageMatcher = MILEAGE_PATTERN1.matcher(listing);
        String mileage = null;
        if(mileageMatcher.find()) {
            mileage = mileageMatcher.group(2);
        }
        
        if(mileage == null) {
            mileageMatcher = MILEAGE_PATTERN2.matcher(listing);
            if(mileageMatcher.find()) {
                mileage = mileageMatcher.group(1);
            }
        }

        if(mileage == null) {
            mileageMatcher = MILEAGE_PATTERN3.matcher(listing);
            if(mileageMatcher.find()) {
                mileage = mileageMatcher.group(6);
            }
        }

        if(mileage == null) {
            mileageMatcher = MILEAGE_PATTERN4.matcher(listing);
            if(mileageMatcher.find()) {
                mileage = mileageMatcher.group(1);
            }
        }

        if(mileage != null) {
            try {
                result = Integer.parseInt(mileage);
            } catch(Exception e) {
                // ignore, try next
            }
            if(result == -1) {
                int indexOfK = mileage.indexOf('k');
                if(indexOfK == -1) {
                    indexOfK = mileage.indexOf('K');
                }
                if(indexOfK > 0) {
                    try {
                        result = Integer.parseInt(mileage.substring(0, indexOfK)) * 1000;
                    } catch(Exception e) {
                        // ignore, try nxt
                    }
                }
            }
            if(result == -1) {
                if(mileage.contains(",")) {
                    mileage = mileage.replaceAll(",", "");
                }
                try {
                    result = Integer.parseInt(mileage);
                } catch(Exception e) {
                    // ignore, try next
                }
            }

            if(result == -1) {
                int indexOfX = mileage.indexOf('x');
                if(indexOfX == -1) {
                    indexOfX = mileage.indexOf('X');
                }
                if(indexOfX > 0) {
                    if(mileage.matches("[0-9]{1,3}[xX]{3}")) {
                        mileage = mileage.replaceAll("x|X", "0");
                    }
                    try {
                        result = Integer.parseInt(mileage.substring(0,indexOfX)) * 1000 +
                                Integer.parseInt(mileage.substring(indexOfX + 1));
                    } catch(Exception e) {
                        // ignore
                    }
                }
            }
        }
        return result;
    }

    public String getPhoneFromListing(String listing) {
        String phone = null;
        Matcher m = PHONE_PATTERN1.matcher(listing);
        if(m.find()) {
            phone = m.group();
        }
        if(phone == null) {
            m = PHONE_PATTERN2.matcher(listing);
            if(m.find()) {
                phone = m.group();
            }
        }
        return phone;
    }
}
