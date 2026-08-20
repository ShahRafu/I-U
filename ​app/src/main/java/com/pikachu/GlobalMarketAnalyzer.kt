package com.pikachu

import android.util.Log

/**
 * এই ফাইলটি আন্তর্জাতিক বাজার বা গ্লোবাল মার্কেট অ্যানালাইসিস করবে
 * এবং ট্রেডিংয়ের জন্য অনুকূল পরিবেশ আছে কিনা তা সরাসরি যাচাই করবে।
 */
class GlobalMarketAnalyzer {

    companion object {
        private const val TAG = "GlobalMarketAnalyzer"
    }

    /**
     * আন্তর্জাতিক বাজার বা গ্লোবাল মার্কেট অনুকূলে আছে কিনা তা যাচাই করার মূল মেথড।
     * @param volatilityIndex বাজারের ভোলাটিলিটি বা অস্থিরতার মাত্রা
     * @param isHighImpactNewsTime উচ্চ-ঝুঁকিপূর্ণ বা বড় কোনো নিউজ প্রকাশিত হওয়ার সময় কি না
     * @return true হলে ট্রেড করা যাবে, false হলে ট্রেড ব্লক থাকবে
     */
    fun isGlobalMarketFavorable(volatilityIndex: Double, isHighImpactNewsTime: Boolean): Boolean {
        // উচ্চ-ঝুঁকিপূর্ণ নিউজ বা ইভেন্টের সময় হলে ট্রেড সম্পূর্ণ নিরাপদ রাখতে ব্লক করা হবে
        if (isHighImpactNewsTime) {
            Log.d(TAG, "Global Market: UNSAFE (High Impact News Detected)")
            return false
        }

        // বাজারের ভোলাটিলিটি যদি অস্বাভাবিকভাবে অনেক বেশি বা একদম কম থাকে
        if (volatilityIndex > 85.0 || volatilityIndex < 15.0) {
            Log.d(TAG, "Global Market: UNSAFE (Abnormal Volatility: $volatilityIndex)")
            return false
        }

        Log.d(TAG, "Global Market: FAVORABLE for Trading")
        return true
    }
}
