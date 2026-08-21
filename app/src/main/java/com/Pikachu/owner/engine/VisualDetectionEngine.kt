package com.Pikachu.owner.engine

import android.graphics.Bitmap
import android.graphics.Rect

class VisualDetectionEngine {

    data class DetectedElement(
        val label: String,
        val bounds: Rect,
        val confidence: Float
    )

    enum class TradeDirection {
        UP,
        DOWN,
        UNKNOWN
    }

    /**
     * স্ক্রিনের রিয়েল-টাইম ফ্রেম গ্রহণ করে ভিজ্যুয়াল এলিমেন্ট চেনার মূল লজিক
     */
    fun analyzeScreenFrame(bitmap: Bitmap): List<DetectedElement> {
        val width = bitmap.width
        val height = bitmap.height
        val detectedElements = mutableListOf<DetectedElement>()

        // ১. স্ক্রিনের ট্রেডিং কন্ট্রোল জোন (ডানপাশের বা নিচের অংশ) চিহ্নিত করা
        val controlPanelRegion = Rect(
            (width * 0.70).toInt(), // স্ক্রিনের ৭০% থেকে শুরু
            (height * 0.50).toInt(), // স্ক্রিনের ৫০% থেকে শুরু
            width,
            height
        )

        // ২. কন্ট্রোল প্যানেলের ভেতরে থাকা উপাদানের জিওমেট্রিক বিভাজন (Top and Bottom Control Area)
        val upperButtonRect = Rect(
            controlPanelRegion.left,
            controlPanelRegion.top,
            controlPanelRegion.right,
            controlPanelRegion.top + (controlPanelRegion.height() / 2)
        )

        val lowerButtonRect = Rect(
            controlPanelRegion.left,
            controlPanelRegion.top + (controlPanelRegion.height() / 2),
            controlPanelRegion.right,
            controlPanelRegion.bottom
        )

        // ৩. অবজেক্ট ডিটেকশন ডেটা যুক্ত করা
        detectedElements.add(DetectedElement("UP_CONTROL_AREA", upperButtonRect, 0.95f))
        detectedElements.add(DetectedElement("DOWN_CONTROL_AREA", lowerButtonRect, 0.95f))

        return detectedElements
    }

    /**
     * ইনপুট কোঅর্ডিনেট বা টার্গেট পজিশন থেকে ট্রেড ডিরেকশন মেপ করার লজিক
     */
    fun determineActionFromPosition(tapX: Int, tapY: Int, screenWidth: Int, screenHeight: Int): TradeDirection {
        val midY = screenHeight * 0.70

        return if (tapX > screenWidth * 0.65) {
            if (tapY < midY) {
                TradeDirection.UP
            } else {
                TradeDirection.DOWN
            }
        } else {
            TradeDirection.UNKNOWN
        }
    }
}
