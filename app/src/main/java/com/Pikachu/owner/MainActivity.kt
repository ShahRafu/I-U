package com.Pikachu.owner

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // সিম্পল ইউজার ইন্টারফেস লেআউট
        val startButton = Button(this).apply {
            text = "Start Pikachu Core Engine"
            setOnClickListener {
                checkAndRequestAccessibilityPermission()
            }
        }
        setContentView(startButton)
    }

    private fun checkAndRequestAccessibilityPermission() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
        Toast.makeText(this, "Enable Pikachu Core Service from Settings", Toast.LENGTH_LONG).show()
    }
}
