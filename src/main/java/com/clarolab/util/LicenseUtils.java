package com.clarolab.util;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class LicenseUtils {

    private static final String KEY = "rpeitygehq";
    private static final int KEY_LENGTH = 10;

    public static String cipher(String str) {
        String encrypted = "";
        for (int i = 0; i < str.length(); i++) {
            //stores ascii value of character in the string at index 'i'
            int c = str.charAt(i);
            //encryption logic for uppercase letters
            if (Character.isUpperCase(c)) {
                c = c + (KEY_LENGTH % 26);
                //if c value exceeds the ascii value of 'Z' reduce it by subtracting 26(no.of alphabets) to keep in boundaries of ascii values of 'A' and 'Z'
                if (c > 'Z')
                    c = c - 26;
            }
            //encryption logic for lowercase letters
            else if (Character.isLowerCase(c)) {
                c = c + (KEY_LENGTH % 26);
                //if c value exceeds the ascii value of 'z' reduce it by subtracting 26(no.of alphabets) to keep in boundaries of ascii values of 'a' and 'z'
                if (c > 'z')
                    c = c - 26;
            }
            //concatinate the encrypted characters/strings
            encrypted = encrypted + (char) c;
        }
        return encrypted;
    }

    public static String decipher(String str) {
        String decrypted = "";
        for (int i = 0; i < str.length(); i++) {
            //stores ascii value of character in the string at index 'i'
            int c = str.charAt(i);
            //decryption logic for uppercase letters
            if (Character.isUpperCase(c)) {
                c = c - (KEY_LENGTH % 26);
                //if c value deceed the ascii value of 'A' increase it by adding 26(no.of alphabets) to keep in boundaries of ascii values of 'A' and 'Z'
                if (c < 'A')
                    c = c + 26;
            }
            //decryption logic for uppercase letters
            else if (Character.isLowerCase(c)) {
                c = c - (KEY_LENGTH % 26);
                //if c value deceed the ascii value of 'A' increase it by adding 26(no.of alphabets) to keep in boundaries of ascii values of 'A' and 'Z'
                if (c < 'a')
                    c = c + 26;
            }
            //concatinate the decrypted characters/strings
            decrypted = decrypted + (char) c;
        }
        return decrypted;
    }


    public static String generateCode(String domain, String expirationPeriod) throws Exception {

        // Encoding the expiration time in base64
        String encodedExpirationPeriod = Base64.getEncoder().encodeToString(expirationPeriod.getBytes());
        String cipheredTime = cipher(encodedExpirationPeriod);
        String firstRandom = StringUtils.randomString(32);
        String secondRandom = StringUtils.randomString(32);
        String encodedDomain = Base64.getEncoder().encodeToString(domain.getBytes());
        String cipheredDomain = cipher(encodedDomain);
        String finalCode = firstRandom + cipheredTime + "$" + secondRandom + cipheredDomain;


        return finalCode;

    }


    public static List<String> licenceDecode(String code) {
        List<String> answer = new ArrayList<>();
        String kept = code.substring(32);
        String cipheredPeriod = kept.substring(0, kept.indexOf("$"));
        String decipherPeriod = decipher(cipheredPeriod);
        byte[] bytesPeriod = Base64.getDecoder().decode(decipherPeriod);
        String decodedExpirationPeriod = new String(bytesPeriod);

        String secondPart = kept.substring(kept.indexOf("$") + 1, kept.length());
        String cipheredDomain = secondPart.substring(32);
        String decipherDomain = decipher(cipheredDomain);
        byte[] bytesDomain = Base64.getDecoder().decode(decipherDomain);
        String decodedDomain = new String(bytesDomain);

        answer.add(decodedDomain);
        answer.add(decodedExpirationPeriod);

        return answer;


    }

}
