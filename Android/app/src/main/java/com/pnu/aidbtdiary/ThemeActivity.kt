package com.pnu.aidbtdiary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pnu.aidbtdiary.databinding.ActivityThemeBinding
import com.pnu.aidbtdiary.helper.NotificationUtil
import com.pnu.aidbtdiary.helper.SyncHelper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ThemeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThemeBinding
    private lateinit var syncHelper: SyncHelper
    private val PICK_IMAGE_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotificationUtil.createChannel(applicationContext)
        syncHelper = SyncHelper(applicationContext)
        binding.btnDefaultTheme.setOnClickListener {
            // 기본 테마로 변경
        }

        binding.btnGalleryTheme.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


        binding.btnSync.setOnClickListener {
            GlobalScope.launch {
                syncHelper.syncDiaries()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            // 선택된 이미지를 배경으로 설정
        }
    }
}