package com.pikachu.core

import android.util.Log
import kotlin.math.abs

/**
 * উন্নত সম্ভাব্যতা ট্রেডিং ইঞ্জিন - ৮০% থ্রেশহোল্ড সহ
 * RSI, MACD, এবং মূল্য গতিশীলতা বিশ্লেষণ করে শক্তিশালী সিগন্যাল প্রদান করে
 */
class ImprovedProbabilityEngine {

    companion object {
        private const val TAG = "ImprovedProbabilityEngine"
        private const val CONFIDENCE_THRESHOLD = 0.80  // ৮০% থ্রেশহোল্ড
        private const val RSI_OVERBOUGHT = 70.0
        private const val RSI_OVERSOLD = 30.0
    }

    /**
     * মাল্টি-ফ্যাক্টর বিশ্লেষণ: রিটার্ন করে (সিগন্যাল, কনফিডেন্স, বিস্তারিত)
     */
    fun analyzeMarket(
        priceHistory: DoubleArray,
        currentPrice: Double
    ): Triple<String, Double, String> {
        if (priceHistory.isEmpty() || priceHistory.size < 10) {
            return Triple("HOLD", 0.0, "Insufficient data")
        }

        // ১. মূল্য গতিবিদ্যা বিশ্লেষণ
        val priceDirection = analyzePriceDirection(priceHistory, currentPrice)
        val priceConfidence = calculateDirectionConfidence(priceHistory, currentPrice)

        // ২. RSI ক্যালকুলেশন
        val rsi = calculateRSI(priceHistory, currentPrice)
        val rsiSignal = when {
            rsi < RSI_OVERSOLD -> "BULLISH"  // অতিবিক্রয়
            rsi > RSI_OVERBOUGHT -> "BEARISH" // অতিক্রয়
            else -> "NEUTRAL"
        }
        val rsiConfidence = abs(rsi - 50.0) / 50.0  // 0-1 রেঞ্জ

        // ৩. MACD সিগন্যাল
        val (macdSignal, macdConfidence) = calculateMACD(priceHistory, currentPrice)

        // ৪. চূড়ান্ত সিদ্ধান্ত
        val finalConfidence = (priceConfidence * 0.4 + rsiConfidence * 0.35 + macdConfidence * 0.25)
        val finalSignal = when {
            priceDirection == "UP" && rsiSignal != "BEARISH" && macdSignal == "UP" && finalConfidence >= CONFIDENCE_THRESHOLD -> {
                Log.d(TAG, "🟢 STRONG UP SIGNAL - Confidence: ${finalConfidence * 100}%")
                "UP"
            }
            priceDirection == "DOWN" && rsiSignal != "BULLISH" && macdSignal == "DOWN" && finalConfidence >= CONFIDENCE_THRESHOLD -> {
                Log.d(TAG, "🔴 STRONG DOWN SIGNAL - Confidence: ${finalConfidence * 100}%")
                "DOWN"
            }
            else -> {
                Log.d(TAG, "⚪ HOLD - Confidence: ${finalConfidence * 100}% (Threshold: 80%)")
                "HOLD"
            }
        }

        val details = "Price: $priceDirection ($priceConfidence%) | RSI: $rsiSignal ($rsiConfidence%) | MACD: $macdSignal"
        return Triple(finalSignal, finalConfidence, details)
    }

    /**
     * মূল্য দিকনির্দেশনা বিশ্লেষণ
     */
    private fun analyzePriceDirection(priceHistory: DoubleArray, currentPrice: Double): String {
        var upCount = 0
        var downCount = 0
        for (i in 1 until priceHistory.size) {
            if (priceHistory[i] > priceHistory[i - 1]) upCount++ else downCount++
        }
        if (currentPrice > priceHistory.last()) upCount++ else downCount++
        return if (upCount > downCount) "UP" else "DOWN"
    }

    /**
     * দিকনির্দেশনা আত্মবিশ্বাস গণনা
     */
    private fun calculateDirectionConfidence(priceHistory: DoubleArray, currentPrice: Double): Double {
        var upCount = 0
        var downCount = 0
        for (i in 1 until priceHistory.size) {
            if (priceHistory[i] > priceHistory[i - 1]) upCount++ else downCount++
        }
        if (currentPrice > priceHistory.last()) upCount++ else downCount++
        val total = upCount + downCount
        return if (total > 0) maxOf(upCount, downCount) / total.toDouble() else 0.0
    }

    /**
     * RSI (রিলেটিভ স্ট্রেংথ ইন্ডেক্স) গণনা
     */
    private fun calculateRSI(prices: DoubleArray, current: Double): Double {
        var gains = 0.0
        var losses = 0.0
        val combined = prices + doubleArrayOf(current)
        
        for (i in 1 until combined.size) {
            val diff = combined[i] - combined[i - 1]
            if (diff >= 0) gains += diff else losses += abs(diff)
        }
        
        if (losses == 0.0) return 100.0
        val rs = (gains / 14.0) / (losses / 14.0)
        return 100.0 - (100.0 / (1.0 + rs))
    }

    /**
     * MACD (মুভিং এভারেজ কনভার্জেন্স ডাইভার্জেন্স) গণনা
     */
    private fun calculateMACD(prices: DoubleArray, current: Double): Pair<String, Double> {
        val shortEMA = calculateEMA(prices, current, 12)
        val longEMA = calculateEMA(prices, current, 26)
        val macdLine = shortEMA - longEMA
        
        val signal = if (macdLine > 0) "UP" else "DOWN"
        val confidence = abs(macdLine) / (shortEMA + 1)  // নর্মালাইজ করা
        return Pair(signal, confidence.coerceIn(0.0, 1.0))
    }

    /**
     * এক্সপোনেনশিয়াল মুভিং এভারেজ গণনা
     */
    private fun calculateEMA(prices: DoubleArray, current: Double, period: Int): Double {
        val combined = prices + doubleArrayOf(current)
        val multiplier = 2.0 / (period + 1)
        var ema = combined.take(period).average()
        
        for (i in period until combined.size) {
            ema = combined[i] * multiplier + ema * (1 - multiplier)
        }
        return ema
    }
}
