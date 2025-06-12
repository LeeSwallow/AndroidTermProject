package com.pnu.aidbtdiary.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class DbtSkillAdapter(
    private val context: Context,
) : BaseAdapter() {
    private val dbtSkills: List<String> = listOf(
        "감정 인식",  "감정 표현",  "감정 조절",
        "사고 기록",  "사고 분석",  "사고 수정",
        "행동 계획",  "문제 해결",  "사회적 기술",
        "스트레스 관리"
    )

    override fun getCount(): Int = dbtSkills.size
    override fun getItem(position: Int): String = dbtSkills[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = convertView as? TextView ?: TextView(context)
        textView.text = dbtSkills[position]
        return textView
    }
}