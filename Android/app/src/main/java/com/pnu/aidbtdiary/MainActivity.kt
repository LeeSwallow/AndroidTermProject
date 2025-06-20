package com.pnu.aidbtdiary

import android.content.Intent
import android.os.Bundle
import java.time.LocalDate
import androidx.appcompat.app.AppCompatActivity
import com.pnu.aidbtdiary.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDateHeader.text =  LocalDate.now().toString()

        binding.cardEntry.setOnClickListener {
            startActivity(Intent(this, EntryActivity::class.java))
        }

        binding.cardHistory.setOnClickListener {
            startActivity(Intent(this, DiaryListActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, ThemeActivity::class.java))
        }
    }
}
