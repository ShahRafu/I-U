package com.pikachu

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
import com.pikachu.core.BinaryTradingMaster

/**
 * OverlayDisplayService
 * - Shows a small floating widget with UP/DOWN percentages and candle percent
 * - Runs a lightweight loop to update values every 1-5 seconds (configurable via intent extra)
 * - DOES NOT perform auto-execution; it only notifies and displays information for manual trading
 */
class OverlayDisplayService : Service() {

    companion object {
        private const val TAG = "OverlayDisplayService"
        const val EXTRA_UPDATE_MS = "update_ms"
    }

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var txtUpPercent: TextView
    private lateinit var txtDownPercent: TextView
    private lateinit var txtCandlePercent: TextView
    private val tradingMaster = BinaryTradingMaster()
    private var running = false

    override fun onCreate() {
        super.onCreate()
        setupFloatingWindow()
    }

    private fun setupFloatingWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null)
        txtUpPercent = floatingView.findViewById(R.id.txtUpPercent)
        txtDownPercent = floatingView.findViewById(R.id.txtDownPercent)
        txtCandlePercent = floatingView.findViewById(R.id.txtCandlePercent)

        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 100

        windowManager.addView(floatingView, params)
    }

    private fun startUpdateLoop(updateMs: Long) {
        running = true
        Thread {
            while (running) {
                try {
                    // TODO: Replace with real price history source (OCR or provided API)
                    val samplePrices = doubleArrayOf(100.5, 101.0, 101.2, 102.0, 101.8, 102.5, 103.0, 103.5, 104.0, 104.5)
                    val currentLivePrice = 105.2

                    val (upProb, downProb, candlePercent) = tradingMaster.estimateProbabilityForCurrentCandle(samplePrices, currentLivePrice)

                    floatingView.post {
                        txtUpPercent.text = String.format("UP: %.0f%%", upProb * 100)
                        txtDownPercent.text = String.format("DOWN: %.0f%%", downProb * 100)
                        txtCandlePercent.text = String.format("Candle: %.0f%%", candlePercent * 100)
                    }

                    // Do NOT auto-execute trades — manual only.
                    Log.d(TAG, "Signal -> UP: ${upProb*100}%, DOWN: ${downProb*100}% | Candle: ${candlePercent*100}%")

                    Thread.sleep(updateMs)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in overlay loop: ${e.message}")
                }
            }
        }.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val updateMs = intent?.getLongExtra(EXTRA_UPDATE_MS, 3000L) ?: 3000L
        // Clamp between 1s and 5s
        val clamped = updateMs.coerceIn(1000L, 5000L)
        startUpdateLoop(clamped)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        running = false
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}
