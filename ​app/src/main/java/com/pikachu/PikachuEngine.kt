package com.pikachu

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * পিকাজু সার্ভিস ইঞ্জিন (Pikachu Engine)
 * এই সার্ভিসটি সার্বক্ষনিক সেন্সিং ও কন্ট্রোল করবে: ব্যাকগ্রাউন্ড নিউজ চেক করা
 * আর স্ক্রিন থেকে রিয়েল-টাইম দাম রিড করে ক্লিক করা।
 */
class PikachuEngine : AccessibilityService() {

    companion object {
        private const val TAG = "PikachuEngine"
    }

    // অন্যান্য মডিউলগুলো ডিক্লেয়ার বা কানেক্ট করা
    private val newsFetcher = GlobalNewsFetcher()
    private val marketAnalyzer = GlobalMarketAnalyzer()
    private val logicEngine = TradingLogicEngine()

    private var isGlobalSafeToTrade = true // গ্লোবাল সেফটি ভ্যারিয়েবল

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Pikachu Engine Started Successfully!")

        // অ্যাপ চালু হওয়ার সাথে সাথেই নিউজ থেকে ব্যাকগ্রাউন্ডে গ্লোবাল নিউজ চেক করা শুরু করা
        startBackgroundNewsMonitoring()
    }

    private fun startBackgroundNewsMonitoring() {
        // ব্যাকগ্রাউন্ডে বিভিন্ন নিউজ চেক করার জেসচার সেন্সরটি আপডেট করা
        newsFetcher.fetchAndCheckGlobalNews(object : GlobalNewsFetcher.NewsCheckCallback {
            override fun onResult(isHighImpactNewsFound: Boolean) {
                // গ্লোবালরিয়াদের নিচে চেক করা
                isGlobalSafeToTrade = !marketAnalyzer.isGlobalMarketFavorable(50.0, isHighImpactNewsFound)
                if (!isGlobalSafeToTrade) {
                    Log.d(TAG, "Pikachu Alert: Market is unsafe due to global news!")
                }
            }
        })
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // ১. যদি গ্লোবাল মার্কেটটি নিরাপদ না থাকে, তবে খসড়া অপারেশন বন্ধ থাকবে
        if (!isGlobalSafeToTrade) {
            return
        }

        val rootNode = rootInActiveWindow ?: return

        // ৩. স্ক্রিন থেকে চার্ট বা লাইভ এন্ট্রি বাজা
        val currentPrice = extractPriceFromScreen(rootNode)
        if (currentPrice <= 0.0) return

        // ৫. টেকনিক্যাল লজিক ইঞ্জিনকে বাজারে জাজমেন্ট দেওয়া বাজা
        val decision = logicEngine.evaluateMarketTrend(currentPrice)

        // ৬. সিদ্ধান্ত অনুযায়ী স্কিন গেসচার বা ক্লিক একিউট করা
        if (decision == "UP") {
            executeClick(540f, 1500f) // আপ কিনুন
        } else if (decision == "DOWN") {
            executeClick(540f, 1700f) // ডাউন কিনুন
        }
    }

    private fun extractPriceFromScreen(nodeInfo: AccessibilityNodeInfo?): Double {
        if (nodeInfo == null) return 0.0
        try {
            for (i in 0 until nodeInfo.childCount) {
                val child = nodeInfo.getChild(i)
                if (child != null && child.text != null) {
                    val text = child.text.toString().replace("[^0-9.]".toRegex(), "")
                    if (text.isNotEmpty() && text.length > 2) {
                        return text.toDouble()
                    }
                }
                val valResult = extractPriceFromScreen(child)
                if (valResult > 0.0) return valResult
            }
        } catch (e: Exception) {
            Log.e(TAG, "Extraction error: ${e.message}")
        }
        return 0.0
    }

    private fun executeClick(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
        }
        val builder = GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(path, 0, 100))
        }
        dispatchGesture(builder.build(), null, null)
        Log.d(TAG, "Pikachu Executed Action at X: $x Y: $y")
    }

    override fun onInterrupt() {
        Log.d(TAG, "Pikachu Engine Interrupted.")
    }
}
