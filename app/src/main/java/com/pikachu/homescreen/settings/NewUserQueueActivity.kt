package com.pikachu.homescreen.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class NewUserQueueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user_queue)

        // এখানে নতুন ইউজার যখন ইউজার অ্যাপ নামিয়ে প্রথমবার ওপেন করবে, 
        // তখন তাদের জেনারেট হওয়া কোড এবং মোবাইল/ডিভাইস ইনফো এই কিউতে জমা হবে 
        // এবং ওনার সেখান থেকে তা কন্ট্রোল করতে পারবেন।
        
        Toast.makeText(this, "৩ নম্বর ফাইল: নতুন ইউজার কোড ও ডিভাইস কিউ সেকশন লোড হয়েছে", Toast.LENGTH_SHORT).show()
    }
}
