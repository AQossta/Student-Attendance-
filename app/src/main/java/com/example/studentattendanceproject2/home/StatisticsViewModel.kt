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

    private val _statsResponse = MutableLiveData<MessageResponse<AttendanceStats>>()
    val statsResponse: LiveData<MessageResponse<AttendanceStats>> = _statsResponse

    private val _errorResponse = MutableLiveData<String>()
    val errorResponse: LiveData<String> = _errorResponse

    fun loadStats(scheduleId: Int, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val apiService = ServiceBuilder.buildService(ApiService::class.java)
            runCatching {
                apiService.getScheduleStats(scheduleId, "Bearer $token")
            }.onSuccess { response ->
                _statsResponse.postValue(response)
            }.onFailure { e ->
                Log.e("StatisticsViewModel", "Статистика жүктеу қатесі: ${e.message}", e)
                _errorResponse.postValue(e.message ?: "Белгісіз қате")
            }
        }
    }
}
