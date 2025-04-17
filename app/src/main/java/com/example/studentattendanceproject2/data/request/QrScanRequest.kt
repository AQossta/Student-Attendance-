package com.example.studentattendanceproject2.data.request

data class QrScanRequest(
    val userId: Long,
    val scheduleId: Long,
    val scanType: String,
    val latitude: Double,
    val longitude: Double
)
