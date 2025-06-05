package com.example.studentattendanceproject2.data.request


import com.google.gson.annotations.SerializedName

data class ScanRequest(
    @SerializedName("userId")
    val userId: Long, // 9007199254740991
    @SerializedName("scheduleId")
    val scheduleId: Long, // 9007199254740991
    @SerializedName("scanType")
    val scanType: String, // string
    @SerializedName("latitude")
    val latitude: Double, // 0.1
    @SerializedName("longitude")
    val longitude: Double // 0.1
)