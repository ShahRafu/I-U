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

class PermanentDeleteActivity : AppCompatActivity() {

    private lateinit var listViewAllUsers: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var allUserList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permanent_delete)

        listViewAllUsers = findViewById(R.id.listViewAllUsers)
        sharedPreferences = getSharedPreferences("PikachuOwnerPrefs", Context.MODE_PRIVATE)

        loadAllUsersForDeletion()

        listViewAllUsers.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = allUserList[position]

            AlertDialog.Builder(this)
                .setTitle("পার্মানেন্ট ডিলিট")
                .setMessage("সতর্কতা: '$selectedUser' কে পাকাপাকিভাবে মুছে ফেলতে চান? এটি আর ফিরিয়ে আনা যাবে না।")
                .setPositiveButton("ডিলিট করুন") { _, _ ->
                    permanentDeleteUser(selectedUser)
                }
                .setNegativeButton("বাতিল", null)
                .show()
        }
    }

    private fun loadAllUsersForDeletion() {
        val activeString = sharedPreferences.getString("ACTIVE_USERS_LIST", "Rafu_User1, Tanvir_User2") ?: ""
        allUserList = if (activeString.isNotEmpty()) {
            ArrayList(activeString.split(","))
        } else {
            arrayListOf()
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, allUserList)
        listViewAllUsers.adapter = adapter
    }

    private fun permanentDeleteUser(user: String) {
        allUserList.remove(user)
        
        val updatedString = allUserList.joinToString(",")
        sharedPreferences.edit().putString("ACTIVE_USERS_LIST", updatedString).apply()

        adapter.notifyDataSetChanged()
        Toast.makeText(this, "$user কে পাকাপাকিভাবে ডিলিট করা হয়েছে!", Toast.LENGTH_SHORT).show()
    }
}
