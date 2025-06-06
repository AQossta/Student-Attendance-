package com.example.studentattendanceproject2.home

import android.os.Bundle
import android.text.format.DateUtils.formatDateTime
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.data.ServiceBuilder
import com.example.studentattendanceproject2.service.ApiService
import kotlinx.coroutines.launch

class StatisticsFragment : Fragment() {

    private val apiService = ServiceBuilder.buildService(ApiService::class.java)
    private var scheduleId: Int = -1
    private var authToken: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        scheduleId = arguments?.getInt("scheduleId") ?: -1
        authToken = arguments?.getString("auth-token")

        Log.d("StatisticsFragment", "scheduleId = $scheduleId, token = $authToken")

        if (scheduleId != -1 && !authToken.isNullOrEmpty()) {
            fetchStatistics(scheduleId, authToken!!, view)
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBackStatistic)
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_statisticsFragment_to_homeFragment)
        }
    }

    private fun fetchStatistics(scheduleId: Int, token: String, rootView: View) {
        lifecycleScope.launch {
            try {
                val response = apiService.getScheduleStats(scheduleId, token)
                val stats = response.body

                if (stats != null) {
                    val studentRecycler = rootView.findViewById<RecyclerView>(R.id.rc_main_categories_3)
                    studentRecycler.layoutManager = LinearLayoutManager(requireContext())
                    studentRecycler.adapter = StudentAdapter(stats.studentDTO)


                    val lessonRecycler = rootView.findViewById<RecyclerView>(R.id.rc_main_categories_1)
                    lessonRecycler.layoutManager = LinearLayoutManager(requireContext())
                    lessonRecycler.adapter = LessonInfoAdapter(stats.scheduleDTO)


                    val statsRecycler = rootView.findViewById<RecyclerView>(R.id.rc_main_categories_2)
                    statsRecycler.layoutManager = LinearLayoutManager(requireContext())
                    statsRecycler.adapter = StatsInfoAdapter(
                        stats.totalCount.toInt(),
                        stats.presentCount.toInt(),
                        stats.statistic
                    )

                }
            } catch (e: Exception) {
                Log.e("StatisticsFragment", "Error: ${e.message}")
            }
        }
    }


}
