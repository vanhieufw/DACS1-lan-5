package com.movie.util;

public class GenerateAdminPassword {
    public static void main(String[] args) {
        String password = "123456789";
        String hashedPassword = PasswordEncrypter.encrypt(password);
        System.out.println("Mật khẩu mã hóa cho admin: " + hashedPassword);
    }
}