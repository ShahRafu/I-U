package com.pikachu.home.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class OwnerAppConfigActivity : AppCompatActivity() {

    private lateinit var etOwnerName: EditText
    private lateinit var btnSaveOwnerConfig: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_app_config)

        etOwnerName = findViewById(R.id.etOwnerName)
        btnSaveOwnerConfig = findViewById(R.id.btnSaveOwnerConfig)
        sharedPreferences = getSharedPreferences("PikachuOwnerPrefs", Context.MODE_PRIVATE)

        // পূর্বে সেভ করা ওনার কনফিগ লোড করা
        val currentOwnerTitle = sharedPreferences.getString("OWNER_APP_TITLE", "King Assistant Owner")
        etOwnerName.setText(currentOwnerTitle)

        btnSaveOwnerConfig.setOnClickListener {
            val newOwnerTitle = etOwnerName.text.toString().trim()
            if (newOwnerTitle.isNotEmpty()) {
                sharedPreferences.edit().putString("OWNER_APP_TITLE", newOwnerTitle).apply()
                Toast.makeText(this, "ওনার অ্যাপ কনফিগারেশন সফলভাবে সেভ হয়েছে!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "নাম খালি রাখা যাবে না", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
