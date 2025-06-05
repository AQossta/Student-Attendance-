package com.example.studentattendanceproject2.data.response


import com.google.gson.annotations.SerializedName

data class AttendanceStats(
    @SerializedName("scheduleDTO")
    val scheduleDTO: ScheduleDTO,
    @SerializedName("totalCount")
    val totalCount: Double, // 0.1
    @SerializedName("presentCount")
    val presentCount: Double, // 0.1
    @SerializedName("statistic")
    val statistic: Double, // 0.1
    @SerializedName("studentDTO")
    val studentDTO: List<StudentAttendance>
)