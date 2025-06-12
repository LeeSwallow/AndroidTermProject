package com.pnu.aidbtdiary.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlin.collections.listOf

class EmotionAdapter(
    private val context: Context,
) : BaseAdapter() {
    private val emotions: List<Pair<String, Boolean>> = listOf(
            "즐거움" to true, "감사" to true,
            "평온" to true, "자신감" to true, "슬픔" to false,
            "분노" to false, "두려움" to false, "놀람" to false,
            "혐오" to false, "지루함" to false, "후회" to false, "수치심" to false,
            "실망" to false, "불안" to false, "외로움" to false, "질투" to false
    )
    override fun getCount(): Int = emotions.size

    override fun getItem(position: Int): Pair<String, Boolean> = emotions[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = convertView as? TextView ?: TextView(context)
        textView.text = emotions[position].first // 한글 감정명만 표시
        return textView
    }

    fun getSentimentByEmotion(emotion: String): Boolean {
        return emotions.find { it.first == emotion }?.second == true
    }
}