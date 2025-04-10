package com.example.studentattendanceproject2.Data.Response

import com.google.gson.annotations.SerializedName

data class QrBody(
    @SerializedName("id")
    val id: Long, // 9007199254740991
    @SerializedName("qrCode")
    val qrCode: String, // string
    @SerializedName("createdAt")
    val createdAt: String, // 2025-04-07T04:20:19.946Z
    @SerializedName("expiration")
    val expiration: String // 2025-04-07T04:20:19.957Z
)
