package com.example.studentattendanceproject2.Data.Response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("id")
    val id: Long, // 1
    @SerializedName("email")
    val email: String, // Azhar@gmail.com
    @SerializedName("name")
    val name: String, // Azhar
    @SerializedName("phoneNumber")
    val phoneNumber: String, // +77011112235
    @SerializedName("dateOfBirth")
    val dateOfBirth: Any, // null
    @SerializedName("roles")
    val roles: List<String>,
    @SerializedName("groupId")
    val groupId: Long, // null
    @SerializedName("groupName")
    val groupName: Any, // null
    @SerializedName("accessToken")
    val accessToken: String // hSJehroPljLIPnSk9mXfeyRhyxtIQGnDXzwsul0Cqti5ezAsd7DNA0un5NrymjF4J6nt5CGETnLlJ2MWyuOn8GJPLew8a3h5Fdl8tdGXInoRoGdWtCG7Pf6zt2dV8X6n
)
