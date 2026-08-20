package com.pikachu.home.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class UserAppLogoActivity : AppCompatActivity() {

    private lateinit var etLogoName: EditText
    private lateinit var btnSaveLogo: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_app_logo)

        etLogoName = findViewById(R.id.etLogoName)
        btnSaveLogo = findViewById(R.id.btnSaveLogo)
        sharedPreferences = getSharedPreferences("PikachuOwnerPrefs", Context.MODE_PRIVATE)

        // পূর্বে সেভ করা লোগো নাম বা স্টাইল লোড করা
        val currentLogo = sharedPreferences.getString("USER_APP_LOGO_NAME", "DefaultLogo")
        etLogoName.setText(currentLogo)

        btnSaveLogo.setOnClickListener {
            val newLogoName = etLogoName.text.toString().trim()
            if (newLogoName.isNotEmpty()) {
                sharedPreferences.edit().putString("USER_APP_LOGO_NAME", newLogoName).apply()
                Toast.makeText(this, "ইউজার অ্যাপের লোগো সফলভাবে আপডেট করা হয়েছে!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "লোগোর নাম খালি রাখা যাবে না", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
