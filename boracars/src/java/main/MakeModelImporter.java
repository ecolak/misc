/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import dao.MakeModel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import util.IOUtil;

/**
 *
 * @author ecolak
 */
public class MakeModelImporter {
    public static final String CARS_FILE_PATH = "C:/Users/ecolak/Desktop/car_make_models.txt";
    public static final String MAKE_REGEX = "<strong>(.+)</strong>";
    public static final Pattern MAKE_PATTERN = Pattern.compile(MAKE_REGEX);

    public static void main(String... args) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(CARS_FILE_PATH));
            String make = null;
            String line = null;
            while((line = reader.readLine()) != null) {
                if(StringUtils.isNotBlank(line)) {
                    Matcher matcher = MAKE_PATTERN.matcher(line);
                    if(matcher.find()) {
                        make = matcher.group(1).trim().toLowerCase();
                        continue;
                    }
                    String model = line.trim().toLowerCase();
                    MakeModel mm = MakeModel.getByMakeAndModel(make, model);
                    if(mm == null) {
                        mm = new MakeModel();
                    }
                    mm.setMake(make);
                    mm.setModel(model);
                    mm.save();
                }
            }
        } catch(IOException ie) {
            ie.printStackTrace();
        } finally {
            IOUtil.closeQuietly(reader);
        }
    }
}
