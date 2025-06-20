// BaseActivity.kt
package com.pnu.aidbtdiary

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

open class BaseActivity : AppCompatActivity() {
    private val PREF_NAME = "theme_pref"
    private val KEY_COLOR = "theme_color"
    private val KEY_URI   = "theme_image_uri"

    override fun onResume() {
        super.onResume()
        // rootLayout이 있으면 테마 적용
        findViewById<View>(R.id.rootLayout)?.let { applyTheme(it) }
    }

    protected fun applyTheme(root: View) {
        val prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val uriStr = prefs.getString(KEY_URI, null)
        if (!uriStr.isNullOrEmpty()) {
            val uri = Uri.parse(uriStr)
            contentResolver.openInputStream(uri)?.use { input ->
                val bmp = BitmapFactory.decodeStream(input)
                root.background = BitmapDrawable(resources, bmp)
            }
        } else {
            val color = prefs.getInt(
                KEY_COLOR,
                ContextCompat.getColor(this, R.color.entryCardBackground)
            )
            root.setBackgroundColor(color)
        }
    }
}
