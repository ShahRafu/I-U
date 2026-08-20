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
        
        // SharedPreferences দিয়ে চেক করা ইউজার ইতিমধ্যে ভেরিফাই করা কি না
        sharedPreferences = getSharedPreferences("PikachuUserPrefs", Context.MODE_PRIVATE)
        val isVerified = sharedPreferences.getBoolean("IS_VERIFIED", false)

        // যদি ইতিমধ্যে ভেরিফাই করা থাকে, তবে সরাসরি হোমস্ক্রিনে পাঠিয়ে দিবে
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

            // ১. নাম চেক
            if (name.isEmpty()) {
                etUserName.error = "নাম দিতে হবে"
                etUserName.requestFocus()
                return@setOnClickListener
            }

            // ২. ৬ অক্ষরের পিন চেক
            if (initialPin.length != 6) {
                etInitialPin.error = "পিন অবশ্যই ৬ অক্ষরের হতে হবে"
                etInitialPin.requestFocus()
                return@setOnClickListener
            }

            // ৩. ৮ অক্ষরের ইউনিক কোড ফরম্যাট ও রুল ভ্যালিডেশন
            if (!isValidUniqueCode(verificationCode)) {
                Toast.makeText(
                    this, 
                    "কোড ভুল! ৮ অক্ষরে ৩টি অক্ষর (বড় ও ছোট হাতের মিশ্রিত), ৩টি সংখ্যা এবং ২টি স্পেশাল ক্যারেক্টার থাকতে হবে।", 
                    Toast.MAX_LENGTH
                ).show()
                etVerificationCode.error = "সঠিক কোড দিন"
                etVerificationCode.requestFocus()
                return@setOnClickListener
            }

            // সব ঠিক থাকলে ভেরিফিকেশন সফল হিসেবে সেভ করে হোমস্ক্রিনে প্রবেশ করবে
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

    // নিখুঁত ৮ অক্ষরের পাসওয়ার্ড রুল চেকার (৩ অক্ষর মিক্সড, ৩ সংখ্যা, ২ স্পেশাল)
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

        // শর্ত যাচাই: ৩টি অক্ষর (কমপক্ষে ১টি বড় ও ১টি ছোট), ৩টি সংখ্যা, ২টি স্পেশাল ক্যারেক্টার
        val isLetterValid = letterCount == 3 && upperCount > 0 && lowerCount > 0
        val isDigitValid = digitCount == 3
        val isSpecialValid = specialCount == 2

        return isLetterValid && isDigitValid && isSpecialValid
    }
}
