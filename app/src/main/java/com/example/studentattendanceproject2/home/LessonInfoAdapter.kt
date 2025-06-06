package com.example.studentattendanceproject2.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.data.response.ScheduleDTO

class LessonInfoAdapter(
    private val schedule: ScheduleDTO
) : RecyclerView.Adapter<LessonInfoAdapter.LessonViewHolder>() {

    inner class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textLesson: TextView = itemView.findViewById(R.id.textLesson)
        val groupText: TextView = itemView.findViewById(R.id.groupText)
        val teacherText: TextView = itemView.findViewById(R.id.teacherText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_information_view, parent, false)
        return LessonViewHolder(view)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.textLesson.text = "Тақырып: ${schedule.subject}"
        holder.groupText.text = "Топ: ${schedule.groupName}"
        holder.teacherText.text = "Оқытушы: ${schedule.teacherName}"
        holder.timeText.text = "Уақыт: ${schedule.startTime.split("T")[1].substring(0, 5)} - ${schedule.endTime.split("T")[1].substring(0, 5)}"
    }
}
