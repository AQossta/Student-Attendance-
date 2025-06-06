package com.example.studentattendanceproject2.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.data.response.StudentAttendance

class StudentAdapter(
    private var students: List<StudentAttendance>
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        val tvEntry: TextView = itemView.findViewById(R.id.tvEnterTime)
        val tvExit: TextView = itemView.findViewById(R.id.tvExitTime)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_view, parent, false)
        return StudentViewHolder(view)
    }

    override fun getItemCount(): Int = students.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.tvName.text = student.name
        holder.tvEmail.text = student.email
        holder.tvPhone.text = student.phoneNumber
        holder.tvEntry.text = student.attendTime ?: "-"
        holder.tvExit.text = student.exitTime ?: "-"
        holder.tvDuration.text = "${student.attendanceDuration} мин"
    }

    fun submitList(newList: List<StudentAttendance>) {
        students = newList
        notifyDataSetChanged()
    }
}
