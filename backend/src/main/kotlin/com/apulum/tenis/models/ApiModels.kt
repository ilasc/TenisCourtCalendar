package com.apulum.tenis.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Long,
    val displayName: String,
    val email: String,
    val role: String
)

@Serializable
data class CourtDto(
    val id: String,
    val nameRo: String,
    val nameEn: String,
    val surfaceRo: String,
    val surfaceEn: String,
    val type: String,
    val imageUrl: String
)

@Serializable
data class TimeSlotDto(
    val time: String,
    val available: Boolean
)

@Serializable
data class AvailabilityResponse(
    val courtId: String,
    val date: String,
    val durationMinutes: Int,
    val slots: List<TimeSlotDto>
)

@Serializable
data class CreateReservationRequest(
    val courtId: String,
    val date: String,
    val startTime: String,
    val durationMinutes: Int
)

@Serializable
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

@Serializable
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

@Serializable
data class ErrorResponse(val message: String)
