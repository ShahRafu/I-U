package com.kingassistant;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * এই ফাইলটি নিজেই ব্যাকগ্রাউন্ডে ইন্টারনেট থেকে আন্তর্জাতিক নিউজ 
 * এবং ফিন্যান্সিয়াল আপডেট ফেচ বা সংগ্রহ করে আনবে।
 */
public class GlobalNewsFetcher {

    private static final String TAG = "GlobalNewsFetcher";
    
    // ফিন্যান্সিয়াল বা অর্থনৈতিক খবরের ফ্রি বা পাবলিক এপিআই এন্ডপয়েন্ট (উদাহরণস্বরূপ)
    private static final String NEWS_API_URL = "https://api.gdeltproject.org/api/v2/doc/doc?query=market%20news&mode=artlist&format=json";

    public interface NewsCheckCallback {
        void onResult(boolean isHighImpactNewsFound);
    }

    // নিজে নিজে ইন্টারনেট থেকে ডেটা এনে চেক করার মূল মেথড
    public void fetchAndCheckGlobalNews(NewsCheckCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL(NEWS_API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // প্রাপ্ত ডেটা অ্যানালাইসিস করে দেখা খবরের ভেতরে কোনো প্যানিক বা হাই-ইম্প্যাক্ট নিউজ আছে কি না
                    boolean hasRisk = analyzeNewsContent(response.toString());
                    callback.onResult(hasRisk);
                } else {
                    Log.d(TAG, "Failed to fetch news. Response code: " + responseCode);
                    callback.onResult(false); // সার্ভার কানেক্ট না হলে সেফ ধরে চলবে
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching global news: " + e.getMessage());
                callback.onResult(false);
            }
        });
    }

    // খবরের শিরোনাম বা টেক্সট স্ক্যান করে ঝুঁকিপূর্ণ রাজনৈতিক বা মার্কেট নিউজ ফিল্টার করা
    private boolean analyzeNewsContent(String jsonResponse) {
        try {
            String lowerData = jsonResponse.toLowerCase();
            
            // যদি আন্তর্জাতিক বাজারে বড় কোনো প্যানিক, ক্র্যাশ বা যুদ্ধ/রাজনৈতিক অস্থিরতার শব্দ থাকে
            if (lowerData.contains("war") || lowerData.contains("crash") || 
                lowerData.contains("emergency") || lowerData.contains("sanctions")) {
                Log.d(TAG, "High-Impact Risk News Detected from Internet!");
                return true; // ট্রেড ব্লক করার জন্য ট্রু রিটার্ন করবে
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing news content: " + e.getMessage());
        }
        return false; // কোনো বড় ঝুঁকি না থাকলে নিরাপদ
    }
}
