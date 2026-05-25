package com.apulum.tenis.data.repository

import com.apulum.tenis.data.api.ApiClient
import com.apulum.tenis.data.api.AdminReservationDto
import com.apulum.tenis.data.api.AuthResponse
import com.apulum.tenis.data.api.AvailabilityResponse
import com.apulum.tenis.data.api.CourtDto
import com.apulum.tenis.data.api.CreateReservationRequest
import com.apulum.tenis.data.api.LoginRequest
import com.apulum.tenis.data.api.ReservationDto
import com.apulum.tenis.data.local.SessionStore
import com.apulum.tenis.data.local.UserSession

class TenisRepository(private val sessionStore: SessionStore) {
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        val response = ApiClient.api.login(LoginRequest(email, password))
        if (!response.isSuccessful || response.body() == null) {
            return Result.failure(Exception("login_failed"))
        }
        val body = response.body()!!
        sessionStore.save(body)
        return Result.success(body)
    }

    suspend fun logout() = sessionStore.clear()

    suspend fun getCourts(token: String): Result<List<CourtDto>> =
        apiCall { ApiClient.api.courts(ApiClient.bearer(token)) }

    suspend fun getAvailability(
        token: String,
        courtId: String,
        date: String,
        durationMinutes: Int
    ): Result<AvailabilityResponse> =
        apiCall {
            ApiClient.api.availability(
                ApiClient.bearer(token),
                courtId,
                date,
                durationMinutes
            )
        }

    suspend fun getReservations(token: String): Result<List<ReservationDto>> =
        apiCall { ApiClient.api.reservations(ApiClient.bearer(token)) }

    suspend fun getAdminReservations(
        token: String,
        from: String,
        to: String,
        courtId: String? = null
    ): Result<List<AdminReservationDto>> =
        apiCall {
            ApiClient.api.adminReservations(ApiClient.bearer(token), from, to, courtId)
        }

    suspend fun createReservation(
        token: String,
        request: CreateReservationRequest
    ): Result<ReservationDto> =
        apiCall { ApiClient.api.createReservation(ApiClient.bearer(token), request) }

    private inline fun <T> apiCall(block: () -> retrofit2.Response<T>): Result<T> {
        val response = block()
        if (!response.isSuccessful || response.body() == null) {
            return Result.failure(Exception(response.message()))
        }
        return Result.success(response.body()!!)
    }
}
