package com.pikachu

import android.util.Log

/**
 * এই ফাইলটি ট্রেন্ড বা সাংখ্যিক লজিক ব্যবহার করে 
 * মার্কেট আপ নাকি ডাউন হবে তা ডিসাইড করবে।
 */
class TradingLogicEngine {

    companion object {
        private const val TAG = "TradingLogicEngine"
    }

    // মার্কেটর হিসাব ছোট এবং বর্তমান অবজারভেশন করার মূল মেথড
    fun evaluateMarketTrend(priceHistory: DoubleArray?, currentPrice: Double): String {
        if (priceHistory == null || priceHistory.size < 5) {
            return "WAIT" // পর্যাপ্ত ডেটা না থাকলে অপেক্ষা করবে
        }

        // সিম্পল মুভিং এভারেজ (SMA) ক্যালকুলেশন
        val sum = priceHistory.sum()
        val movingAverage = sum / priceHistory.size

        // SMA তুলনামূলক লজিক চেক
        if (currentPrice > movingAverage) {
            Log.d(TAG, "Market Trend: UP (Call)")
            return "UP"
        } else if (currentPrice < movingAverage) {
            Log.d(TAG, "Market Trend: DOWN (Put)")
            return "DOWN"
        }

        return "HOLD"
    }
}
