package com.example.studentattendanceproject2.Login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentattendanceproject2.Data.Response.LoginResponse

class AuthViewModel: ViewModel() {
    private val _userData = MutableLiveData<LoginResponse?>()
    val userData: LiveData<LoginResponse?> = _userData

    fun setUserData(data: LoginResponse) {
        _userData.value = data
    }
}