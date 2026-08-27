package com.pikachu.userapp.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R
import com.pikachu.userapp.homescreen.UserHomeActivity

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etUserName: EditText
    private lateinit var etInitialPin: EditText
    private lateinit var etVerificationCode: EditText
    private lateinit var btnSubmit: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("PikachuUserPrefs", Context.MODE_PRIVATE)
        val isVerified = sharedPreferences.getBoolean("IS_VERIFIED", false)

        if (isVerified) {
            startActivity(Intent(this, UserHomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_registration)

        etUserName = findViewById(R.id.etUserName)
        etInitialPin = findViewById(R.id.etInitialPin)
        etVerificationCode = findViewById(R.id.etVerificationCode)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val name = etUserName.text.toString().trim()
            val initialPin = etInitialPin.text.toString().trim()
            val verificationCode = etVerificationCode.text.toString().trim()

            if (name.isEmpty()) {
                etUserName.error = "নাম দিতে হবে"
                etUserName.requestFocus()
                return@setOnClickListener
            }

            if (initialPin.length != 6) {
                etInitialPin.error = "পিন অবশ্যই ৬ অক্ষরের হতে হবে"
                etInitialPin.requestFocus()
                return@setOnClickListener
            }

            if (!isValidUniqueCode(verificationCode)) {
                Toast.makeText(
                    this,
                    "কোড ভুল! সঠিক ফরম্যাট দিন (৮ অক্ষর)।",
                    Toast.LENGTH_LONG
                ).show()
                etVerificationCode.error = "সঠিক কোড দিন"
                etVerificationCode.requestFocus()
                return@setOnClickListener
            }

            val editor = sharedPreferences.edit()
            editor.putBoolean("IS_VERIFIED", true)
            editor.putString("USER_NAME", name)
            editor.apply()

            Toast.makeText(this, "ভেরিফিকেশন সফল হয়েছে!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isValidUniqueCode(code: String): Boolean {
        if (code.length != 8) return false

        var letterCount = 0
        var digitCount = 0
        var specialCount = 0
        var upperCount = 0
        var lowerCount = 0

        for (char in code) {
            when {
                char.isUpperCase() -> {
                    letterCount++
                    upperCount++
                }
                char.isLowerCase() -> {
                    letterCount++
                    lowerCount++
                }
                char.isDigit() -> {
                    digitCount++
                }
                !char.isLetterOrDigit() -> {
                    specialCount++
                }
            }
        }

        val isLetterValid = letterCount == 3 && upperCount > 0 && lowerCount > 0
        val isDigitValid = digitCount == 3
        val isSpecialValid = specialCount == 2

        return isLetterValid && isDigitValid && isSpecialValid
    }
}
