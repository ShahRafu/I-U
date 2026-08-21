package com.Pikachu.owner.engine

import com.Pikachu.owner.service.PikachuCoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TradeExecutionEngine(
    private val textReaderEngine: TextReaderEngine,
    private val visualDetectionEngine: VisualDetectionEngine
) {

    private var isEngineRunning = false
    private val executionScope = CoroutineScope(Dispatchers.Default)

    /**
     * ইঞ্জিনের এক্সিকিউশন স্টেট চালু করার ফাংশন
     */
    fun startExecutionLoop() {
        isEngineRunning = true
    }

    /**
     * ইঞ্জিনের এক্সিকিউশন স্টেট বন্ধ করার ফাংশন
     */
    fun stopExecutionLoop() {
        isEngineRunning = false
    }

    /**
     * ওসিআর এবং টাইমার লজিকের ওপর ভিত্তি করে অটো-ট্যাপ এক্সিকিউট করার মূল মেথড
     */
    fun processAndExecute(
        detectedTexts: List<TextReaderEngine.RecognizedTextData>,
        targetX: Float,
        targetY: Float
    ) {
        if (!isEngineRunning) return

        val timerValue = textReaderEngine.extractTimerValue(detectedTexts)

        // টাইমার যদি নির্দিষ্ট এন্ট্রি সেকেন্ডে পৌঁছায় (e.g., "00:01" বা "00:00")
        if (timerValue == "00:01" || timerValue == "00:00") {
            triggerInstantTap(targetX, targetY)
        }
    }

    /**
     * এক্সেসিবিলিটি সার্ভিস ট্রিগার করে এক্স্যাক্ট মিলি-সেকেন্ডে অটো-ট্যাপ করার মেথড
     */
    private fun triggerInstantTap(x: Float, y: Float) {
        PikachuCoreService.instance?.let { service ->
            executionScope.launch {
                // মিলি-সেকেন্ড ডেলিভ্যারির জন্য ইনস্ট্যান্ট ট্যাপ
                service.performClickAt(x, y, durationMs = 30L) { isSuccess ->
                    if (isSuccess) {
                        // ট্যাপ সফল হলে পরবর্তী সেকেন্ডের জন্য সাময়িক পজ
                        pauseExecutionTemporarily()
                    }
                }
            }
        }
    }

    private fun pauseExecutionTemporarily() {
        executionScope.launch {
            isEngineRunning = false
            delay(2000L) // ২ সেকেন্ড কুলডাউন যাতে একটি ক্যান্ডেলে একাধিক ট্যাপ না পড়ে
            isEngineRunning = true
        }
    }
}
