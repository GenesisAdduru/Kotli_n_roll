package com.example.jotnroll

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jotnroll.databinding.ActivityItemDiaryEntryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ItemDiaryEntry : AppCompatActivity() {

    private lateinit var binding: ActivityItemDiaryEntryBinding
    private lateinit var diaryAdapter: DiaryAdapter
    private val diaryList = mutableListOf<DiaryEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDiaryEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        diaryAdapter = DiaryAdapter(this, diaryList)
        binding.recyclerViewDiary.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDiary.adapter = diaryAdapter

        // Fetch user entries from Firebase
        fetchUserEntries()

        // Handle Back button click
        binding.btnBack.setOnClickListener {
            finish() // Closes the current activity
        }
    }

    private fun fetchUserEntries() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("entries")
            .whereEqualTo("userId", uid)                      // 🔍 Filter by current user
            .orderBy("timestamp", Query.Direction.DESCENDING) // 🕓 Sort by latest first
            .get()
            .addOnSuccessListener { result ->
                diaryList.clear()

                for (doc in result) {
                    Log.d("FIREBASE_ENTRY", doc.data.toString()) // ✅ Debugging: see what is fetched

                    val entry = DiaryEntry(
                        id       = doc.id,
                        userName = doc.getString("userName") ?: "",
                        title    = doc.getString("title")    ?: "",
                        date     = doc.getString("date")     ?: "",
                        content  = doc.getString("content")  ?: "",
                        email    = doc.getString("email")    ?: ""
                    )
                    diaryList.add(entry)
                }

                diaryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_ERROR", "Failed to fetch entries: ${e.message}")
            }
    }

}
