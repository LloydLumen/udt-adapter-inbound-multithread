package com.udtrucks.adaptor;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class PasswordEncoder {

    public static void main(String[] args) {
        String password = "m1kcGSKBfvP&";
        String encodedPassword = encode(password);
//        String encodedPassword = "S/YI1kClMbDWCveiLzb87w==";
        System.out.println("Encoded password: " + encodedPassword);

        String decodedPassword = new String(PasswordUtils.decodePassword(encodedPassword));
        System.out.println("Decoded password: " + decodedPassword);
    }

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_KEY = "UDTrucks@1234567"; // Replace with your encryption key
//    private static final String ENCRYPT_MODE = "BC";

    public static String encode(String password) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
            return Base64.encodeBase64String(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}