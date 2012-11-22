/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import dao.CarMake;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import util.IOUtil;

/**
 *
 * @author ecolak
 */
public class KbbCarImporter {

    public static final String CARS_FILE_PATH = "C:/Users/ecolak/Desktop/kbb_make_model_year.csv";

    public static void main(String... args) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(CARS_FILE_PATH));
            String line;
            while((line = reader.readLine()) != null) {
                String[] tokens = StringUtils.split(line, ',');
                if(tokens != null && tokens.length == 3) {
                    String model = tokens[0];
                    String make = tokens[1];
                    String years = tokens[2];
                    String yTokens[] = StringUtils.split(years, '|');
                    if(yTokens != null && yTokens.length > 0) {
                        for(String yt : yTokens) {
                            int year = getYear(yt);
                            CarMake carMake = CarMake.getByMakeModelYear(make, model, year);
                            if(carMake == null) {
                                carMake = new CarMake();
                            }
                            carMake.setMake(make);
                            carMake.setModel(model);
                            if(year != -1) {
                                carMake.setYear(year);
                            }
                            carMake.save();
                        }
                    }
                 }
            }
        } catch(IOException ie) {
            ie.printStackTrace();
        } finally {
            IOUtil.closeQuietly(reader);
        }
    }

    public static int getYear(String kbbYear) {
        int result = -1;
        if(StringUtils.isNotBlank(kbbYear)) {
            String fourDigit = null;
            int twoDigitYear = Integer.parseInt(kbbYear);
            if(twoDigitYear >= 0) {
                if(twoDigitYear < 20) {
                    fourDigit = "20" + kbbYear;
                } else {
                    fourDigit = "19" + kbbYear;
                }
            }
            result = Integer.parseInt(fourDigit);
        }
        return result;
    }
}
