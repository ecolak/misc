/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author ecolak
 */
public class IOUtil {
    private IOUtil(){}

    public static void closeQuietly(Reader reader) {
        if(reader != null) {
            try {
                reader.close();
            } catch(IOException e) {
                System.err.println("Cannot close reader");
            }
        }
    }
}
