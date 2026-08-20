package com.kingassistant;

import android.util.Log;

/**
 * এই ফাইলটি লাইব্রেরি বা ম্যাথম্যাটিক্যাল লজিক ব্যবহার করে 
 * মার্কেট আপ নাকি ডাউন হবে তা হিসাব করবে।
 */
public class TradingLogicEngine {

    private static final String TAG = "TradingLogicEngine";

    // মার্কেটের বিগত ডেটা এবং বর্তমান অবস্থা অ্যানালাইসিস করার মূল মেথড
    public String evaluateMarketTrend(double[] priceHistory, double currentPrice) {
        if (priceHistory == null || priceHistory.length < 5) {
            return "WAIT"; // পর্যাপ্ত ডেটা না থাকলে অপেক্ষা করবে
        }

        // সিম্পল মুভিং এভারেজ (SMA) ক্যালকুলেশন
        double sum = 0;
        for (double price : priceHistory) {
            sum += price;
        }
        double movingAverage = sum / priceHistory.length;

        // ৯০% কনফার্মেশন লজিক চেক
        if (currentPrice > movingAverage) {
            Log.d(TAG, "Market Trend: UP (Call)");
            return "UP";
        } else if (currentPrice < movingAverage) {
            Log.d(TAG, "Market Trend: DOWN (Put)");
            return "DOWN";
        }

        return "HOLD";
    }
}
