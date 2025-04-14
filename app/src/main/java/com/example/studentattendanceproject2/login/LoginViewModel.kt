package com.example.studentattendanceproject2.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentattendanceproject2.data.request.LoginRequest
import com.example.studentattendanceproject2.data.response.LoginResponse
import com.example.studentattendanceproject2.data.response.MessageResponse
import com.example.studentattendanceproject2.data.ServiceBuilder
import com.example.studentattendanceproject2.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel(): ViewModel() {
    private var _loginResponse: MutableLiveData<MessageResponse<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<MessageResponse<LoginResponse>> = _loginResponse

    private var _errorResponse: MutableLiveData<String> = MutableLiveData()
    val errorResponse: LiveData<String> = _errorResponse

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ServiceBuilder.buildService(ApiService::class.java)
            runCatching { response.loginUser(LoginRequest(email, password)) }
                .onSuccess {
                    _loginResponse.postValue(it)
                }
                .onFailure {
                    Log.e("LoginViewModel", "Login error: ${it.message}", it)
                    _errorResponse.postValue(it.message)
                }
        }
    }
}