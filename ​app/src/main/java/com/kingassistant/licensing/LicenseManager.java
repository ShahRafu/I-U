package com.kingassistant.licensing;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

/**
 * এই ফাইলটি ডিভাইসের ইউনিক আইডি তৈরি এবং লাইসেন্স বা অ্যাক্টিভেশন 
 * স্ট্যাটাস ম্যানেজ করার জন্য তৈরি করা হয়েছে।
 */
public class LicenseManager {

    private static final String TAG = "LicenseManager";
    private static final String PREF_NAME = "PikachuLicensePrefs";
    private static final String KEY_IS_ACTIVATED = "is_activated";
    
    // ডিভাইসের জন্য একটি ইউনিক কোড বা আইডি বের করা
    public static String getUniqueDeviceCode(Context context) {
        String androidId = Settings.Secure.getString(
            context.getContentResolver(), 
            Settings.Secure.ANDROID_ID
        );
        // সিকিউরিটির জন্য একে একটি নির্দিষ্ট ফরম্যাটে বা প্রিফিক্স সহ রিটার্ন করা যেতে পারে
        return "PIKACHU-" + (androidId != null ? androidId.toUpperCase() : "UNKNOWN_DEVICE");
    }

    // অ্যাপের ভেতরে লোকালি চেক করা ডিভাইসটি অ্যাক্টিভেটেড কি না
    public static boolean isDeviceActivated(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_ACTIVATED, false);
    }

    // কন্ট্রোল প্যানেল বা অ্যাডমিনের অনুমোদন পাওয়ার পর অ্যাপটি অ্যাক্টিভ করার মেথড
    public static void setDeviceActivated(Context context, boolean status) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_ACTIVATED, status);
        editor.apply();
        Log.d(TAG, "Device activation status updated to: " + status);
    }

    /**
     * রিমোট সার্ভার বা কন্ট্রোল প্যানেল (যেমন Firebase) থেকে চেক করার ইন্টারফেস
     * ভবিষ্যতে এখানে রিমোট ডাটাবেস কানেকশন বসানো যাবে।
     */
    public interface LicenseCheckCallback {
        void onChecked(boolean isApproved);
    }

    public static void checkRemoteLicense(Context context, LicenseCheckCallback callback) {
        String deviceCode = getUniqueDeviceCode(context);
        
        // এখানে আপনার কন্ট্রোল প্যানেল ডাটাবেস (যেমন Firebase Realtime Database) 
        // থেকে চেক করতে হবে যে এই deviceCode-এর বিপরীতে অনুমোদন আছে কি না।
        // আপাতত লোকাল স্টোরেজ চেক করে রেজাল্ট দেওয়া হচ্ছে:
        
        boolean approved = isDeviceActivated(context);
        callback.onChecked(approved);
    }
}
