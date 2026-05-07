package com.example.tinjaukelas.network

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val user: UserDto)
data class UserDto(val id: Int, val name: String, val role: String)
data class Room(val id: Int, val name: String, val capacity: Int, val is_occupied: Int)
data class SchoolClass(
    val id: Int,
    val name: String,
    val room: Room?,
    val members_count: Int,
    val attendance_percentage: Int?
)data class Student(val id: Int, val name: String)
data class AbsenceEntry(val student_id: Int, val status: String, val note: String?)
data class AttendanceRequest(val class_id: Int, val date: String, val absences: List<AbsenceEntry>)
data class AttendanceResponse(val message: String, val attendance_id: Int)
data class AttendanceSummary(val student_id: Int, val name: String, val hadir: Int, val tidak_hadir: Int, val total_sesi: Int, val persentase: Int)
data class CreateClassRequest(val name: String, val room_id: Int? = null)
data class CreateRoomRequest(val name: String, val capacity: Int = 0)
data class UpdateRoomRequest(val is_occupied: Boolean)
data class AbsenceDetail(val student_name: String, val status: String, val note: String?)
data class AttendanceDetail(val id: Int, val date: String, val absences: List<AbsenceDetail>)