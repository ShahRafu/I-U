package com.pikachu.homescreen.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class CustomNicknameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_nickname)

        // এখানে ইউজারদের ডিভাইসের আসল নামের পরিবর্তে নিজের পছন্দমতো 
        // বড় বা সুন্দর নাম (Nickname) দিয়ে সেভ করে রাখার লজিক বসবে।
        // একবার এখানে নাম সেভ করলে ওনার অ্যাপের বাকি সব অপশন বা মেনুতে ওই কাস্টম নামটিই শো করবে।
        
        Toast.makeText(this, "৮ নম্বর ফাইল: কাস্টম নাম বা নিকনেম সেটআপ সেকশন লোড হয়েছে", Toast.LENGTH_SHORT).show()
    }
}
