/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.util;


import com.clarolab.view.KeyValuePair;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.java.Log;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.text.WordUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log
public final class StringUtils {

    private static final int iterations = 20 * 1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 512;
    private static final int maxStringSize = 1000;

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static SecureRandom rnd = new SecureRandom();

    private static List<UUID> uuids = Lists.newArrayList();

    private static String[] lowercaseWords = {"a", "an", "and", "as", "for", "to", "by", "then", "on", "in", "the", "when", "not","with","without","from", "i18n", "test"};
    public static final String[] separators = new String[] {",", " ", ".", "-", ";"};

    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
    }

    public static boolean check(String password, String stored) throws Exception {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException("The stored password have the form 'salt$hash'");
        }

        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
        return MessageDigest.isEqual(hashOfInput.getBytes(),saltAndPass[1].getBytes());
    }

    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Empty passwords are not supported.");
        }
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen));
        return Base64.encodeBase64String(key.getEncoded());
    }

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public static String randomToken() {
        return StringUtils.randomString(30);
    }

    public static boolean isEmpty(String s) {
        boolean answer = Strings.isNullOrEmpty(s);
        if (!answer) {
            answer = Strings.isNullOrEmpty(s.trim());
        }

        return answer;
    }

    public static boolean isValidLength(String s, int l) {
        return s != null && s.length() >= l;
    }

    public static UUID createUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (uuids.contains(uuid));
        uuids.add(uuid);
        log.info("New UUID Created: " + uuid);
        return uuid;
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public static boolean isEmpty(Collection col) {
        return col.isEmpty();
    }

    public static boolean isEmpty(String[] col) {
        return col == null || col.length == 0;
    }

    public static String getEmpty() {
        return new String();
    }

/*
    public static String parseDataError(String error, String value){
        return String.format("%s : [%s]", error, value);
    }
*/

    public static String parseDataError(String error, Object value) {
        return String.format("%s : [%s]", error, value);
    }

    public static String getLineSeparator() {
        StringBuffer str = new StringBuffer();
        return str.append("\n").append("********************************************").append("\n").toString();
    }

    public static StringBuilder addTab(StringBuilder string, int depthTab){
        StringBuilder newString = new StringBuilder(string);
        for(int i = 0; i<depthTab; i++){
            newString.append("\t");
        }
        return newString;
    }

    public static String methodToWords(String methodName) {
        if (methodName == null) {
            return null;
        }
        String methodNoPackage = classTail(methodName.trim());
        String methodNoParams = removeParameters(methodNoPackage);
        return fromCamelCaseToWords(methodNoParams);
    }

    private static String fromCamelCaseToWords(String camelString) {
        StringBuffer words = new StringBuffer();
        String camel = sanitize(camelString);
        String[] splittedCamel = camel.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        String[] splittedSpace;

        for (String w : splittedCamel) {
            splittedSpace = w.split(" ");
            for (String word : splittedSpace) {
                words.append(capitalizeIfNotLowercase(word));
                words.append(" ");
            }
        }
        return words.toString().trim();
    }

    public static String removeParameters(String text) {
        String str = parametersAsParentheses(text);
        str = str.replaceAll("\\(.*\\)", "");
        ;
        return str;
    }

    private static String parametersAsParentheses(String text) {
        String str = sanitize(text);
        if(StringUtils.isEmpty(str))
            return StringUtils.getEmpty();
        str = str.replaceAll("\\[", "(");
        str = str.replaceAll("\\]", ")");
        str = str.replaceAll("\\{", "(");
        str = str.replaceAll("\\}", ")");
        return str;
    }

    public static String[] getParameters(String text) {
        String str = parametersAsParentheses(text);
        int amount = org.apache.commons.lang3.StringUtils.countMatches(str, "(");
        String[] answer = new String[amount];
        if (amount > 0) {
            Matcher m = Pattern.compile("\\((.*?)\\)").matcher(str);
            int i = 0;
            while (m.find()) {
                answer[i] = m.group(1);
                i++;
            }
        }

        return answer;
    }

    // Removes the package from the className
    public static String classTail(String classPath) {
        if (classPath == null) {
            return null;
        }
        if (classPath.indexOf(" ") > 0) {
            // It may not be a package or a pure class name
            return classPath;
        }

        // Analyzes if it has parameters
        String textName = parametersAsParentheses(classPath);
        int parameterPosition = textName.indexOf("(");
        if (parameterPosition > 0) {
            textName = textName.substring(0, parameterPosition);
        }
        String[] names = textName.split("\\.");
        if (names.length > 0) {
            return names[names.length - 1];
        }
        return classPath;
    }

    public static String capitalizeIfNotLowercase(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        for (String lower : lowercaseWords) {
            if (word.equalsIgnoreCase(lower)) {
                return lower;
            }
        }
        return WordUtils.capitalize(word);
    }

    public static String getSystemError(String error) {
        return "System Error: ".concat(error);
    }

    public static boolean containsIgnoreCase(String str, String subString) {
        return !isEmpty(str) && !isEmpty(subString) && str.toLowerCase().contains(subString.toLowerCase());
    }

    public static boolean isURL(String str){
        return str.startsWith("http");
    }

    private static String sanitize(String text) {
        if (text == null) {
            return text;
        }
        int max = Math.min(text.length(), maxStringSize);
        return text.substring(0, max);
    }

    public static String trimAll(String in){
        return isEmpty(in) ? "" : in.replaceAll("\\s+", "");
    }

    public static String generateClientID(){
        return StringUtils.randomString(24) + "@" + StringUtils.randomString(7) + ".i";
    }

    public static String generateSecretID(){
        return StringUtils.randomString(64);
    }

    public static String removeFirstWithCondition(String str, String condition){
        if(str.startsWith(condition))
            return removeFirst(str);
        return str;
    }

    public static String removeLastWithCondition(String str, String condition){
        if(str.endsWith(condition))
            return removeLast(str);
        return str;
    }

    public static String removeFirst(String str){
        return new StringBuilder(str).deleteCharAt(0).toString();
    }

    public static String removeLast(String str){
        return new StringBuilder(str).deleteCharAt(str.length()-1).toString();
    }

    public static List<KeyValuePair> getKeyValuePairList(List<Object[]> list) {
        List<KeyValuePair>  result = Lists.newArrayList();
        for (Object[] ob : list) {
            String path = (String) ob[0];
            String key = StringUtils.classTail(path);
            Object value =  ob[1];
            result.add(KeyValuePair.builder().key(key).value(value).description(path).build());
        }
        return result;
    }

    public static String prepareStringForSearch(String str) {
        if (str == null) {
            return "";
        }
        str = str.trim();
        str = str.toLowerCase();
        str = str.replaceAll(" ", "%");
        str = "%" + str + "%";
        return str;
    }

    public static boolean isJson(String str){
        try {
            new Gson().fromJson(str, Object.class);
            return true;
        }catch(JsonSyntaxException e){
            return false;
        }
    }

    public static String cleanup(String text) {
        if (text == null) {
            return text;
        }

        String answer = text.trim();
        answer = answer.replaceAll("\n", "");
        answer = answer.replaceAll("\r", "");
        answer = answer.replaceAll("\t", "");
        answer = answer.replaceAll("_x000D_", "");

        return answer;
    }

    // Starts with prefix and then a separator
    public static boolean startsWith(String text, String prefix) {
        if (isEmpty(text)) {
            return false;
        }

        if (text.startsWith(prefix)) {
            for (String separator : separators) {
                if (text.startsWith(prefix + separator)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static String getUsername(String email) {
        if (StringUtils.isEmpty(email)) {
            return null;
        }
        int atPlace = email.indexOf("@");
        if (atPlace > 0) {
            return email.substring(0, atPlace);
        } else {
            return email;
        }
    }
    
    public static boolean containsSameValue(String value1, String value2) {
        if (StringUtils.isEmpty(value1) || StringUtils.isEmpty(value2)) {
            return false;
        }
        
        return value1.trim().equalsIgnoreCase(value2.trim());
    }

    public static String truncateStringAtLong(String in, int length){
        if(length > in.length())
            return in;
        return in.substring(0, length);
    }
    
    public static String notNull(String text) {
        return text == null ? "" : text;
    }
    
    // concats things like server=http://servername     path=/kanban/123
    public static String concatURL(String server, String path) {
        if (StringUtils.isEmpty(server)) {
            return null;
        }
        
        if (StringUtils.isEmpty(path)) {
            return server;
        }

        if (server.endsWith("/") && path.startsWith("/")) {
            path = path.substring(2);
        }

        if (!server.endsWith("/") && !path.startsWith("/")) {
            path = "/" + path;
        }
        
        String url = server + path;
        
        return url;
    }

    public static boolean contains(List<String> list, String match) {
        if (isEmpty(match)) {
            return false;
        }
        match = match.trim();
        for (String text : list) {
            if (text.contains(match)) {
                return true;
            }
        }

        return false;
    }

    public static boolean contains(String main, String match) {
        if (isEmpty(main)  || isEmpty(match)) {
            return false;
        }
        match = match.trim().toLowerCase();
        main = main.trim().toLowerCase();

        return main.contains(match);
    }

    public static double getDistance(String longer, String shorter, double threshold) {
        return getDistance(longer, shorter, threshold, false);
    }

    public static double getDistance(String longer, String shorter, double threshold, boolean excludeNumbers) {
        if (longer.length() == 0)
            return 0;

        if (longer.length() < shorter.length())
            return -1;

        int longerLength = longer.length();
        int shorterLength = shorter.length();
        double charactersThreshold = longerLength - (longerLength * threshold);

        double distance = 0;
        for (int i = 0; i < shorterLength; i++) {
            final boolean areEqual = shorter.charAt(i) != longer.charAt(i);
            if (excludeNumbers)
                if ((!Character.isDigit(shorter.charAt(i)) || !Character.isDigit(longer.charAt(i))) && areEqual)
                    distance++;
            else
                if (areEqual)
                    distance++;
            if (distance >= charactersThreshold)
                return distance;
            if (i == shorterLength-1)
                distance += (longerLength - i - 1);
        }

        return distance;
    }

    public static double getSimilarity(String a, String b, double threshold) {
        return getSimilarity(a, b, threshold, false);
    }

    public static double getSimilarity(String a, String b, double threshold, boolean useLevenshtein) {
        String longer = a, shorter = b;
        if (a.length() < b.length()) {
            longer = b;
            shorter = a;
        }

        int longerLength = longer.length();
        if (shorter.length() < (longerLength*threshold))
            return 0;

        if (longerLength == 0)
            return 1;
        if (useLevenshtein)
            return (longerLength - LevenshteinDistance.getDefaultInstance().apply(longer, shorter)) / (double) longerLength;
        return (longerLength - getDistance(longer, shorter, threshold, true)) / (double) longerLength;
    }

    public static String encodeURL(String url) {
        if (url == null) {
            return url;
        }
        try {
            return new String(URLCodec.encodeUrl(null ,url.getBytes("UTF-8")), "US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
