package com.pikachu

import android.util.Log

/**
 * পিকাজু প্রোবাবিলিটি ট্রেডিং ইঞ্জিন (Probability Trading Engine)
 * দশম শ্রেণির উচ্চতর গণিতের সম্ভাবনা (Chapter 14) এর লজিক অনুযায়ী 
 * ঐতিহাসিক ডেটার ভিত্তিতে অনুকূল ফলাফলের শতাংশ বা সম্ভাবনা হিসাব করে ট্রেড সিদ্ধান্ত নেয়।
 */
class ProbabilityTradingEngine {

    companion object {
        private const val TAG = "ProbabilityEngine"
        private const val CONFIDENCE_THRESHOLD = 0.70 // ৭০% বা তার বেশি সম্ভাবনা হলে তবেই ট্রেড সিগন্যাল দেবে
    }

    /**
     * ঐতিহাসিক ডেটা এবং বর্তমান প্রাইস বিশ্লেষণ করে আপ বা ডাউনের সম্ভাব্যতা (Probability) বের করে।
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

        // মোট ইভেন্ট বা ট্রায়াল সংখ্যা
        val totalEvents = (totalPoints).toDouble()

        // ২. সম্ভাবনার সূত্র প্রয়োগ: P(Event) = (অনুকূল ফলাফল / মোট সম্ভাব্য ফলাফল)
        val upProbability = upwardCount / totalEvents
        val downProbability = downwardCount / totalEvents

        Log.d(TAG, "Probability Analysis -> Up Prob: ${upProbability * 100}% | Down Prob: ${downProbability * 100}%")

        // ৩. থ্রেশহোল্ড (নির্দিষ্ট সম্ভাবনা লেভেল) চেক করে ফাইনাল ডিসিশন নেওয়া
        return if (upProbability >= CONFIDENCE_THRESHOLD) {
            Log.d(TAG, "Decision: UP (Probability met threshold: ${upProbability * 100}%)")
            "UP"
        } else if (downProbability >= CONFIDENCE_THRESHOLD) {
            Log.d(TAG, "Decision: DOWN (Probability met threshold: ${downProbability * 100}%)")
            "DOWN"
        } else {
            Log.d(TAG, "Decision: HOLD (Probability is below confidence threshold)")
            "HOLD"
        }
    }
}
