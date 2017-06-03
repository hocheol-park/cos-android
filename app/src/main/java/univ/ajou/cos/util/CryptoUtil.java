package univ.ajou.cos.util;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {
    public static String makeSha256Key(String str) { // 암호화 지정

        byte[] defaultBytes = str.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            // *** Testausgabe

            str = hexString + "";
        } catch (NoSuchAlgorithmException nsae) {

        }
        return str;
    }


    public static String makeSha512Key(String str) { // 암호화 지정

        byte[] defaultBytes = str.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            // *** Testausgabe

            str = hexString + "";
        } catch (NoSuchAlgorithmException nsae) {

        }
        return str;
    }


    public static String encrypt(String clearText) {
        byte[] encryptedText = null;
        try {
            byte[] keyData = "mobiletvdatacryp".getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, ks);
            encryptedText = c.doFinal(clearText.getBytes("UTF-8"));
            return Base64.encodeToString(encryptedText, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decrypts the text
     * @param encryptedText The text you want to encrypt
     * @return Decrypted data if successful, or null if unsucessful
     */
    public static String decrypt (String encryptedText) {
        byte[] clearText = null;
        try {
            byte[] keyData = "mobiletvdatacryp".getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, ks);
            clearText = c.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
            return new String(clearText, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }
}
