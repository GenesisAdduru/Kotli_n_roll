package com.example.jotnroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jotnroll.databinding.ActivityViewEntryBinding

class ViewEntry : AppCompatActivity() {

    private lateinit var binding: ActivityViewEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = intent.getStringExtra("userName") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val content = intent.getStringExtra("content") ?: ""

        binding.textViewViewName.text = userName
        binding.textViewViewTitle.text = title
        binding.textViewViewDate.text = date
        binding.textViewViewContent.text = content

        binding.buttonBackView.setOnClickListener {
            finish()
        }
    }
}
