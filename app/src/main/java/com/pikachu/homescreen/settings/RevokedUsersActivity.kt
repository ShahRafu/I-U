package com.pikachu.home.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class RevokedUsersActivity : AppCompatActivity() {

    private lateinit var listViewRevokedUsers: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var revokedUserList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revoked_users)

        listViewRevokedUsers = findViewById(R.id.listViewRevokedUsers)
        sharedPreferences = getSharedPreferences("PikachuOwnerPrefs", Context.MODE_PRIVATE)

        loadRevokedUsers()

        // বাতিলকৃত ইউজারের লিস্ট থেকে কাউকে সিলেক্ট করলে পুনরায় পারমিশন দেওয়ার অপশন আসবে
        listViewRevokedUsers.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = revokedUserList[position]

            AlertDialog.Builder(this)
                .setTitle("পারমিশন পুনর্বহাল")
                .setMessage("আপনি কি '$selectedUser' এর অ্যাক্সেস পুনরায় চালু করতে চান?")
                .setPositiveButton("হ্যাঁ") { _, _ ->
                    restoreUser(selectedUser)
                }
                .setNegativeButton("না", null)
                .show()
        }
    }

    private fun loadRevokedUsers() {
        // লোকাল স্টোরেজ থেকে বাতিলকৃত ইউজারদের ডেটা লোড করা
        val savedRevokedString = sharedPreferences.getString("REVOKED_USERS_LIST", "") ?: ""
        revokedUserList = if (savedRevokedString.isNotEmpty()) {
            ArrayList(savedRevokedString.split(","))
        } else {
            arrayListOf()
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, revokedUserList)
        listViewRevokedUsers.adapter = adapter
    }

    private fun restoreUser(user: String) {
        revokedUserList.remove(user)

        // বাতিলকৃত ইউজারের লিস্ট আপডেট করে লোকাল স্টোরেজে সেভ করা
        val updatedRevokedString = revokedUserList.joinToString(",")
        sharedPreferences.edit().putString("REVOKED_USERS_LIST", updatedRevokedString).apply()

        // ইউজারকে আবার অ্যাক্টিভ ইউজারের লিস্টে যুক্ত করা
        val activeString = sharedPreferences.getString("ACTIVE_USERS_LIST", "") ?: ""
        val newActiveString = if (activeString.isEmpty()) user else "$activeString,$user"
        sharedPreferences.edit().putString("ACTIVE_USERS_LIST", newActiveString).apply()

        adapter.notifyDataSetChanged()
        Toast.makeText(this, "$user এর পারমিশন সফলভাবে পুনর্বহাল করা হয়েছে!", Toast.LENGTH_SHORT).show()
    }
}
