package com.pikachu.userapp.auth

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // রিয়েল ভিউ ইনিশিয়ালাইজেশন
        etUserName = findViewById(R.id.etUserName)
        etInitialPin = findViewById(R.id.etInitialPin)
        etVerificationCode = findViewById(R.id.etVerificationCode)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val name = etUserName.text.toString().trim()
            val initialPin = etInitialPin.text.toString().trim()
            val verificationCode = etVerificationCode.text.toString().trim()

            // বেসিক ফিল্ড চেক
            if (name.isEmpty() || initialPin.isEmpty()) {
                Toast.makeText(this, "দয়া করে নাম এবং ৬ অক্ষরের পিন দিন", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (initialPin.length != 6) {
                Toast.makeText(this, "পিন অবশ্যই ৬ অক্ষরের হতে হবে", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ৮ অক্ষরের ইউনিক কোড ভেরিফিকেশন ও রুল চেক
            if (isValidUniqueCode(verificationCode)) {
                Toast.makeText(this, "ভেরিফিকেশন সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                
                // সফল হলে সরাসরি হোমস্ক্রিনে চলে যাবে
                val intent = Intent(this, UserHomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "কোডটি সঠিক নয়! (৩টি অক্ষর, ৩টি সংখ্যা এবং ২টি স্পেশাল ক্যারেক্টার আবশ্যক)", Toast.LENGTH_LONG).show()
            }
        }
    }

    // রিয়েল লজিক: ৮ অক্ষরের পাসওয়ার্ডে সঠিক ক্যাটাগরি আছে কিনা তা চেক করার ফাংশন
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
                char.isDigit() -> digitCount++
                // স্পেশাল ক্যারেক্টার চেক (যেকোনো সিম্বল)
                !char.isLetterOrDigit() -> specialCount++
            }
        }

        // শর্ত: মোট ৩টি অক্ষর (মিক্সড কেস: বড় ও ছোট মিলিয়ে), ৩টি সংখ্যা এবং ২টি স্পেশাল ক্যারেক্টার থাকতে হবে
        val isLetterValid = letterCount == 3 && upperCount > 0 && lowerCount > 0
        val isDigitValid = digitCount == 3
        val isSpecialValid = specialCount == 2

        return isLetterValid && isDigitValid && isSpecialValid
    }
}
