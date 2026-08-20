package com.pikachu.home.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class TimerControlActivity : AppCompatActivity() {

    private lateinit var etExpiryDays: EditText
    private lateinit var btnSaveTimer: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_control)

        etExpiryDays = findViewById(R.id.etExpiryDays)
        btnSaveTimer = findViewById(R.id.btnSaveTimer)
        sharedPreferences = getSharedPreferences("PikachuOwnerPrefs", Context.MODE_PRIVATE)

        val currentDays = sharedPreferences.getString("USER_EXPIRY_DAYS", "30")
        etExpiryDays.setText(currentDays)

        btnSaveTimer.setOnClickListener {
            val days = etExpiryDays.text.toString().trim()
            if (days.isNotEmpty()) {
                sharedPreferences.edit().putString("USER_EXPIRY_DAYS", days).apply()
                Toast.makeText(this, "টাইমার বা মেয়াদ সফলভাবে আপডেট করা হয়েছে!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "মেয়াদ খালি রাখা যাবে না", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
