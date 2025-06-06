package com.example.studentattendanceproject2.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentattendanceproject2.data.response.ScheduleGroupResponse
import com.example.studentattendanceproject2.data.response.ScheduleTeacherResponse
import com.example.studentattendanceproject2.databinding.ItemScheduleViewBinding

class ScheduleAdapter(
    private var scheduleList: List<ScheduleGroupResponse> = emptyList(),
    private var teacherScheduleList: List<ScheduleTeacherResponse> = emptyList(),
    private val onItemClick: (ScheduleTeacherResponse) -> Unit = {},
    private val onStatsClick: (ScheduleTeacherResponse) -> Unit = {}
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        if (scheduleList.isNotEmpty()) {
            holder.bind(scheduleList[position])
        } else if (teacherScheduleList.isNotEmpty()) {
            holder.bindTeacher(teacherScheduleList[position])
        }
    }

    override fun getItemCount(): Int {
        return if (scheduleList.isNotEmpty()) {
            scheduleList.size
        } else {
            teacherScheduleList.size
        }
    }

    fun updateData(newList: List<ScheduleGroupResponse>) {
        scheduleList = newList
        teacherScheduleList = emptyList()
        notifyDataSetChanged()
    }

    fun updateTeacherData(newList: List<ScheduleTeacherResponse>) {
        teacherScheduleList = newList
        scheduleList = emptyList()
        notifyDataSetChanged()
    }

    inner class ScheduleViewHolder(private val binding: ItemScheduleViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleGroupResponse) {
            binding.tvStartTime.text = item.startTime.split("T")[1].substring(0, 5)
            binding.tvEndTime.text = item.endTime.split("T")[1].substring(0, 5)
            binding.tvSubject.text = item.subject
            binding.tvGroup.text = "Группа: ${item.groupName}"
            binding.tvTeacherName.text = "Преподаватель: ${item.teacherName}"
            binding.root.setOnClickListener(null)
        }

        fun bindTeacher(item: ScheduleTeacherResponse) {
            binding.tvStartTime.text = item.startTime.split("T")[1].substring(0, 5)
            binding.tvEndTime.text = item.endTime.split("T")[1].substring(0, 5)
            binding.tvSubject.text = item.subject
            binding.tvGroup.text = "Группа: ${item.groupName}"
            binding.tvTeacherName.text = "Преподаватель: ${item.teacherName}"

            binding.root.setOnClickListener {
                onItemClick(item) // Передаем весь объект
            }

            binding.btnStats.setOnClickListener {
                onStatsClick(item) // Статистика батырмасы басылғанда
            }
        }
    }
}