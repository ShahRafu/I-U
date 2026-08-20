package com.pikachu.userapp.homescreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class UserHomeActivity : AppCompatActivity() {

    private var isVoiceActive = false
    private var isServiceRunning = false
    private val handler = Handler(Looper.getMainLooper())
    
    // ১০ সেকেন্ডের চেক লজিক
    private val checkChartRunnable = Runnable {
        // এখানে চেক করবে ট্রেডিং চার্ট ওপেন আছে কি না
        // যদি না থাকে, এআই বলবে: "আমি তো কোন ট্রেডিং চার্ট দেখতাছিনা, ট্রেড করার জন্য চ্যাট ওপেন কর।"
        Toast.makeText(this, "AI: আমি তো কোন ট্রেডিং চ্যাট দেখতাছিনা, ট্রেড করার জন্য চ্যাট ওপেন কর!", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        val btnVoice = findViewById<Button>(R.id.btnVoice)
        val btnToggleService = findViewById<Button>(R.id.btnToggleService)

        // সেটিংস মেনু (পরে কাজ হবে)
        btnSettings.setOnClickListener {
            Toast.makeText(this, "সেটিংস মেনু ওপেন হচ্ছে...", Toast.LENGTH_SHORT).show()
        }

        // লাইভ ভয়েস বাটন
        btnVoice.setOnClickListener {
            isVoiceActive = !isVoiceActive
            val status = if (isVoiceActive) "লাইভ ভয়েস চালু হয়েছে" else "লাইভ ভয়েস বন্ধ হয়েছে"
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        }

        // ব্যাকগ্রাউন্ড সার্ভিস অন/অফ
        btnToggleService.setOnClickListener {
            isServiceRunning = !isServiceRunning
            val msg = if (isServiceRunning) "সার্ভিস চালু হয়েছে" else "সার্ভিস বন্ধ হয়েছে"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // অ্যাপ থেকে বের হওয়ার পর ১০ সেকেন্ডের টাইমআউট শুরু হবে
    override fun onStop() {
        super.onStop()
        if (isServiceRunning) {
            handler.postDelayed(checkChartRunnable, 10000) // ১০ সেকেন্ড
        }
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(checkChartRunnable) // অ্যাপের ভেতরে থাকলে টাইমার অফ
    }
}
