package com.pikachu

import android.util.Log

/**
 * এই ফাইলটি ট্রেডিং বা ম্যাথমেটিক্যাল লজিক ব্যবহার করে 
 * মার্কেটের ট্রেন্ড জাজ বা নির্ধারণ করবে।
 */
class TradingLogicEngine {

    companion object {
        private const val TAG = "TradingLogicEngine"
    }

    /**
     * সাম্প্রতিক মূল্য এবং পূর্বের প্রাইস হিস্ট্রি বিশ্লেষণ করে ট্রেন্ড বের করবে।
     * @param priceHistory পূর্বের দামগুলোর একটি অ্যারে
     * @param currentPrice বর্তমান লাইভ প্রাইস
     * @return "WAIT", "UP" অথবা "DOWN"
     */
    fun evaluateMarketTrend(priceHistory: DoubleArray?, currentPrice: Double): String {
        // পর্যাপ্ত ডাটা না থাকলে অপেক্ষা করবে
        if (priceHistory == null || priceHistory.size < 5) {
            return "WAIT"
        }

        // সিম্পল মুভিং এভারেজ (SMA) ক্যালকুলেশন
        val sum = priceHistory.sum()
        val movingAverage = sum / priceHistory.size

        // মার্কেট কন্ডিশন চেক
        return if (currentPrice > movingAverage) {
            Log.d(TAG, "Market Trend: UP (Call)")
            "UP"
        } else if (currentPrice < movingAverage) {
            Log.d(TAG, "Market Trend: DOWN (Put)")
            "DOWN"
        } else {
            "HOLD"
        }
    }
}
