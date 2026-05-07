package com.example.tinjaukelas.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("rooms")
    suspend fun createRoom(
        @Header("Authorization") token: String,
        @Body body: CreateRoomRequest
    ): Response<Room>

    @PATCH("rooms/{id}")
    suspend fun updateRoomStatus(
        @Header("Authorization") token: String,
        @Path("id") roomId: Int,
        @Body body: UpdateRoomRequest
    ): Response<Room>
    @POST("classes")
    suspend fun createClass(
        @Header("Authorization") token: String,
        @Body body: CreateClassRequest
    ): Response<SchoolClass>
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("rooms")
    suspend fun getRooms(@Header("Authorization") token: String): Response<List<Room>>

    @GET("classes")
    suspend fun getClasses(@Header("Authorization") token: String): Response<List<SchoolClass>>

    @GET("classes/{id}/students")
    suspend fun getStudents(
        @Header("Authorization") token: String,
        @Path("id") classId: Int
    ): Response<List<Student>>

    @POST("attendances")
    suspend fun submitAttendance(
        @Header("Authorization") token: String,
        @Body body: AttendanceRequest
    ): Response<AttendanceResponse>

    @GET("attendances/{classId}/summary")
    suspend fun getAttendanceSummary(
        @Header("Authorization") token: String,
        @Path("classId") classId: Int
    ): Response<List<AttendanceSummary>>
    @GET("classes/all")
    suspend fun getAllClasses(
        @Header("Authorization") token: String
    ): Response<List<SchoolClass>>

    @GET("attendances/{classId}/detail")
    suspend fun getAttendanceDetail(
        @Header("Authorization") token: String,
        @Path("classId") classId: Int
    ): Response<List<AttendanceDetail>>
}