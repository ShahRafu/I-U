package com.pikachu.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.PrimaryKey

/**
 * ট্রেড ডেটা মডেল
 */
@Entity(tableName = "trades")
data class TradeRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val signal: String = "",  // UP/DOWN
    val confidence: Double = 0.0,
    val entryPrice: Double = 0.0,
    val exitPrice: Double = 0.0,
    val result: String = "",  // WIN/LOSS/PENDING
    val profit: Double = 0.0,
    val duration: Long = 0  // মিলিসেকেন্ডে
)

/**
 * ট্রেড ডেটাবেস DAO
 */
@Dao
interface TradeDao {
    @Insert
    suspend fun insertTrade(trade: TradeRecord)

    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    suspend fun getAllTrades(): List<TradeRecord>

    @Query("SELECT COUNT(*) FROM trades WHERE result = 'WIN'")
    suspend fun getWinCount(): Int

    @Query("SELECT COUNT(*) FROM trades WHERE result = 'LOSS'")
    suspend fun getLossCount(): Int

    @Query("SELECT SUM(profit) FROM trades WHERE result = 'WIN'")
    suspend fun getTotalProfit(): Double

    @Query("DELETE FROM trades")
    suspend fun clearAllTrades()
}

/**
 * ট্রেড ডেটাবেস
 */
@Database(entities = [TradeRecord::class], version = 1)
abstract class TradeHistoryDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao

    companion object {
        @Volatile
        private var INSTANCE: TradeHistoryDatabase? = null

        fun getInstance(context: Context): TradeHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TradeHistoryDatabase::class.java,
                    "trade_history.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
