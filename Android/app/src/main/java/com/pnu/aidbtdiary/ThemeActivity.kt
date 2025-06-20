package com.pnu.aidbtdiary

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.pnu.aidbtdiary.BaseActivity
import com.pnu.aidbtdiary.databinding.ActivityThemeBinding
import com.pnu.aidbtdiary.helper.SyncHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ThemeActivity : BaseActivity() {
    private lateinit var binding: ActivityThemeBinding
    private val PREF_NAME = "theme_pref"
    private val KEY_COLOR = "theme_color"
    private val KEY_URI   = "theme_image_uri"
    private val PICK_IMAGE_REQUEST = 100
    private lateinit var syncHelper: SyncHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // onResume()에서 자동으로 applyTheme 호출되므로, onCreate에서는 초기화 후 별도 호출 불필요

        // SyncHelper 초기화
        syncHelper = SyncHelper(this)

        // 뒤로 가기
        binding.btnBack.setOnClickListener { finish() }

        // 컬러 테마 버튼
        binding.btnThemeWhite.setOnClickListener {
            saveColorTheme(ContextCompat.getColor(this, R.color.white))
        }
        binding.btnThemePink.setOnClickListener {
            saveColorTheme(ContextCompat.getColor(this, R.color.themePink))
        }
        binding.btnThemeBlue.setOnClickListener {
            saveColorTheme(ContextCompat.getColor(this, R.color.themeBlue))
        }
        binding.btnThemeYellow.setOnClickListener {
            saveColorTheme(ContextCompat.getColor(this, R.color.themeYellow))
        }

        // 커스텀 이미지 테마 선택
        binding.btnGallerySwatch.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // 클라우드 동기화
        binding.btnSyncCloud.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                syncHelper.syncDiaries()
            }
        }
    }

    private fun saveColorTheme(colorInt: Int) {
        val prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        prefs.edit().apply {
            putInt(KEY_COLOR, colorInt)
            remove(KEY_URI)
            apply()
        }
        // 테마 변경 후 즉시 반영: onResume() 호출 없이도 적용
        findViewById<View>(R.id.rootLayout)?.let { applyTheme(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                // 퍼미션 영구 저장
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                // SharedPreferences에 저장
                getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().apply {
                    putString(KEY_URI, uri.toString())
                    remove(KEY_COLOR)
                    apply()
                }
                // 동적 스와치 추가
                addCustomSwatch(uri)
                // 즉시 테마 적용
                findViewById<View>(R.id.rootLayout)?.let { applyTheme(it) }
            }
        }
    }

    private fun addCustomSwatch(uri: Uri) {
        val size = (40 * resources.displayMetrics.density).toInt()
        val params = LinearLayout.LayoutParams(size, size).apply {
            marginEnd = (8 * resources.displayMetrics.density).toInt()
        }
        val btn = ImageButton(this).apply {
            layoutParams = params
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            setImageURI(uri)
            setOnClickListener {
                getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().apply {
                    putString(KEY_URI, uri.toString())
                    remove(KEY_COLOR)
                    apply()
                }
                findViewById<View>(R.id.rootLayout)?.let { applyTheme(it) }
            }
        }
        binding.llThemeSwatches.addView(btn)
    }
}
