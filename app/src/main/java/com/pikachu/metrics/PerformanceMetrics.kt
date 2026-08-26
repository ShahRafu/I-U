package com.pikachu.metrics

import android.util.Log
import com.pikachu.database.TradeHistoryDatabase
import com.pikachu.database.TradeRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ট্রেডিং পারফরম্যান্স মেট্রিক্স ট্র্যাকার
 * Win/Loss রেট, মোট লাভ, Accuracy শতাংশ গণনা করে
 */
class PerformanceMetrics(private val database: TradeHistoryDatabase) {

    companion object {
        private const val TAG = "PerformanceMetrics"
    }

    data class MetricsSnapshot(
        val totalTrades: Int,
        val wins: Int,
        val losses: Int,
        val winRate: Double,  // 0-100%
        val totalProfit: Double,
        val avgProfitPerTrade: Double,
        val profitFactor: Double  // মোট লাভ / মোট ক্ষতি
    )

    /**
     * সম্পূর্ণ মেট্রিক্স স্ন্যাপশট পাওয়া
     */
    suspend fun getMetricsSnapshot(): MetricsSnapshot = withContext(Dispatchers.IO) {
        val dao = database.tradeDao()
        val trades = dao.getAllTrades()
        val wins = dao.getWinCount()
        val losses = dao.getLossCount()
        val totalProfit = dao.getTotalProfit() ?: 0.0

        val total = trades.size
        val winRate = if (total > 0) (wins.toDouble() / total) * 100 else 0.0
        val avgProfit = if (total > 0) totalProfit / total else 0.0
        val profitFactor = if (losses > 0) totalProfit / (losses * 1.0) else if (totalProfit > 0) Double.POSITIVE_INFINITY else 0.0

        Log.d(TAG, "📊 Metrics: $wins wins, $losses losses, Win Rate: ${winRate.toInt()}%, Total Profit: $totalProfit")

        MetricsSnapshot(
            totalTrades = total,
            wins = wins,
            losses = losses,
            winRate = winRate,
            totalProfit = totalProfit,
            avgProfitPerTrade = avgProfit,
            profitFactor = profitFactor
        )
    }

    /**
     * নতুন ট্রেড রেকর্ড করা
     */
    suspend fun recordTrade(
        signal: String,
        confidence: Double,
        entryPrice: Double,
        exitPrice: Double,
        duration: Long
    ) = withContext(Dispatchers.IO) {
        val profit = (exitPrice - entryPrice)
        val result = if (signal == "UP" && exitPrice > entryPrice) "WIN"
        else if (signal == "DOWN" && exitPrice < entryPrice) "WIN"
        else "LOSS"

        val trade = TradeRecord(
            signal = signal,
            confidence = confidence,
            entryPrice = entryPrice,
            exitPrice = exitPrice,
            result = result,
            profit = profit,
            duration = duration
        )
        database.tradeDao().insertTrade(trade)
        Log.d(TAG, "✅ Trade Recorded: $signal @ ৳$entryPrice -> ৳$exitPrice = $result (+৳$profit)")
    }

    /**
     * সর্বশেষ N ট্রেড পাওয়া
     */
    suspend fun getRecentTrades(count: Int = 10): List<TradeRecord> = withContext(Dispatchers.IO) {
        database.tradeDao().getAllTrades().take(count)
    }
}
