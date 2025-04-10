package com.example.studentattendanceproject2.Data

import android.content.Context
import android.content.SharedPreferences

class SharedProvider(private val context: Context) {

    private val preference: SharedPreferences
        get() = context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    fun saveLanguage(language: String){
        preference.edit().putString("language", language).apply()
    }

    fun getLanguage(): String {
        return preference.getString("language", "kk").toString()
    }

    fun getToken(): String? {
        return preference.getString("auth-token", null)
    }


    fun clearUserData() {
        val editor = preference.edit()
        editor.clear() // Удаляет все сохраненные данные
        editor.apply()
    }
}