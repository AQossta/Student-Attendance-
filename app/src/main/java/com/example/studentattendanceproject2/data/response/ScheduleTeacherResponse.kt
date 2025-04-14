package com.example.studentattendanceproject2.data.response

import com.google.gson.annotations.SerializedName

data class ScheduleTeacherResponse(
    @SerializedName("id")
    val id: Long, // 9007199254740991
    @SerializedName("subject")
    val subject: String, // string
    @SerializedName("startTime")
    val startTime: String, // 2025-03-16T17:45:23.441Z
    @SerializedName("endTime")
    val endTime: String, // 2025-03-16T17:45:23.441Z
    @SerializedName("groupId")
    val groupId: Long, // 9007199254740991
    @SerializedName("teacherId")
    val teacherId: Long, // 9007199254740991
    @SerializedName("teacherName")
    val teacherName: String, // string
    @SerializedName("groupName")
    val groupName: String // string
) : java.io.Serializable
