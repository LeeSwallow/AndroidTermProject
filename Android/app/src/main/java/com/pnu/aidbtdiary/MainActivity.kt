package com.pnu.aidbtdiary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pnu.aidbtdiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        binding.btnTodayEntry.setOnClickListener {
            startActivity(Intent(this, EntryActivity::class.java))
        }

        binding.btnViewDiary.setOnClickListener {
            startActivity(Intent(this, DiaryListActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, ThemeActivity::class.java))
        }
    }
}