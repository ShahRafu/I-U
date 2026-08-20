package com.pikachu

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * এই ফাইলটি নিজেই ব্যাকগ্রাউন্ডে ইন্টারনেট থেকে আন্তর্জাতিক নিউজ 
 * এবং ফিন্যান্সিয়াল আপডেট ফেচ বা সংগ্রহ করে আনবে।
 */
class GlobalNewsFetcher {

    companion object {
        private const val TAG = "GlobalNewsFetcher"
        private const val NEWS_API_URL = "https://api.gdeltproject.org/api/v2/doc/doc?query=market%20news&mode=artlist&format=json"
    }

    interface NewsCheckCallback {
        fun onResult(isHighImpactNewsFound: Boolean)
    }

    // নিজে নিজে ইন্টারনেট থেকে ডেটা এনে চেক করার মূল মেথড
    fun fetchAndCheckGlobalNews(callback: NewsCheckCallback) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val url = URL(NEWS_API_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while ({ line = reader.readLine(); line }() != null) {
                        response.append(line)
                    }
                    reader.close()

                    // প্রাপ্ত ডেটা অ্যানালাইসিস করে দেখা খবরের ভেতরে কোনো প্যানিক বা হাই-ইম্প্যাক্ট নিউজ আছে কি না
                    val hasRisk = analyzeNewsContent(response.toString())
                    callback.onResult(hasRisk)
                } else {
                    Log.d(TAG, "Failed to fetch news. Response code: $responseCode")
                    callback.onResult(false) // সার্ভার কানেক্ট না হলে সেফ ধরে চলবে
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching global news: ${e.message}")
                callback.onResult(false)
            }
        }
    }

    // খবরের শিরোনাম বা টেক্সট স্ক্যান করে ঝুঁকিপূর্ণ রাজনৈতিক বা মার্কেট নিউজ ফিল্টার করা
    private fun analyzeNewsContent(jsonResponse: String): Boolean {
        try {
            val lowerData = jsonResponse.lowercase()

            // যদি আন্তর্জাতিক বাজারে বড় কোনো প্যানিক, ক্র্যাশ বা যুদ্ধ/রাজনৈতিক অস্থিরতার শব্দ থাকে
            if (lowerData.contains("war") || lowerData.contains("crash") || 
                lowerData.contains("emergency") || lowerData.contains("sanctions")) {
                Log.d(TAG, "High-Impact Risk News Detected from Internet!")
                return true // ট্রেড ব্লক করার জন্য ট্রু রিটার্ন করবে
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing news content: ${e.message}")
        }
        return false // কোনো বড় ঝুঁকি না থাকলে নিরাপদ
    }
}
