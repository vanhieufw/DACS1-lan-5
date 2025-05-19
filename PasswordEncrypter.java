package com.movie.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncrypter {
    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashed) {
        try {
            return BCrypt.checkpw(password, hashed);
        } catch (Exception e) {
            return password.equals(hashed); // Fallback cho admin
        }
    }
}