package com.pikachu

import android.app.Service
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.util.DisplayMetrics

/**
 * পিকাজু স্ক্রিন ক্যাপচার সার্ভিস
 * এটি MediaProjection API ব্যবহার করে রিয়েল-টাইম স্ক্রিন ক্যাপচার করবে।
 */
class ScreenCaptureService : Service() {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaProjectionManager: MediaProjectionManager? = null

    override fun onCreate() {
        super.onCreate()
        // সিস্টেম সার্ভিস থেকে MediaProjectionManager ইনিশিয়ালাইজ করা
        mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    /**
     * ক্যাপচার শুরু করার জন্য মূল মেথড
     * @param resultCode অডিও-ভিজ্যুয়াল পারমিশন রেজাল্ট কোড
     * @param data পারমিশন ইনটেন্ট ডেটা
     */
    fun startCapture(resultCode: Int, data: Intent) {
        mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)
        
        // ভার্চুয়াল ডিসপ্লে তৈরি করা (পিকাজুর প্রসেসিংয়ের জন্য)
        val metrics = resources.displayMetrics
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "PikachuDisplay",
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            null, null, null
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // সার্ভিস বন্ধ হওয়ার সময় রিসোর্স রিলিজ করা
        virtualDisplay?.release()
        mediaProjection?.stop()
    }
}
