package com.example.alex.moneymanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.alex.moneymanager.entities.User;
import com.google.gson.Gson;

public class PreferenceUtil {

    private static final String USER = "prefs_key_user";
    private static final String BALANCE = "prefs_key_balance";

    private SharedPreferences sharedPreferences;

    private Gson gson = new Gson();

    public PreferenceUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(
                context.getPackageName(),
                Context.MODE_PRIVATE
        );
    }

    public User getUser() {
        return gson.fromJson(sharedPreferences.getString(USER, null), User.class);
    }

    public void setUser(User user) {
        updateStringValue(USER, gson.toJson(user));
    }

    public double getBalance() {
        return sharedPreferences.getFloat(BALANCE, 0f);
    }

    public void setBalance(float balance) {
        updateDoubleValue(BALANCE, balance);
    }

    private void updateBooleanValue(String key, boolean value) {
        sharedPreferences.edit()
                .putBoolean(key, value)
                .apply();
    }

    private void updateStringValue(String key, String value) {
        sharedPreferences.edit()
                .putString(key, value)
                .apply();
    }

    private void updateDoubleValue(String key, float value) {
        sharedPreferences.edit()
                .putFloat(key, value)
                .apply();
    }
}