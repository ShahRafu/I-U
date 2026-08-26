package com.pikachu.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.pikachu.R
import com.pikachu.core.ImprovedProbabilityEngine
import com.pikachu.data.PriceDataCapture
import com.pikachu.database.TradeHistoryDatabase
import com.pikachu.metrics.PerformanceMetrics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * প্রধান ট্রেড সার্ভিস - সমস্ত অটো-ট্রেডিং লজিক অর্কেস্ট্রেট করে
 * ৮০% কনফিডেন্স থাকলে অটোমেটিক ট্রেড এক্সিকিউট করে
 */
class TradeService : Service() {

    companion object {
        private const val TAG = "TradeService"
    }

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var txtSignal: TextView
    private lateinit var txtConfidence: TextView
    private lateinit var txtMetrics: TextView

    private lateinit var priceCapture: PriceDataCapture
    private lateinit var probabilityEngine: ImprovedProbabilityEngine
    private lateinit var database: TradeHistoryDatabase
    private lateinit var metrics: PerformanceMetrics
    private lateinit var tradeExecutor: TradeExecutionManager

    private var isRunning = false
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        initializeComponents()
        setupFloatingWidget()
        startTradingLoop()
        Log.d(TAG, "🚀 Trade Service Started")
    }

    private fun initializeComponents() {
        priceCapture = PriceDataCapture(this)
        probabilityEngine = ImprovedProbabilityEngine()
        database = TradeHistoryDatabase.getInstance(this)
        metrics = PerformanceMetrics(database)
        tradeExecutor = TradeExecutionManager(this)
    }

    private fun setupFloatingWidget() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null)
        
        txtSignal = floatingView.findViewById(R.id.txtUpPercent) ?: TextView(this)
        txtConfidence = floatingView.findViewById(R.id.txtDownPercent) ?: TextView(this)
        txtMetrics = floatingView.findViewById(R.id.txtCandlePercent) ?: TextView(this)

        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            250, 300,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 20
        params.y = 100

        windowManager.addView(floatingView, params)
    }

    private fun startTradingLoop() {
        isRunning = true
        Thread {
            while (isRunning) {
                try {
                    // সিমুলেশন মোড - লাইভে এটি OCR থেকে আসবে
                    val simulatedPrices = generateSimulatedPrices()
                    val currentPrice = simulatedPrices.lastOrNull() ?: 100.0

                    // বাজার বিশ্লেষণ
                    val (signal, confidence, details) = probabilityEngine.analyzeMarket(
                        simulatedPrices,
                        currentPrice
                    )

                    // UI আপডেট
                    updateFloatingWidget(signal, confidence, details)

                    // ৮০% কনফিডেন্স থাকলে ট্রেড এক্সিকিউট করা
                    if (confidence >= 0.80 && signal != "HOLD") {
                        Log.d(TAG, "🎯 EXECUTING TRADE: $signal with $confidence confidence")
                        tradeExecutor.executeAutoTrade(signal, confidence, currentPrice)
                        
                        // ট্রেড মেট্রিক্স রেকর্ড করা
                        serviceScope.launch {
                            metrics.recordTrade(
                                signal = signal,
                                confidence = confidence,
                                entryPrice = currentPrice,
                                exitPrice = currentPrice + if (signal == "UP") 0.5 else -0.5,
                                duration = 60000  // 1 মিনিট
                            )
                        }
                    }

                    Thread.sleep(2000)  // ২ সেকেন্ড ইন্টারভাল
                } catch (e: Exception) {
                    Log.e(TAG, "Error in trading loop: ${e.message}")
                }
            }
        }.start()
    }

    private fun updateFloatingWidget(signal: String, confidence: Double, details: String) {
        floatingView.post {
            txtSignal.text = "Signal: $signal"
            txtConfidence.text = String.format("Confidence: %.0f%%", confidence * 100)
            txtMetrics.text = details
        }
    }

    /**
     * সিমুলেটেড প্রাইস ডেটা জেনারেট করা (ডেমোর জন্য)
     * লাইভে এটি OCR থেকে আসবে
     */
    private fun generateSimulatedPrices(): DoubleArray {
        val base = 100.0
        val prices = mutableListOf<Double>()
        var current = base
        
        repeat(30) {
            val change = (Math.random() - 0.48) * 2  // ৪৮% মানে বেশি আপ ট্রেন্ড
            current += change
            prices.add(current)
        }
        return prices.toDoubleArray()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        if (::floatingView.isInitialized) {
            try {
                windowManager.removeView(floatingView)
            } catch (e: Exception) {
                Log.e(TAG, "Error removing view: ${e.message}")
            }
        }
        Log.d(TAG, "🛑 Trade Service Stopped")
    }
}
