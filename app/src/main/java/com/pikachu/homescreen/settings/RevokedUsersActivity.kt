package com.pikachu.homescreen.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class RevokedUsersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revoked_users)

        // এখানে বাতিলকৃত বা রিভোক করা ইউজারের তালিকা লোড হবে।
        // প্রতিটি ইউজারের নামের পাশে টিক (✔️) আইকন থাকবে।
        // টিক চিহ্নে চাপ দিলে ওই ইউজারের পারমিশন পুনরায় চালু হয়ে ১ নম্বর ফাইলে ফিরে যাবে।
        
        Toast.makeText(this, "২ নম্বর ফাইল: বাতিলকৃত ইউজার সেকশন লোড হয়েছে", Toast.LENGTH_SHORT).show()
    }
}
