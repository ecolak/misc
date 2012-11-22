/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import dao.User;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author ecolak
 */
public class AuthUtil {

    private static KeyGenerator keygen = null;
    private static SecretKey desKey = null;
    private static Cipher desCipher = null;
    private static SecureRandom prng = null;
    private static BASE64Encoder base64Encoder = null;
    private static BASE64Decoder base64Decoder = null;
    public static final String charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int RANDOM_PASSWORD_LENGTH = 8;
    public static String MD5_ENCRYPTION_EXCEPTION_MSG = "Failed encrypting password with MD5";

    static {
        try {
            keygen = KeyGenerator.getInstance("DES");
            prng = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ne) {
            System.err.println(ne);
        }
        if (keygen != null) {
            desKey = keygen.generateKey();
        }

        try {
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException ne) {
            System.err.println(ne);
        } catch (NoSuchPaddingException pe) {
            System.err.println(pe);
        }
        base64Encoder = new BASE64Encoder();
        base64Decoder = new BASE64Decoder();
    }

    public static String encryptWithDES(String plainText) throws Exception {
        String cipherText = null;
        if (desCipher != null && desKey != null) {
            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, desKey);

            // Our cleartext
            byte[] clearTextBytes = plainText.getBytes();

            // Encrypt the cleartext
            byte[] cipherTextBytes = desCipher.doFinal(clearTextBytes);

            //cipherText = base64Encoder.encode(cipherTextBytes);
            cipherText =  new String(cipherTextBytes);
        }
        return cipherText;
    }

    public static String decryptWithDES(String cipherText) throws Exception {
        String plainText = null;
        if (desCipher != null && desKey != null) {
            // Initialize the cipher for encryption
            desCipher.init(Cipher.DECRYPT_MODE, desKey);

            // Our cleartext
            //byte[] cipherTextBytes = base64Decoder.decodeBuffer(cipherText);
            byte[] cipherTextBytes = cipherText.getBytes();

            // Encrypt the cleartext
            byte[] plainTextBytes = desCipher.doFinal(cipherTextBytes);
            plainText = new String(plainTextBytes);
        }
        return plainText;
    }

    public static String encryptWithMD5(String plainText) throws Exception {
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(plainText.getBytes());
        byte[] messageDigest = algorithm.digest();

        return base64Encoder.encode(messageDigest);
        //return AuthUtil.bytesToHex(messageDigest);
    }

    public static String encryptWithMD5andHex(String plainText) throws Exception {
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(plainText.getBytes());
        byte[] messageDigest = algorithm.digest();

        //return base64Encoder.encode(messageDigest);
        return AuthUtil.bytesToHex(messageDigest);
    }

    public static String generateConfKeyForUser() throws Exception {
        BASE64Encoder encoder = new BASE64Encoder();
        String confKey = null;
        if (prng != null) {
            String randomNum = new Integer(prng.nextInt()).toString();
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] confKeyBytes = sha.digest(randomNum.getBytes());
            confKey = encoder.encode(confKeyBytes);
        }

        return confKey;
    }

    public static String generateRandomPassword() throws Exception {
        return generateRandomPassword(RANDOM_PASSWORD_LENGTH);
    }

    public static String generateRandomPassword(int length) throws Exception {
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int pos = rand.nextInt(charset.length());
            sb.append(charset.charAt(pos));
        }
        return sb.toString();
    }

    public static String bytesToHex(byte[] b) {
        char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder buf = new StringBuilder();
        for (int j = 0; j < b.length; j++) {
            buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
            buf.append(hexDigit[b[j] & 0x0f]);
        }
        return buf.toString();
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static User getLoggedInUser(HttpServletRequest request) {
        return (User)request.getSession().getAttribute("user");
    }

    public static Map<String, String> getParameterMap(String paramString) {
        Map<String, String> paramMap = new HashMap<String, String>();

        if (paramString != null) {
            String[] params = paramString.split("&");
            for (String param : params) {
                String[] p = param.split("=");
                paramMap.put(p[0], p[1]);
            }
        }

        return paramMap;
    }

    public static String encodeString(String plainText) {
        return base64Encoder.encode(plainText.getBytes());
    }

    public static String decodeString(String encryptedText) {
        String decoded = null;

        try {
            decoded = new String(base64Decoder.decodeBuffer(encryptedText));
        } catch (IOException e) {}

        return decoded;
    }

}
