package com.pikachu.owner

import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R
import com.pikachu.owner.service.OwnerCoreService

class OwnerHomeActivity : AppCompatActivity() {

    private lateinit var masterSwitch: Switch
    private lateinit var btnLiveVoice: ImageButton
    private lateinit var btnSettings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // যদি এক্সএমএল ডিজাইন ছাড়াই করেন বা নিজস্ব লেআউট ব্যবহার করেন
        setContentView(R.layout.activity_owner_home)

        masterSwitch = findViewById(R.id.masterSwitch)
        btnLiveVoice = findViewById(R.id.btnLiveVoice)
        btnSettings = findViewById(R.id.btnSettings)

        // ১. মাঝের মাস্টার সুইচ লজিক (স্ক্রিন মনিটরিং, ক্যামেরা ও অটো ট্রেডিং কন্ট্রোল)
        masterSwitch.setOnCheckedChangeListener { _, isChecked ->
            val serviceIntent = Intent(this, OwnerCoreService::class.java).apply {
                putExtra("MASTER_SWITCH", isChecked)
            }
            if (isChecked) {
                startForegroundService(serviceIntent)
                Toast.makeText(this, "পিকাজু মাস্টার সুইচ চালু হয়েছে", Toast.LENGTH_SHORT).show()
            } else {
                stopService(serviceIntent)
                Toast.makeText(this, "পিকাজু মাস্টার সুইচ বন্ধ করা হয়েছে", Toast.LENGTH_SHORT).show()
            }
        }

        // ২. ওপরের ডান পাশের লাইভ ভয়েস অপশন বাটন
        btnLiveVoice.setOnClickListener {
            Toast.makeText(this, "লাইভ ভয়েস মোড সক্রিয় করা হয়েছে। 'পিকাজু' বলে ডাকুন।", Toast.LENGTH_SHORT).show()
            // লাইভ ভয়েস ট্রিগার করার কোড বা লজিক এখানে যুক্ত হবে
        }

        // ৩. ওপরের বাম পাশের সেটিংস অপশন (স্ক্রোলবল মেনুবারের জন্য)
        btnSettings.setOnClickListener {
            // সেটিংস মেনুবার ওপেন করার লজিক
            Toast.makeText(this, "সেটিংস মেনু ওপেন হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }
}
