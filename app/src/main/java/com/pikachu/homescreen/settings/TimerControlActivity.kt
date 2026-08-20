package com.pikachu.homescreen.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class TimerControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_control)

        // এখানে অনুমোদিত ইউজারদের জন্য নির্দিষ্ট মেয়াদ বা টাইমার সেট করার লজিক বসবে।
        // নির্ধারিত সময় পার হয়ে গেলে সিস্টেম অটোমেটিক তাদের পারমিশন বাতিল করে 
        // ২ নম্বর সেকশনে (বাতিলকৃত ইউজার) পাঠিয়ে দেবে।
        
        Toast.makeText(this, "৭ নম্বর ফাইল: টাইমার ও এক্সপায়ারি কন্ট্রোল সেকশন লোড হয়েছে", Toast.LENGTH_SHORT).show()
    }
}
