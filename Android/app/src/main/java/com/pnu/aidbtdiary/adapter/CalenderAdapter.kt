// adapter/CalendarAdapter.kt
package com.pnu.aidbtdiary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pnu.aidbtdiary.R
import com.pnu.aidbtdiary.model.DiaryDay
import com.pnu.aidbtdiary.model.Sentiment

class CalendarAdapter(
    private val days: List<DiaryDay>,
    private val onDayClick: (DiaryDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        val displayMetrics = parent.context.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val calendarHeight = (screenHeight * 0.6).toInt()
        val cellHeight = calendarHeight / 6
        view.layoutParams.height = cellHeight
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        fun bind(day: DiaryDay) {
            if (day.date == null) {
                tvDay.text = ""
                tvDay.setBackgroundResource(R.drawable.bg_day_default)
                itemView.isClickable = false
            } else {
                tvDay.text = day.date.dayOfMonth.toString()
                if (!day.hasDiary) {
                    tvDay.setBackgroundResource(R.drawable.bg_day_default)
                } else {
                    when (day.sentiment) {
                        Sentiment.POSITIVE -> tvDay.setBackgroundResource(R.drawable.bg_day_positive)
                        Sentiment.NEGATIVE -> tvDay.setBackgroundResource(R.drawable.bg_day_negative)
                        else -> tvDay.setBackgroundResource(R.drawable.bg_day_default)
                    }
                }
                itemView.setOnClickListener { onDayClick(day) }
                itemView.isClickable = true
            }
        }
    }
}