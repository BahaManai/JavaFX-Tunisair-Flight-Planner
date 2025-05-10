package com.beginsecure.tunisairaeroplan.utils;
public class SessionManager {
    private static boolean isLoggedIn = false;
    private static String currentUser;

    public static void login(String username) {
        isLoggedIn = true;
        currentUser = username;
    }

    public static void logout() {
        isLoggedIn = false;
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static String getCurrentUser() {
        return currentUser;
    }
}