package com.example.studentattendanceproject2.Data

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "auth_prefs"  // Имя файла SharedPreferences
    private const val KEY_TOKEN = "access_token"       // Ключ для хранения токена

    // Функция для сохранения токена
    fun saveToken(context: Context, token: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
}