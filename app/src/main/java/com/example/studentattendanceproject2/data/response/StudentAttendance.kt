package com.example.studentattendanceproject2.data.response


import com.google.gson.annotations.SerializedName

data class StudentAttendance(
    @SerializedName("id")
    val id: Long, // 9007199254740991
    @SerializedName("name")
    val name: String, // string
    @SerializedName("email")
    val email: String, // string
    @SerializedName("phoneNumber")
    val phoneNumber: String, // string
    @SerializedName("attend")
    val attend: Boolean, // true
    @SerializedName("attendTime")
    val attendTime: String, // 2025-06-05T14:04:43.493Z
    @SerializedName("exitTime")
    val exitTime: String, // 2025-06-05T14:04:43.493Z
    @SerializedName("attendanceDuration")
    val attendanceDuration: Long // 9007199254740991
)