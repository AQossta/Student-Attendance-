package com.example.studentattendanceproject2.data.request

data class QrScanRequest(
    val userId: Long,
    val code: String,
    val scanType: String,
    val latitude: Double,
    val longitude: Double
)
