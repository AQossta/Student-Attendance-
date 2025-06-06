package com.example.studentattendanceproject2.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentattendanceproject2.data.ServiceBuilder
import com.example.studentattendanceproject2.data.response.AttendanceStats
import com.example.studentattendanceproject2.data.response.MessageResponse
import com.example.studentattendanceproject2.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StatisticsViewModel : ViewModel() {
    private val _stats = MutableLiveData<AttendanceStats>()
    val stats: LiveData<AttendanceStats> = _stats

    private val apiService = ServiceBuilder.buildService(ApiService::class.java)

    fun fetchStatistics(scheduleId: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getScheduleStats(scheduleId, token)
                _stats.value = response.body
            } catch (e: Exception) {
                Log.e("StatsViewModel", "Error: ${e.message}")
            }
        }
    }
}



