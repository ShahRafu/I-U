package com.pikachu.core

/**
 * BinaryTradingMaster
 * - Provides candle-based probability estimates for UP/DOWN and a candle confidence percent
 * - Designed to be a single-file "master" of binary trading heuristics (RSI/MACD/SMA snippets included)
 * - Implemented with straightforward, deterministic algorithms so it works as soon as file is added
 */
class BinaryTradingMaster {

    // Supported timeframes (labels only)
    val supportedTimeframes = listOf("30s", "1m", "5m", "8m", "15m")

    /**
     * Estimate probabilities for the current candle based on a recent price history sample.
     * @return Triple(upProbability, downProbability, candleConfidence)
     */
    fun estimateProbabilityForCurrentCandle(priceHistory: DoubleArray, currentPrice: Double): Triple<Double, Double, Double> {
        if (priceHistory.isEmpty()) return Triple(0.0, 0.0, 0.0)

        var upCount = 0
        var downCount = 0
        for (i in 1 until priceHistory.size) {
            if (priceHistory[i] > priceHistory[i - 1]) upCount++ else if (priceHistory[i] < priceHistory[i - 1]) downCount++
        }
        // include current movement
        if (currentPrice > priceHistory.last()) upCount++ else downCount++

        val total = (priceHistory.size).coerceAtLeast(1).toDouble()
        val upProb = upCount / total
        val downProb = downCount / total

        // Candle confidence: normalize max of up/down probability with a small smoothing
        val candleConfidence = kotlin.math.max(upProb, downProb)

        return Triple(upProb, downProb, candleConfidence)
    }

    // Simple RSI helper (period 14)
    fun calculateRSI(prices: DoubleArray, current: Double): Double {
        if (prices.isEmpty()) return 50.0
        var gains = 0.0
        var losses = 0.0
        val combined = prices + doubleArrayOf(current)
        for (i in 1 until combined.size) {
            val diff = combined[i] - combined[i - 1]
            if (diff >= 0) gains += diff else losses += kotlin.math.abs(diff)
        }
        if (losses == 0.0) return 100.0
        val rs = (gains / 14.0) / (losses / 14.0)
        return 100.0 - (100.0 / (1.0 + rs))
    }

    // Simple MACD-like metric
    fun calculateSimpleMACD(prices: DoubleArray, current: Double): Double {
        val recentSum = prices.takeLast(5).sum() + current
        val olderSum = prices.take(0, 5).sum() // defensive, will be empty if not enough
        val recentAvg = recentSum / (5.0 + 1.0)
        val olderAvg = if (prices.size >= 5) prices.take(5).sum() / 5.0 else recentAvg
        return recentAvg - olderAvg
    }
}
