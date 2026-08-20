package com.pikachu

import android.util.Log

/**
 * পিকাজু প্রোবাবিলিটি ট্রেডিং ইঞ্জিন (ProbabilityTradingEngine)
 * দশম শ্রেণির উচ্চতর গণিতের সম্ভাবনা (Chapter 14) এর লজিক অনুযায়ী 
 * ঐতিহাসিক ডেটার ভিত্তিতে ৭৮% বা তার বেশি সম্ভাবনা নিশ্চিত হলে তবেই ট্রেড সিগন্যাল দেবে।
 */
class ProbabilityTradingEngine {

    companion object {
        private const val TAG = "ProbabilityEngine"
        private const val CONFIDENCE_THRESHOLD = 0.78 // ৭৮% বা তার বেশি সম্ভাবনা হলে তবেই ট্রেড নেবে
    }

    /**
     * ঐতিহাসিক ডেটা এবং বর্তমান প্রাইস বিশ্লেষণ করে ৭৮%+ নিশ্চিত না হলে ট্রেড ব্লক করবে।
     * @param priceHistory পূর্বের দামগুলোর তালিকা (স্যাম্পল স্পেস)
     * @param currentPrice বর্তমান লাইভ প্রাইস
     * @return "UP", "DOWN" অথবা "HOLD"
     */
    fun evaluateProbabilityTrend(priceHistory: DoubleArray?, currentPrice: Double): String {
        if (priceHistory == null || priceHistory.size < 10) {
            return "HOLD"
        }

        val totalPoints = priceHistory.size
        var upwardCount = 0
        var downwardCount = 0

        // ১. নমুনা ক্ষেত্র (Sample Space) বিশ্লেষণ: অতীত ডেটা থেকে আপ ও ডাউনের ফ্রিকোয়েন্সি গণনা
        for (i in 1 until totalPoints) {
            if (priceHistory[i] > priceHistory[i - 1]) {
                upwardCount++
            } else if (priceHistory[i] < priceHistory[i - 1]) {
                downwardCount++
            }
        }

        // বর্তমান মুহূর্তের মুভমেন্ট বা ফলাফল যোগ করা
        val isCurrentUp = currentPrice > priceHistory.last()
        if (isCurrentUp) {
            upwardCount++
        } else {
            downwardCount++
        }

        val totalEvents = totalPoints.toDouble()

        // ২. সম্ভাবনার সূত্র প্রয়োগ: P(Event) = (অনুকূল ফলাফল / মোট সম্ভাব্য ফলাফল)
        val upProbability = upwardCount / totalEvents
        val downProbability = downwardCount / totalEvents

        Log.d(TAG, "Probability Analysis -> Up Prob: ${upProbability * 100}% | Down Prob: ${downProbability * 100}%")

        // ৩. কঠোর ৭৮% থ্রেশহোল্ড চেক
        return if (upProbability >= CONFIDENCE_THRESHOLD) {
            Log.d(TAG, "Decision: UP (78%+ Confidence met: ${upProbability * 100}%)")
            "UP"
        } else if (downProbability >= CONFIDENCE_THRESHOLD) {
            Log.d(TAG, "Decision: DOWN (78%+ Confidence met: ${downProbability * 100}%)")
            "DOWN"
        } else {
            Log.d(TAG, "Decision: HOLD (Probability is below 78%. No Trade Placed.)")
            "HOLD"
        }
    }
}
