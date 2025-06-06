package com.example.studentattendanceproject2.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentattendanceproject2.R

class StatsInfoAdapter(
    private val total: Int,
    private val present: Int,
    private val percentage: Double
) : RecyclerView.Adapter<StatsInfoAdapter.StatsViewHolder>() {

    inner class StatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val totalStudents: TextView = itemView.findViewById(R.id.totalStudents)
        val presentStudents: TextView = itemView.findViewById(R.id.presentStudents)
        val attendancePercentage: TextView = itemView.findViewById(R.id.attendancePercentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participation_statistics_view, parent, false)
        return StatsViewHolder(view)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        holder.totalStudents.text = "Барлық студенттер: $total"
        holder.presentStudents.text = "Қатысты: $present"
        holder.attendancePercentage.text = "Пайыз: %.2f %%".format(percentage)
    }
}
