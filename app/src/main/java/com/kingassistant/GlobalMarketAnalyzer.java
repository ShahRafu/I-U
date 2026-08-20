package com.kingassistant;

import android.util.Log;

/**
 * এই ফাইলটি আন্তর্জাতিক বাজার বা গ্লোবাল মার্কেটের অস্থিরতা 
 * এবং কারেন্সি স্ট্রেন্থ অ্যানালাইসিস করার জন্য তৈরি।
 */
public class GlobalMarketAnalyzer {

    private static final String TAG = "GlobalMarketAnalyzer";

    // আন্তর্জাতিক মার্কেটের ডেটা ফিল্টার করে ট্রেড নেওয়ার উপযোগী কিনা তা যাচাই করা
    public boolean isGlobalMarketFavorable(double volatilityIndex, boolean isHighImpactNewsTime) {
        // যদি হাই-ইম্প্যাক্ট নিউজ বা অতিরিক্ত প্যানিক মার্কেট থাকে, তবে ট্রেড ব্লক করবে
        if (isHighImpactNewsTime) {
            Log.d(TAG, "Global Market: UNSAFE (High Impact News Detected)");
            return false; 
        }

        // ভোলাটিলিটি বা অস্থিরতা যদি খুব বেশি বা অস্বাভাবিক হয়
        if (volatilityIndex > 85.0 || volatilityIndex < 15.0) {
            Log.d(TAG, "Global Market: UNSAFE (Abnormal Volatility)");
            return false;
        }

        Log.d(TAG, "Global Market: FAVORABLE for Trading");
        return true;
    }
}
