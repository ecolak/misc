/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author ecolak
 */
public class HttpUtil {
    private HttpUtil(){}

    public static String httpGet(String url) throws IOException {
        StringBuilder result = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(instream));
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } finally {
                IOUtil.closeQuietly(reader);
            }
        }
        return result.toString();
    }
}
