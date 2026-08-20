package com.pikachu

import android.util.Log
import kotlin.math.abs

/**
 * পিকাজু অ্যাডভান্সড ট্রেডিং লজিক ইঞ্জিন (নতুন ফাইল)
 * এতে RSI, MACD, প্রাইস অ্যাকশন এবং স্টপ-লস যুক্ত করা হয়েছে। 
 * আগের বেসিক লজিকটি (SMA) TradingLogicEngine-এ সংরক্ষিত আছে।
 */
class AdvancedTradingLogicEngine {

    companion object {
        private const val TAG = "AdvancedLogicEngine"
        private var consecutiveLosses = 0
        private const val MAX_CONSECUTIVE_LOSSES = 3 
    }

    fun evaluateAdvancedTrend(priceHistory: DoubleArray?, currentPrice: Double): String {
        // ১. রিস্ক ম্যানেজমেন্ট (Stop-Loss)
        if (consecutiveLosses >= MAX_CONSECUTIVE_LOSSES) {
            Log.d(TAG, "Risk Alert: Max losses reached. Trading Stopped!")
            return "STOP"
        }

        if (priceHistory == null || priceHistory.size < 14) {
            return "HOLD"
        }

        // ২. RSI ক্যালকুলেশন
        val rsi = calculateRSI(priceHistory, currentPrice)

        // ৩. MACD সিগন্যাল
        val macdSignal = calculateMACD(priceHistory, currentPrice)

        // ৪. ক্যান্ডেলস্টিক প্যাটার্ন
        val isBullishEngulfing = currentPrice > priceHistory.last() && priceHistory.last() < priceHistory[priceHistory.size - 2]
        val isBearishEngulfing = currentPrice < priceHistory.last() && priceHistory.last() > priceHistory[priceHistory.size - 2]

        // ৫. ফাইনাল ডিসিশন
        if ((rsi < 30.0 || isBullishEngulfing) && macdSignal > 0) {
            Log.d(TAG, "Advanced Decision: UP")
            return "UP"
        }
        
        if ((rsi > 70.0 || isBearishEngulfing) && macdSignal < 0) {
            Log.d(TAG, "Advanced Decision: DOWN")
            return "DOWN"
        }

        return "HOLD"
    }

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

    private fun calculateMACD(prices: DoubleArray, current: Double): Double {
        val recentSum = prices.takeLast(5).sum() + current
        val olderSum = prices.take(5).sum()
        return (recentSum / 6.0) - (olderSum / 5.0) 
    }

    fun recordTradeResult(isWin: Boolean) {
        if (isWin) consecutiveLosses = 0 else consecutiveLosses++
    }
}
