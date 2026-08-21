package com.Pikachu.owner.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class PikachuCoreService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: PikachuCoreService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // রিয়েল-টাইম স্ক্রিন ইভেন্ট প্রসেস করার জন্য রাখা হয়েছে
    }

    override fun onInterrupt() {
        instance = null
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    /**
     * স্ক্রিনের নির্দিষ্ট (x, y) স্থানাঙ্কে মিলি-সেকেন্ডের মধ্যে অটো-ট্যাপ ট্রিগার করার অরিজিনাল মেথড
     */
    fun performClickAt(x: Float, y: Float, durationMs: Long = 50L, onComplete: ((Boolean) -> Unit)? = null) {
        val clickPath = Path().apply {
            moveTo(x, y)
        }

        val stroke = GestureDescription.StrokeDescription(clickPath, 0, durationMs)
        val gestureBuilder = GestureDescription.Builder().addStroke(stroke)

        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                onComplete?.invoke(true)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                onComplete?.invoke(false)
            }
        }, null)
    }
}
