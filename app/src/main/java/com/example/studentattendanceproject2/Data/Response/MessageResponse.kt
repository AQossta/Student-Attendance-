package com.example.studentattendanceproject2.Data.Response

import com.google.gson.annotations.SerializedName

data class MessageResponse<T>(
    @SerializedName("body")
    val body: T,  // Теперь `T` будет `LoginResponse`
    @SerializedName("message")
    val message: String? // Сообщение с сервера
)
