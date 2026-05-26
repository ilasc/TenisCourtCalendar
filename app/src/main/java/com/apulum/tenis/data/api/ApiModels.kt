package com.apulum.tenis.data.api

data class LoginRequest(val email: String, val password: String)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val displayName: String,
    val email: String,
    val role: String
)

data class CourtDto(
    val id: String,
    val nameRo: String,
    val nameEn: String,
    val surfaceRo: String,
    val surfaceEn: String,
    val type: String,
    val imageUrl: String,
    val bookable: Boolean = true
)

data class TimeSlotDto(
    val time: String,
    val available: Boolean = false,
    val occupied: Boolean = false
)

data class AvailabilityResponse(
    val courtId: String,
    val date: String,
    val durationMinutes: Int,
    val slots: List<TimeSlotDto>
)

data class CreateReservationRequest(
    val courtId: String,
    val date: String,
    val startTime: String,
    val durationMinutes: Int
)

data class ReservationDto(
    val id: Long,
    val courtId: String,
    val courtNameRo: String,
    val courtNameEn: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val priceRon: Int,
    val surfaceRo: String,
    val surfaceEn: String
)

data class AdminReservationDto(
    val id: Long,
    val courtId: String,
    val courtNameRo: String,
    val courtNameEn: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val status: String,
    val clientName: String,
    val clientPhone: String?,
    val clientEmail: String
)

data class ErrorResponse(val message: String)
