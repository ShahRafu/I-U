package com.pikachu

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * এই সার্ভিসটি সরাসরি ফোনের স্ক্রিন থেকে লাইভ টেক্সট বা বাটন রিড করে
 * এবং বিশেষ জেসচার বা পাথ-এর মাধ্যমে ক্লিক বা প্রেস করবে।
 */
class TradingAutomationService : AccessibilityService() {

    companion object {
        private const val TAG = "TradingAutomationService"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return

        // নোড অ্যানালাইসিস শুরু করে ট্রেডিং লজিক প্রসেস করা হচ্ছে
        analyzeAndExecute(rootNode)
    }

    private fun analyzeAndExecute(nodeInfo: AccessibilityNodeInfo?) {
        if (nodeInfo == null) return

        for (i in 0 until nodeInfo.childCount) {
            val child = nodeInfo.getChild(i)
            if (child != null) {
                val text = child.text
                if (text != null) {
                    val content = text.toString().lowercase()

                    // আন্তর্জাতিক বাণিজ্যের সিগন্যাল বা বাটন ম্যাচিং
                    if (content.contains("call") || content.contains("put") || content.contains("up") || content.contains("down")) {
                        // শর্ত মিলে গেলে এখান থেকে অটো-ক্লিক ট্রিগার হবে
                        Log.d(TAG, "Target trading keyword matched: $content")
                        performClickAt(540f, 1600f) // স্ক্রিনের নির্দিষ্ট স্থানাঙ্কে ক্লিক
                        break
                    }
                }
                analyzeAndExecute(child)
            }
        }
    }

    // স্ক্রিনের নির্দিষ্ট পজিশনে টপ বা ক্লিক করার জেসচার মেথড
    private fun performClickAt(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
        }
        val builder = GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(path, 0, 100))
        }
        dispatchGesture(builder.build(), null, null)
        Log.d(TAG, "Automation Click Executed at X: $x, Y: $y")
    }

    override fun onInterrupt() {
        Log.d(TAG, "Trading Automation Service Interrupted.")
    }
}
