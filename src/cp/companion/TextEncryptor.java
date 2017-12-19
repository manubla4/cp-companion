/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.companion;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author manub
 */
public class TextEncryptor {
     
    public static final String SECRET_KEY = "LsG2a423oPjSgn55aBs82dHoj9";
    
    
    public static String encrypt(String texto, String secretKey) {
        String base64EncryptedString = "";
                
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
 
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, key);
 
            byte[] plainTextBytes = texto.getBytes("utf-8");
            byte[] buf = cipher.doFinal(plainTextBytes);
            byte[] base64Bytes = Base64.getEncoder().encode(buf);
            base64EncryptedString = new String(base64Bytes);
 
        }catch (Exception ex) {            
        }
        return base64EncryptedString;
    }
       
 
    
    public static String decrypt(String textoEncriptado, String secretKey) throws Exception {
        String base64EncryptedString = "";
        
        try {
            byte[] message = Base64.getDecoder().decode(textoEncriptado.getBytes("utf-8"));
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
 
            Cipher decipher = Cipher.getInstance("DESede");
            decipher.init(Cipher.DECRYPT_MODE, key);
 
            byte[] plainText = decipher.doFinal(message);
 
            base64EncryptedString = new String(plainText, "UTF-8");
 
        } catch (Exception ex) {
        }
        return base64EncryptedString;
    }
  
    
}
