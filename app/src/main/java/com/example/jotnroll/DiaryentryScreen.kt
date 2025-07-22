package com.example.jotnroll

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DiaryentryScreen : AppCompatActivity() {

    private lateinit var btnSubmit: Button
    private lateinit var btnBack: Button
    private lateinit var nameInput: EditText
    private lateinit var titleInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var contentInput: EditText

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diaryentry_screen)

        btnSubmit = findViewById(R.id.buttonSubmit)
        btnBack = findViewById(R.id.buttonBack)
        nameInput = findViewById(R.id.editTextName)
        titleInput = findViewById(R.id.editTextTitle)
        dateInput = findViewById(R.id.editTextDate)
        contentInput = findViewById(R.id.editTextDiary)

        btnSubmit.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val title = titleInput.text.toString().trim()
            val date = dateInput.text.toString().trim()
            val content = contentInput.text.toString().trim()

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isNotEmpty() && title.isNotEmpty() && content.isNotEmpty() && date.isNotEmpty()) {
                val entry = hashMapOf(
                    "userId"   to currentUser.uid,
                    "userName" to name,
                    "email"    to currentUser.email,
                    "title"    to title,
                    "date"     to date,
                    "content"  to content,
                    "timestamp" to FieldValue.serverTimestamp()
                )


                db.collection("entries")
                    .add(entry)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Diary entry saved!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ItemDiaryEntry::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error saving entry: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

            btnBack.setOnClickListener {
            startActivity(Intent(this, UserdashboardScreen::class.java))
            finish()
        }
    }
}
