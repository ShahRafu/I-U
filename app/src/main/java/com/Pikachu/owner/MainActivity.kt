package com.Pikachu.owner

import android.accessibilityservice.AccessibilityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.Pikachu.owner.service.PikachuCoreService

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private var isServiceEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        startButton = Button(this).apply {
            text = "Start Pikachu Core Engine"
            setOnClickListener {
                checkAndRequestAccessibilityPermission()
            }
        }
        setContentView(startButton)
        
        // অ্যাপ খোলার সময় সার্ভিস চেক করা
        checkAccessibilityService()
    }

    override fun onResume() {
        super.onResume()
        // প্রতিবার রিজিউম হলে চেক করা (ইউজার সেটিংস থেকে ফিরলে)
        checkAccessibilityService()
    }

    /**
     * Accessibility Service চেক করার মূল ফাংশন
     */
    private fun checkAccessibilityService() {
        isServiceEnabled = isAccessibilityServiceEnabled(
            this,
            PikachuCoreService::class.java
        )

        if (isServiceEnabled) {
            // সেবা চালু আছে - সরাসরি ওনার হোম স্ক্রিনে যান
            startButton.text = "✅ Pikachu Core Engine Active"
            startButton.isEnabled = false
            
            // ২ সেকেন্ড পর অটোমেটিক ট্রানজিশন
            startButton.postDelayed({
                navigateToOwnerHome()
            }, 2000)
        } else {
            // সেবা চালু নেই - ইউজারকে নির্দেশনা দিন
            startButton.text = "Start Pikachu Core Engine"
            startButton.isEnabled = true
        }
    }

    /**
     * Accessibility Service enabled আছে কিনা চেক করা
     */
    private fun isAccessibilityServiceEnabled(
        context: Context,
        serviceClass: Class<*>
    ): Boolean {
        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        val componentName = ComponentName(context, serviceClass)
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityManager.FEEDBACK_ALL_MASKS
        )

        return enabledServices.any { service ->
            service.componentName == componentName
        }
    }

    /**
     * বাটন ক্লিক করলে এই ফাংশন চলে
     */
    private fun checkAndRequestAccessibilityPermission() {
        if (isServiceEnabled) {
            // সেবা আগেই চালু আছে - সরাসরি যান
            navigateToOwnerHome()
            return
        }

        // সেবা চালু নেই - সেটিংস খুলুন
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)

        // একটি বার্তা দিন
        Toast.makeText(
            this,
            "⚙️ 'Pikachu' খুঁজুন এবং সক্ষম করুন\n(Settings > Accessibility > Installed apps > Pikachu)",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * ওনার হোম অ্যাক্টিভিটিতে নেভিগেট করা
     */
    private fun navigateToOwnerHome() {
        try {
            val homeIntent = Intent(this, OwnerHomeActivity::class.java)
            startActivity(homeIntent)
            finish() // MainActivity বন্ধ করা
        } catch (e: Exception) {
            Toast.makeText(this, "Error: OwnerHomeActivity not found", Toast.LENGTH_SHORT).show()
        }
    }
}
