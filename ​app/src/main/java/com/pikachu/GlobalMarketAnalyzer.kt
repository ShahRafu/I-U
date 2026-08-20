package com.pikachu

import android.util.Log

/**
 * এই ফাইলটি আন্তর্জাতিক বাজার বা গ্লোবাল মার্কেট অ্যানালাইসিস করবে
 * এবং ট্রেডিংয়ের জন্য অনুকূল পরিবেশ আছে কিনা তা চেক করবে।
 */
class GlobalMarketAnalyzer {

    companion object {
        private const val TAG = "GlobalMarketAnalyzer"
    }

    // আন্তর্জাতিক বাজার বা গ্লোবাল মার্কেট অনুকূলে আছে কিনা তা যাচাই করার মেথড
    fun isGlobalMarketFavorable(volatilityIndex: Double, isHighImpactNewsTime: Boolean): Boolean {
        // আন্তর্জাতিক সার্ভার বা নিউজ টাইম ফিন্ড করলে ট্রেড ব্লক করার জন্য চেক
        if (isHighImpactNewsTime) {
            Log.d(TAG, "Global Market: UNSAFE (High Impact News Detected)")
            return false
        }

        // ভোলাটিলিটি বা অস্থিরতা যদি খুব বেশি বা অস্বাভাবিক হয়
        if (volatilityIndex > 85.0 || volatilityIndex < 15.0) {
            Log.d(TAG, "Global Market: UNSAFE (Abnormal Volatility)")
            return false
        }

        Log.d(TAG, "Global Market: FAVORABLE for Trading")
        return true
    }
}
