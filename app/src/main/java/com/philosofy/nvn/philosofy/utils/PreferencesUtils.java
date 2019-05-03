package com.philosofy.nvn.philosofy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.philosofy.nvn.philosofy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreferencesUtils {

    static boolean areNotificationsEnabled(Context context) {
        String key = context.getString(R.string.pref_show_qod_key);

        boolean defaultValue = context.getResources().getBoolean(R.bool.qod_notif_by_default);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isNotificationsEnabled = sharedPreferences.getBoolean(key, defaultValue);

        return isNotificationsEnabled;
    }

    static long getElapsedTimeSinceLastNotification(Context context) {
        String key = context.getString(R.string.pref_last_notif_key);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        long lastNotificationTime = sharedPreferences.getLong(key, 0);

        long timeSinceLastNotification = System.currentTimeMillis() - lastNotificationTime;

        return timeSinceLastNotification;
    }

    static void saveLastNotificationTime(Context context, long timeOfNotification) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String key = context.getString(R.string.pref_last_notif_key);
        editor.putLong(key, timeOfNotification);
        editor.apply();
    }

    static boolean isSafeSearchOn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getString(R.string.pref_images_safesearch_key);
        boolean defaultValue = context.getResources().getBoolean(R.bool.images_safesearch_default);

        boolean isSafeSearchOn = sharedPreferences.getBoolean(key, defaultValue);

        return isSafeSearchOn;
    }

    static String getPreferredImagesOrientation(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getString(R.string.pref_images_orientation_key);
        String defaultValue = context.getResources().getString(R.string.pref_images_orientation_default);

        String orientation = sharedPreferences.getString(key, defaultValue);

        return orientation;
    }

    static ArrayList<String> getPreferredQodCategories(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getString(R.string.pref_qod_category_key);
        Set<String> defaultCategories = new HashSet<>(Arrays.asList(context.getResources()
                .getStringArray(R.array.qod_categories_default_values)));

        Set<String> qodCategoriesSet = sharedPreferences.getStringSet(key, defaultCategories);

        ArrayList<String> qodCategoriesList = new ArrayList<>(qodCategoriesSet);

        return qodCategoriesList;
    }

    public static boolean isSavingSharedPreferred(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getString(R.string.pref_save_shared_key);
        boolean defaultValue = context.getResources().getBoolean(R.bool.save_shared_default);
        boolean isSavingPreferred = sharedPreferences.getBoolean(key, defaultValue);

        return isSavingPreferred;
    }

    public static long getElapsedTimeSinceLastImagesUpdate(Context context) {
        String key = context.getString(R.string.pref_last_image_update_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        long lastUpdateTime = sharedPreferences.getLong(key, 0);
        long timeSinceLastUpdate = System.currentTimeMillis() - lastUpdateTime;

        return  timeSinceLastUpdate;
    }

    public static void saveLastImageUpdateTime(Context context, long timeOfLastUpdate) {
        String key = context.getString(R.string.pref_last_image_update_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, timeOfLastUpdate);
        editor.apply();
    }

    public static ArrayList<Integer> getRecentColors(Context context) {
        String key1 = context.getString(R.string.pref_recent_color_1_key);
        String key2 = context.getString(R.string.pref_recent_color_2_key);
        String key3 = context.getString(R.string.pref_recent_color_3_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int defaultColor1 = ContextCompat.getColor(context, R.color.colorPrimaryLight);
        int defaultColor2 = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        int defaultColor3 = ContextCompat.getColor(context, R.color.colorAccent);

        int color1 = sharedPreferences.getInt(key1, defaultColor1);
        int color2 = sharedPreferences.getInt(key2, defaultColor2);
        int color3 = sharedPreferences.getInt(key3, defaultColor3);

        ArrayList<Integer> colorsList = new ArrayList<>();
        colorsList.add(color1);
        colorsList.add(color2);
        colorsList.add(color3);

        return colorsList;
    }

    public static void updateRecentColors(Context context, int color) {
        String key1 = context.getString(R.string.pref_recent_color_1_key);
        String key2 = context.getString(R.string.pref_recent_color_2_key);
        String key3 = context.getString(R.string.pref_recent_color_3_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        ArrayList<Integer> colorsList = getRecentColors(context);
        if (colorsList.contains(color)) {
            colorsList.remove(color);
            colorsList.add(0, color);
        } else {
            colorsList.remove(2);
            colorsList.add(0, color);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key1, colorsList.get(0));
        editor.putInt(key2, colorsList.get(1));
        editor.putInt(key3, colorsList.get(2));

        editor.apply();
    }
}
