package com.example.jotnroll

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jotnroll.databinding.ActivityDiaryentryScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DiaryentryScreen : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryentryScreenBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryentryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSubmit.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val title = binding.editTextTitle.text.toString().trim()
            val date = binding.editTextDate.text.toString().trim()
            val content = binding.editTextDiary.text.toString().trim()
            val currentUser = auth.currentUser

            if (currentUser == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isNotEmpty() && title.isNotEmpty() && content.isNotEmpty() && date.isNotEmpty()) {
                val entry = hashMapOf(
                    "userId" to currentUser.uid,
                    "userName" to name,
                    "email" to currentUser.email,
                    "title" to title,
                    "date" to date,
                    "content" to content,
                    "timestamp" to FieldValue.serverTimestamp()
                )

                db.collection("entries")
                    .add(entry)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Diary entry saved!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ViewBacktoEntry::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error saving entry: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonBack.setOnClickListener {
            startActivity(Intent(this, UserdashboardScreen::class.java))
            finish()
        }
    }
}