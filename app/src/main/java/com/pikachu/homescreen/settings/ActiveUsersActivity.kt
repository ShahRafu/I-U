package com.pikachu.homescreen.settings

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class ActiveUsersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_users)

        // এখানে অ্যাক্টিভ ইউজারের তালিকা লোড হবে এবং নামের পাশে ক্রস (❌) আইকনে চাপ দিলে 
        // ইউজার রিভোক বা বাতিল হয়ে ২ নম্বর ফাইলে চলে যাওয়ার লজিক এখানে বসবে।
        
        Toast.makeText(this, "১ নম্বর ফাইল: অ্যাক্টিভ ইউজার সেকশন লোড হয়েছে", Toast.LENGTH_SHORT).show()
    }
}
