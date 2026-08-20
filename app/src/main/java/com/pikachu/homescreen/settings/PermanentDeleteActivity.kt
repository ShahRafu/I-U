package com.pikachu.homescreen.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class PermanentDeleteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permanent_delete)

        // এখানে অনুমোদিত বা বাতিলকৃত ইউজারদের তালিকা থেকে পাকাপাকিভাবে রেকর্ড ডিলিট করার লজিক বসবে।
        // এখান থেকে ডিলিট করলে পরেরবার ওই ইউজারকে অ্যাপে ঢুকতে হলে নতুন কোড দিয়ে আবার জয়েন করতে হবে।
        
        Toast.makeText(this, "৬ নম্বর ফাইল: পাকাপাকি ডিলিট (Permanent Delete) সেকশন লোড হয়েছে", Toast.LENGTH_SHORT).show()
    }
}
