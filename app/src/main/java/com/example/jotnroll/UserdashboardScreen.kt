package com.example.jotnroll

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jotnroll.databinding.ActivityUserdashboardScreenBinding

class UserdashboardScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserdashboardScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserdashboardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }

        binding.btnCreateEntry.setOnClickListener {
            startActivity(Intent(this, DiaryentryScreen::class.java))
        }

        binding.btnAboutUs.setOnClickListener {
            startActivity(Intent(this, AboutusScreen::class.java))
        }

        binding.btnViewEntries.setOnClickListener {
            startActivity(Intent(this, ItemDiaryEntry::class.java))
        }
    }
}
