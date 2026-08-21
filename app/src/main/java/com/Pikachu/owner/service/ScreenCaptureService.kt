package com.Pikachu.owner.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import java.nio.ByteBuffer

class ScreenCaptureService : Service() {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        const val CHANNEL_ID = "PikachuCaptureChannel"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_RESULT_CODE = "RESULT_CODE"
        const val EXTRA_RESULT_DATA = "RESULT_DATA"
        const val EXTRA_WIDTH = "SCREEN_WIDTH"
        const val EXTRA_HEIGHT = "SCREEN_HEIGHT"
        const val EXTRA_DENSITY = "SCREEN_DENSITY"

        var latestFrame: Bitmap? = null
            private set
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1)
        val resultData: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_RESULT_DATA)
        }

        val width = intent.getIntExtra(EXTRA_WIDTH, 1080)
        val height = intent.getIntExtra(EXTRA_HEIGHT, 1920)
        val density = intent.getIntExtra(EXTRA_DENSITY, 320)

        if (resultCode != -1 || resultData == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val mpManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mpManager.getMediaProjection(resultCode, resultData)

        setupVirtualDisplay(width, height, density)

        return START_STICKY
    }

    @SuppressLint("WrongConstant")
    private fun setupVirtualDisplay(width: Int, height: Int, density: Int) {
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "PikachuScreenCapture",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            var image: Image? = null
            try {
                image = reader.acquireLatestImage()
                if (image != null) {
                    val planes = image.planes
                    val buffer: ByteBuffer = planes[0].buffer
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * width

                    val bitmap = Bitmap.createBitmap(
                        width + rowPadding / pixelStride,
                        height,
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap.copyPixelsFromBuffer(buffer)
                    
                    // অতিরিক্ত প্যাডিং ক্রপ করে সঠিক রেজোলিউশন ফ্রেম নেওয়া
                    val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
                    
                    latestFrame = croppedBitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                image?.close()
            }
        }, handler)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pikachu Screen Capture Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("পিকাজু ভিশন সক্রিয়")
            .setContentText("স্ক্রিন প্রসেসিং ব্যাকগ্রাউন্ডে চলছে...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
        latestFrame = null
    }
}
