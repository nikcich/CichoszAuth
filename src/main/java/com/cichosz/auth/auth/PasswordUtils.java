package com.cichosz.auth.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtils {
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        return Base64.getEncoder().encodeToString(digest);
    }

    public static boolean checkPassword(String password, String hashedPassword) throws NoSuchAlgorithmException {
        String newHash = hashPassword(password);
        return newHash.equals(hashedPassword);
    }
}

