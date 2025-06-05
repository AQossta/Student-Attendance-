package com.example.studentattendanceproject2.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentattendanceproject2.data.response.StudentAttendance
import com.example.studentattendanceproject2.databinding.ItemStudentViewBinding

class StudentAdapter(
    private val students: List<StudentAttendance>
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    inner class StudentViewHolder(val binding: ItemStudentViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun getItemCount(): Int = students.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        with(holder.binding) {
            tvName.text = student.name
            tvEmail.text = "${student.email}"
            tvPhone.text = "${student.phoneNumber}"
            tvEnterTime.text = "${student.attendTime ?: "—"}"
            tvExitTime.text = "${student.exitTime ?: "—"}"
            tvDuration.text = "${student.attendanceDuration ?: "-"} мин"

            val isExpanded = expandedPositions.contains(position)
            expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

            tvName.setOnClickListener {
                if (isExpanded) expandedPositions.remove(position) else expandedPositions.add(position)
                notifyItemChanged(position)
            }
        }
    }
}

