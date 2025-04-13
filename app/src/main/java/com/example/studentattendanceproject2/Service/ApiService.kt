package com.example.studentattendanceproject2.Service

import com.example.studentattendanceproject2.Data.Request.LoginRequest
import com.example.studentattendanceproject2.Data.Response.LoginResponse
import com.example.studentattendanceproject2.Data.Response.MessageResponse
import com.example.studentattendanceproject2.Data.Response.QrBody
import com.example.studentattendanceproject2.Data.Response.ScheduleGroupResponse
import com.example.studentattendanceproject2.Data.Response.ScheduleTeacherResponse
import com.example.studentattendanceproject2.Data.ServiceBuilder
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/api/v1/auth/sign-in")
    suspend fun loginUser(@Body request: LoginRequest): MessageResponse<LoginResponse>

    @GET("/api/v1/student/schedule/group/{groupId}")
    suspend fun scheduleGroup(
        @Path("groupId") groupId: Long,
        @Header("auth-token") authToken: String
    ): MessageResponse<List<ScheduleGroupResponse>>

    @GET("/api/v1/teacher/schedule/lecturer/{lecturerId}")
    suspend fun scheduleTeacher(
        @Path("lecturerId") teacherId: Long,
        @Header("auth-token") authToken: String
    ): MessageResponse<List<ScheduleTeacherResponse>>

    @DELETE("/api/v1/auth/logout")
    suspend fun logout(@Header("auth-token") authToken: String): MessageResponse<Any>

    companion object {
        fun create(): ApiService {
            return ServiceBuilder.retrofit.create(ApiService::class.java)
        }
    }

    @POST("/api/v1/teacher/qr/generate")
    suspend fun generateQrCode(
        @Query("scheduleId") scheduleId: Long,
        @Header("auth-token") authToken: String
    ) : MessageResponse<QrBody>
}