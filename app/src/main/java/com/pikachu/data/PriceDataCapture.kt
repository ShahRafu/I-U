package com.pikachu.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.math.abs

/**
 * PriceDataCapture - রিয়েল-টাইম প্রাইস ডেটা ক্যাপচার করার জন্য
 * OCR ব্যবহার করে স্ক্রিন থেকে মূল্য তথ্য বের করে
 */
class PriceDataCapture(private val context: Context) {

    companion object {
        private const val TAG = "PriceDataCapture"
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val priceHistory = mutableListOf<Double>()
    private var lastCapturedPrice = 0.0

    /**
     * স্ক্রিন থেকে মূল্য ডেটা এক্সট্র্যাক্ট করে
     */
    fun captureAndExtractPrice(bitmap: Bitmap, callback: (price: Double, history: DoubleArray) -> Unit) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val extractedPrice = parsePrice(visionText.text)
                    if (extractedPrice > 0) {
                        lastCapturedPrice = extractedPrice
                        updatePriceHistory(extractedPrice)
                        Log.d(TAG, "Price Captured: ৳$extractedPrice | History Size: ${priceHistory.size}")
                        callback(extractedPrice, priceHistory.toDoubleArray())
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "OCR Failed: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Capture Error: ${e.message}")
        }
    }

    /**
     * টেক্সট থেকে প্রাইস প্যারস করা
     */
    private fun parsePrice(text: String): Double {
        return try {
            // সাধারণ সংখ্যা প্যাটার্ন খুঁজে বের করা
            val regex = Regex("\\d+\\.?\\d*")
            val matches = regex.findAll(text)
            
            // সবচেয়ে বড় সংখ্যা = সম্ভবত মূল্য
            matches.map { it.value.toDoubleOrNull() ?: 0.0 }
                .filter { it > 0 }
                .maxOrNull() ?: 0.0
        } catch (e: Exception) {
            Log.e(TAG, "Parse Error: ${e.message}")
            0.0
        }
    }

    /**
     * প্রাইস হিস্ট্রি আপডেট করা (সর্বোচ্চ ৫০টি ডেটা পয়েন্ট রাখা)
     */
    private fun updatePriceHistory(price: Double) {
        // ডুপ্লিকেট প্রাইস এড়ানো
        if (abs(price - lastCapturedPrice) > 0.001) {
            priceHistory.add(price)
            if (priceHistory.size > 50) {
                priceHistory.removeAt(0)
            }
        }
    }

    /**
     * বর্তমান প্রাইস ইতিহাস পাওয়া
     */
    fun getPriceHistory(): DoubleArray {
        return if (priceHistory.size >= 10) priceHistory.toDoubleArray() else doubleArrayOf()
    }

    /**
     * সর্বশেষ মূল্য পাওয়া
     */
    fun getLastPrice(): Double = lastCapturedPrice
}
