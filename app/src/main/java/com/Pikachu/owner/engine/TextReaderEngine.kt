package com.Pikachu.owner.engine

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextReaderEngine {

    // Google ML Kit-এর হাই-স্পিড টেক্সট রিকগনিজার
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    data class RecognizedTextData(
        val text: String,
        val boundingBox: Rect?
    )

    /**
     * পুরো স্ক্রিন বা নির্দিষ্ট ফ্রেম থেকে সমস্ত টেক্সট এবং তাদের পজিশন রিড করার ফাংশন
     */
    fun processImage(
        bitmap: Bitmap,
        onSuccess: (List<RecognizedTextData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val resultList = mutableListOf<RecognizedTextData>()
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        resultList.add(
                            RecognizedTextData(
                                text = line.text,
                                boundingBox = line.boundingBox
                            )
                        )
                    }
                }
                onSuccess(resultList)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * স্ক্রিনের ফিল্টার করা টেক্সট থেকে টাইমার (e.g. 00:05, 01:30) শনাক্ত করার ফাংশন
     */
    fun extractTimerValue(detectedTexts: List<RecognizedTextData>): String? {
        val timerRegex = Regex("""\b\d{2}:\d{2}\b""") // MM:SS ফরম্যাট ফিল্টার
        for (item in detectedTexts) {
            val match = timerRegex.find(item.text)
            if (match != null) {
                return match.value
            }
        }
        return null
    }

    /**
     * ইঞ্জিনের রিসোর্স ক্লিন-আপ
     */
    fun close() {
        recognizer.close()
    }
}
