package com.pikachu.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pikachu.R

class ActiveUsersActivity : AppCompatActivity() {

    private lateinit var listViewActiveUsers: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_users)

        listViewActiveUsers = findViewById(R.id.listViewActiveUsers)
        sharedPreferences = getSharedPreferences("PikachuOwnerPrefs", Context.MODE_PRIVATE)

        loadActiveUsers()

        // লিস্টের কোনো ইউজারে ক্লিক করলে বা রিমুভ করতে চাইলে অপশন আসবে
        listViewActiveUsers.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = userList[position]
            
            AlertDialog.Builder(this)
                .setTitle("পারমিশন বাতিল")
                .setMessage("আপনি কি '$selectedUser' এর অ্যাক্সেস বাতিল করতে চান?")
                .setPositiveButton("হ্যাঁ") { _, _ ->
                    removeActiveUser(selectedUser)
                }
                .setNegativeButton("না", null)
                .show()
        }
    }

    private fun loadActiveUsers() {
        // সেভ করা অ্যাক্টিভ ইউজারদের ডেটা লোড করা (কমা সেপারেটেড স্ট্রিং থেকে)
        val savedUsersString = sharedPreferences.getString("ACTIVE_USERS_LIST", "Rafu_User1, Tanvir_User2") ?: ""
        userList = if (savedUsersString.isNotEmpty()) {
            ArrayList(savedUsersString.split(","))
        } else {
            arrayListOf()
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        listViewActiveUsers.adapter = adapter
    }

    private fun removeActiveUser(user: String) {
        userList.remove(user)
        
        // আপডেট লিস্ট আবার লোকাল স্টোরেজে সেভ করা
        val updatedString = userList.joinToString(",")
        sharedPreferences.edit().putString("ACTIVE_USERS_LIST", updatedString).apply()
        
        // বাতিলকৃত ইউজারের লিস্টেও এটি পাঠিয়ে দেওয়ার ব্যবস্থা করা যায়
        val revokedString = sharedPreferences.getString("REVOKED_USERS_LIST", "") ?: ""
        val newRevokedString = if (revokedString.isEmpty()) user else "$revokedString,$user"
        sharedPreferences.edit().putString("REVOKED_USERS_LIST", newRevokedString).apply()

        adapter.notifyDataSetChanged()
        Toast.makeText(this, "$user এর পারমিশন সফলভাবে বাতিল করা হয়েছে", Toast.LENGTH_SHORT).show()
    }
}
