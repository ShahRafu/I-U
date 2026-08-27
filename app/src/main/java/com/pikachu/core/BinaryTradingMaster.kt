package com.pikachu.core

class BinaryTradingMaster {

    val supportedTimeframes = listOf("30s", "1m", "5m", "8m", "15m")

    fun estimateProbabilityForCurrentCandle(priceHistory: DoubleArray, currentPrice: Double): Triple<Double, Double, Double> {
        if (priceHistory.isEmpty()) return Triple(0.0, 0.0, 0.0)

        var upCount = 0
        var downCount = 0
        for (i in 1 until priceHistory.size) {
            if (priceHistory[i] > priceHistory[i - 1]) upCount++ else if (priceHistory[i] < priceHistory[i - 1]) downCount++
        }
        if (currentPrice > priceHistory.last()) upCount++ else downCount++

        val total = (priceHistory.size).coerceAtLeast(1).toDouble()
        val upProb = upCount / total
        val downProb = downCount / total
        val candleConfidence = kotlin.math.max(upProb, downProb)

        return Triple(upProb, downProb, candleConfidence)
    }

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

    fun calculateSimpleMACD(prices: DoubleArray, current: Double): Double {
        val combined = if (prices.isEmpty()) doubleArrayOf(current) else prices + doubleArrayOf(current)
        val recentWindow = 6
        val recentStart = kotlin.math.max(0, combined.size - recentWindow)
        val recent = combined.sliceArray(recentStart until combined.size)
        val recentAvg = if (recent.isNotEmpty()) recent.sum() / recent.size.toDouble() else 0.0

        val older = if (recentStart > 0) combined.sliceArray(0 until recentStart) else doubleArrayOf()
        val olderAvg = if (older.isNotEmpty()) older.sum() / older.size.toDouble() else recentAvg

        return recentAvg - olderAvg
    }
}
