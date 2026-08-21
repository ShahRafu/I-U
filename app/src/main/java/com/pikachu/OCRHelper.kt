package com.pikachu

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * OCRHelper – a thin wrapper around ML Kit on-device text recognition.
 * Note: This is a simple helper; production code should handle threading, lifecycle, and camera/image sources.
 */
class OCRHelper(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processBitmap(bitmap: Bitmap, onResult: (result: String?) -> Unit) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText: Text ->
                    onResult(visionText.text)
                }
                .addOnFailureListener { e ->
                    Log.e("OCRHelper", "OCR failed: ${e.message}")
                    onResult(null)
                }
        } catch (e: Exception) {
            Log.e("OCRHelper", "processBitmap error: ${e.message}")
            onResult(null)
        }
    }
}
