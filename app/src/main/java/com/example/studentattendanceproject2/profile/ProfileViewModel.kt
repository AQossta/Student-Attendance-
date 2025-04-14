package com.example.studentattendanceproject2.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {

    private val _language: MutableLiveData<String> = MutableLiveData()
    val language: LiveData<String> = _language

    fun selectLanguage(language: String){
        _language.postValue(language)
    }
}