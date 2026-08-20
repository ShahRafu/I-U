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

class PendingRequestsActivity : AppCompatActivity() {

    private lateinit var listViewPendingRequests: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var pendingList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_requests)

        listViewPendingRequests = findViewById(R.id.listViewPendingRequests)
        sharedPreferences = getSharedPreferences("PikachuOwnerPrefs", Context.MODE_PRIVATE)

        loadPendingRequests()

        // নতুন রিকোয়েস্ট লিস্টে ক্লিক করলে অনুমোদন দেওয়ার অপশন আসবে
        listViewPendingRequests.setOnItemClickListener { _, _, position, _ ->
            val selectedRequest = pendingList[position]

            AlertDialog.Builder(this)
                .setTitle("ইউজার অনুমোদন")
                .setMessage("আপনি কি '$selectedRequest' কে অ্যাপ ব্যবহার করার অনুমতি দিতে চান?")
                .setPositiveButton("অনুমোদন দিন") { _, _ ->
                    approveUser(selectedRequest)
                }
                .setNegativeButton("বাতিল", null)
                .show()
        }
    }

    private fun loadPendingRequests() {
        // ডেমো বা লোকাল স্টোরেজ থেকে পেন্ডিং রিকোয়েস্ট লোড করা
        val savedPendingString = sharedPreferences.getString("PENDING_REQUESTS_LIST", "Device_XYZ_User3, Device_ABC_User4") ?: ""
        pendingList = if (savedPendingString.isNotEmpty()) {
            ArrayList(savedPendingString.split(","))
        } else {
            arrayListOf()
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, pendingList)
        listViewPendingRequests.adapter = adapter
    }

    private fun approveUser(user: String) {
        pendingList.remove(user)

        // পেন্ডিং লিস্ট আপডেট করা
        val updatedPendingString = pendingList.joinToString(",")
        sharedPreferences.edit().putString("PENDING_REQUESTS_LIST", updatedPendingString).apply()

        // ইউজারকে সরাসরি অ্যাক্টিভ ইউজারের তালিকায় যুক্ত করা
        val activeString = sharedPreferences.getString("ACTIVE_USERS_LIST", "") ?: ""
        val newActiveString = if (activeString.isEmpty()) user else "$activeString,$user"
        sharedPreferences.edit().putString("ACTIVE_USERS_LIST", newActiveString).apply()

        adapter.notifyDataSetChanged()
        Toast.NMakeText(this, "$user সফলভাবে অনুমোদিত হয়েছে!", Toast.LENGTH_SHORT).show()
    }
}
