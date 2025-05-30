package com.example.quizz_project;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_ID = "user_id";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_COINS = "user_coins"; // Add this constant

    private SharedPreferences sharedPref;
    private static SessionManager instance;

    private SessionManager(Context context) {
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return sharedPref.contains(KEY_ID);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_ID, user.id);
        editor.putString(KEY_NAME, user.name);
        editor.putString(KEY_EMAIL, user.email);
        editor.putInt(KEY_COINS, user.coins); // Save coins
        editor.apply();
    }

    public User getCurrentUser() {
        if (!sharedPref.contains(KEY_ID)) {
            return null;
        }

        User user = new User();
        user.id = sharedPref.getInt(KEY_ID, -1);
        user.name = sharedPref.getString(KEY_NAME, "");
        user.email = sharedPref.getString(KEY_EMAIL, "");
        user.coins = sharedPref.getInt(KEY_COINS, 0); // Retrieve coins
        return user;
    }

    // Add method to update coins
    public void updateCoins(int newCoins) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_COINS, newCoins);
        editor.apply();
    }

    public void clearSession() {
        sharedPref.edit().clear().apply();
    }

    public void clearUser() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(KEY_ID);
        editor.remove(KEY_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_COINS);
        editor.apply();
    }


    public static class User {
        public int id;
        public String name;
        public String email;
        public int coins;
    }
}