package com.example.studentattendanceproject2.data.response


import com.google.gson.annotations.SerializedName

data class ScheduleDTO(
    @SerializedName("id")
    val id: Long, // 9007199254740991
    @SerializedName("subject")
    val subject: String, // string
    @SerializedName("startTime")
    val startTime: String, // 2025-06-05T14:04:43.493Z
    @SerializedName("endTime")
    val endTime: String, // 2025-06-05T14:04:43.493Z
    @SerializedName("groupId")
    val groupId: Long, // 9007199254740991
    @SerializedName("teacherId")
    val teacherId: Long, // 9007199254740991
    @SerializedName("teacherName")
    val teacherName: String, // string
    @SerializedName("groupName")
    val groupName: String // string
)