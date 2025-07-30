package com.upi.util;

public class MaskingUtil {

    /**
     * Masks an account number, showing only the last 4 digits
     * Example: 1234567890 -> XXXXXX7890
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        int visibleDigits = 4;
        int length = accountNumber.length();
        String lastFourDigits = accountNumber.substring(length - visibleDigits);
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < length - visibleDigits; i++) {
            masked.append("X");
        }
        masked.append(lastFourDigits);
        return masked.toString();
    }

    /**
     * Masks a phone number, showing only the last 4 digits
     * Example: 1234567890 -> XXXXXX7890
     */
    public static String maskPhoneNumber(String phoneNumber) {
        return maskAccountNumber(phoneNumber); // Same logic as account number
    }

    /**
     * Masks an email address, showing only the first character of the username
     * and the domain
     * Example: john.doe@example.com -> j****@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 1) {
            return email;
        }

        StringBuilder maskedUsername = new StringBuilder();
        maskedUsername.append(username.charAt(0));
        for (int i = 1; i < username.length(); i++) {
            maskedUsername.append("*");
        }

        return maskedUsername + "@" + domain;
    }
}