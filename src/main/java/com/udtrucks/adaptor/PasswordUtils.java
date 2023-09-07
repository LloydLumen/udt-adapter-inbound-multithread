package com.udtrucks.adaptor;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import java.security.Security;

import static com.udtrucks.adaptor.constants.Constants.properties;

public class PasswordUtils {
    public static byte[] decodePassword(String encryptedPassword) {
//        String ENCRYPTION_KEY = (String) properties.get("mdm.cdi.customer.url");
//        String ENCRYPTION_ALGORITHM = (String) properties.get("security.encrytion.algorithm");
//        String ENCRYPTION_MODE = (String) properties.get("security.encrytion.mode");
        String ENCRYPTION_KEY = "UDTrucks@1234567";
        String ENCRYPTION_ALGORITHM = "AES/ECB/PKCS5Padding";
        String ENCRYPTION_MODE = "BC";
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ENCRYPTION_ALGORITHM);
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM, ENCRYPTION_MODE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedBytes = Base64.decodeBase64(encryptedPassword);
            return cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}