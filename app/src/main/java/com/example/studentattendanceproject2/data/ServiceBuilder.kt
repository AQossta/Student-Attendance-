package com.example.studentattendanceproject2.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.7:8080/")  // <-- адрес твоего компьютера!
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()


    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}