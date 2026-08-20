package com.pikachu.licensing

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log

/**
 * এই ফাইলটি ডিভাইসের ইউনিক আইডি তৈরি এবং লাইসেন্স বা অ্যাক্টিভেশন 
 * স্ট্যাটাস ম্যানেজ করার জন্য তৈরি করা হয়েছে।
 */
object LicenseManager {

    private const val TAG = "LicenseManager"
    private const val PREF_NAME = "PikachuLicensePrefs"
    private const val KEY_IS_ACTIVATED = "is_activated"

    /**
     * ডিভাইসের জন্য একটি ইউনিক কোড বা আইডি বের করা
     */
    fun getUniqueDeviceCode(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        // সিকিউরিটির জন্য একে একটি নির্দিষ্ট ফরম্যাটে বা প্রিফিক্স সহ রিটার্ন করা
        return "PIKACHU-" + (androidId?.uppercase() ?: "UNKNOWN_DEVICE")
    }

    /**
     * অ্যাপের ভেতরে লোকালি চেক করা ডিভাইসটি অ্যাক্টিভেটেড কি না
     */
    fun isDeviceActivated(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_ACTIVATED, false)
    }

    /**
     * কন্ট্রোল প্যানেল বা অ্যাডমিনের অনুমোদন পাওয়ার পর অ্যাপটি অ্যাক্টিভ করার মেথড
     */
    fun setDeviceActivated(context: Context, status: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_IS_ACTIVATED, status)
            apply()
        }
        Log.d(TAG, "Device activation status updated to: $status")
    }

    /**
     * রিমোট সার্ভার বা কন্ট্রোল প্যানেল (যেমন Firebase) থেকে চেক করার ইন্টারফেস
     */
    interface LicenseCheckCallback {
        fun onChecked(isApproved: Boolean)
    }

    fun checkRemoteLicense(context: Context, callback: LicenseCheckCallback) {
        val deviceCode = getUniqueDeviceCode(context)

        // এখানে আপনার কন্ট্রোল প্যানেল ডাটাবেস থেকে চেক করতে হবে যে 
        // এই deviceCode-এর বিপরীতে অনুমোদন আছে কি না।
        // আপাতত লোকাল স্টোরেজ চেক করে রেজাল্ট দেওয়া হচ্ছে:
        val approved = isDeviceActivated(context)
        callback.onChecked(approved)
    }
}
