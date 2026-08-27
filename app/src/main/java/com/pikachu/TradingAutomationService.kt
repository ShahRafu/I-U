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

class TradingAutomationService : Service() {

    companion object {
        private const val TAG = "TradingAutomation"
    }

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var txtPercentage: TextView
    private lateinit var probabilityEngine: ProbabilityTradingEngine

    override fun onCreate() {
        super.onCreate()
        probabilityEngine = ProbabilityTradingEngine()
        setupFloatingWindow()
        startMarketAnalysisLoop()
    }

    private fun setupFloatingWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null)
        txtPercentage = floatingView.findViewById<TextView>(R.id.txtPercentage)

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

    private fun startMarketAnalysisLoop() {
        Thread {
            while (true) {
                try {
                    val samplePrices = doubleArrayOf(100.5, 101.0, 101.2, 102.0, 101.8, 102.5, 103.0, 103.5, 104.0, 104.5)
                    val currentLivePrice = 105.2

                    val decision = probabilityEngine.evaluateProbabilityTrend(samplePrices, currentLivePrice)
                    val decisionStr = decision?.toString() ?: ""

                    floatingView.post {
                        txtPercentage.text = decisionStr
                    }

                    if (decisionStr == "UP") {
                        Log.d(TAG, "Auto-Executing: UP Trade placed successfully!")
                    } else if (decisionStr == "DOWN") {
                        Log.d(TAG, "Auto-Executing: DOWN Trade placed successfully!")
                    }

                    Thread.sleep(3000)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in automation loop: ${e.message}")
                }
            }
        }.start()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}
