package com.kingassistant;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * পিকাজু মাস্টার ইঞ্জিন (Pikachu Engine)
 * এটি সবকটি ফাইলকে সেন্ট্রালি কন্ট্রোল করবে। অ্যাপ রান হওয়ার পর 
 * আর পেছনের কোনো ফাইলে হাত দিতে হবে না।
 */
public class PikachuEngine extends AccessibilityService {

    private static final String TAG = "PikachuEngine";

    // অন্যান্য মডিউলগুলোর ইনস্ট্যান্স বা অবজেক্ট
    private final GlobalNewsFetcher newsFetcher = new GlobalNewsFetcher();
    private final GlobalMarketAnalyzer marketAnalyzer = new GlobalMarketAnalyzer();
    private final TradingLogicEngine logicEngine = new TradingLogicEngine();

    private boolean isGlobalSafeToTrade = true; // গ্লোবাল সেফটি স্ট্যাটাস

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.text(TAG, "Pikachu Engine Started Successfully!");
        
        // অ্যাপ চালু হওয়ার সাথেই নিজে থেকে ব্যাকগ্রাউন্ডে গ্লোবাল নিউজ চেক করা শুরু করবে
        startBackgroundNewsMonitoring();
    }

    private void startBackgroundNewsMonitoring() {
        // রিয়েল-টাইম নিউজ ফেচ করে গ্লোবাল সেফটি আপডেট করা
        newsFetcher.fetchAndCheckGlobalNews(new GlobalNewsFetcher.NewsCheckCallback() {
            @Override
            public void onResult(boolean isHighImpactNewsFound) {
                // অ্যানালাইজার দিয়ে চেক করা
                isGlobalSafeToTrade = marketAnalyzer.isGlobalMarketFavorable(50.0, isHighImpactNewsFound);
                if (!isGlobalSafeToTrade) {
                    Log.d(TAG, "Pikachu Alert: Market is unsafe due to global news/volatility!");
                }
            }
        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // ১. যদি গ্লোবাল মার্কেট নিরাপদ না থাকে, তবে কাজ এখানেই বন্ধ থাকবে
        if (!isGlobalSafeToTrade) {
            return; 
        }

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        // ২. স্ক্রিন থেকে প্রাইস বা ডেটা এক্সট্রাক্ট করা
        double currentPrice = extractPriceFromScreen(rootNode);
        if (currentPrice <= 0.0) return;

        // ৩. টেকনিক্যাল লজিক ইঞ্জিনের মাধ্যমে ট্রেন্ড জাজ করা
        String decision = logicEngine.evaluateMarketTrend(currentPrice);

        // ৪. সিদ্ধান্ত অনুযায়ী স্ক্রিনে অটো-ক্লিক এক্সিকিউট করা
        if (decision.equals("UP")) {
            executeClick(540f, 1500f); // আপ বাটন
        } else if (decision.equals("DOWN")) {
            executeClick(540f, 1700f); // ডাউন বাটন
        }
    }

    private double extractPriceFromScreen(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return 0.0;
        try {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                if (child != null && child.getText() != null) {
                    String text = child.getText().toString().replaceAll("[^0-9.]", "");
                    if (!text.isEmpty() && text.length() > 2) {
                        return Double.parseDouble(text);
                    }
                }
                double val = extractPriceFromScreen(child);
                if (val > 0.0) return val;
            }
        } catch (Exception e) {
            Log.e(TAG, "Extraction error: " + e.getMessage());
        }
        return 0.0;
    }

    private void executeClick(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 100));
        dispatchGesture(builder.build(), null, null);
        Log.d(TAG, "Pikachu Executed Action at X:" + x + " Y:" + y);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Pikachu Engine Interrupted.");
    }
}
