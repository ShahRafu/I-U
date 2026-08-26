package com.pikachu

import android.util.Log
import kotlin.math.abs

/**
 * মার্কেট ডেটা প্রসেসর - রিয়েল-টাইম প্রাইস ডেটা প্রসেসিং এবং স্মুথিং
 */
class MarketDataProcessor {

    companion object {
        private const val TAG = "MarketDataProcessor"
        private const val OUTLIER_THRESHOLD = 5.0  // ৫% বেশি পরিবর্তন = আউটলায়ার
    }

    private val priceBuffer = mutableListOf<Double>()
    private val maxBufferSize = 100

    /**
     * নতুন প্রাইস ডেটা যোগ করা এবং প্রসেস করা
     */
    fun addPrice(price: Double): ProcessedData {
        // আউটলায়ার ফিল্টার করা
        val filteredPrice = if (priceBuffer.isNotEmpty()) {
            val lastPrice = priceBuffer.last()
            val changePercent = abs((price - lastPrice) / lastPrice) * 100
            
            if (changePercent > OUTLIER_THRESHOLD) {
                Log.w(TAG, "⚠️ Outlier detected: ${changePercent.toInt()}% change - adjusting")
                lastPrice * (1 + (OUTLIER_THRESHOLD / 100) * if (price > lastPrice) 1 else -1)
            } else {
                price
            }
        } else {
            price
        }

        priceBuffer.add(filteredPrice)
        if (priceBuffer.size > maxBufferSize) {
            priceBuffer.removeAt(0)
        }

        return ProcessedData(
            currentPrice = filteredPrice,
            movingAverage = calculateSMA(20),
            volatility = calculateVolatility(),
            trend = determineTrend(),
            bufferSize = priceBuffer.size
        )
    }

    /**
     * সাধারণ মুভিং এভারেজ (SMA) গণনা
     */
    private fun calculateSMA(period: Int): Double {
        if (priceBuffer.size < period) return priceBuffer.average()
        return priceBuffer.takeLast(period).average()
    }

    /**
     * বোলিঞ্জার ব্যান্ড - Volatility গণনা
     */
    private fun calculateVolatility(): Double {
        if (priceBuffer.size < 20) return 0.0
        
        val sma = calculateSMA(20)
        val variance = priceBuffer.takeLast(20)
            .map { (it - sma) * (it - sma) }
            .average()
        
        return kotlin.math.sqrt(variance) / sma * 100  // শতাংশে
    }

    /**
     * বর্তমান ট্রেন্ড নির্ধারণ
     */
    private fun determineTrend(): String {
        if (priceBuffer.size < 2) return "NEUTRAL"
        
        val shortMA = calculateSMA(5)
        val longMA = calculateSMA(20)
        
        return when {
            shortMA > longMA * 1.01 -> "STRONG_UP"
            shortMA > longMA -> "UP"
            shortMA < longMA * 0.99 -> "STRONG_DOWN"
            shortMA < longMA -> "DOWN"
            else -> "NEUTRAL"
        }
    }

    /**
     * সমস্ত ডেটা ক্লিয়ার করা
     */
    fun reset() {
        priceBuffer.clear()
        Log.d(TAG, "🔄 Market data reset")
    }

    /**
     * প্রসেস করা মার্কেট ডেটা
     */
    data class ProcessedData(
        val currentPrice: Double,
        val movingAverage: Double,
        val volatility: Double,  // শতাংশে
        val trend: String,       // STRONG_UP/UP/DOWN/STRONG_DOWN/NEUTRAL
        val bufferSize: Int
    )
}
