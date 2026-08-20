package com.pikachu.home.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class CustomNicknameActivity : AppCompatActivity() {

    private lateinit var etNickname: EditText
    private lateinit var btnSaveNickname: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_nickname)

        etNickname = findViewById(R.id.etNickname)
        btnSaveNickname = findViewById(R.id.btnSaveNickname)
        sharedPreferences = getSharedPreferences("PikachuOwnerPrefs", Context.MODE_PRIVATE)

        // পূর্বে সেভ করা নিকনেম লোড করা
        val currentNickname = sharedPreferences.getString("USER_CUSTOM_NICKNAME", "MyDevice")
        etNickname.setText(currentNickname)

        btnSaveNickname.setOnClickListener {
            val nickname = etNickname.text.toString().trim()
            if (nickname.isNotEmpty()) {
                sharedPreferences.edit().putString("USER_CUSTOM_NICKNAME", nickname).apply()
                Toast.makeText(this, "কাস্টম নিকনেম সফলভাবে সেভ হয়েছে!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "নিকনেম খালি রাখা যাবে না", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
