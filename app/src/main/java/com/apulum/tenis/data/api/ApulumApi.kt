package com.apulum.tenis.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApulumApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @GET("api/v1/courts")
    suspend fun courts(@Header("Authorization") token: String): Response<List<CourtDto>>

    @GET("api/v1/availability")
    suspend fun availability(
        @Header("Authorization") token: String,
        @Query("courtId") courtId: String,
        @Query("date") date: String,
        @Query("durationMinutes") durationMinutes: Int
    ): Response<AvailabilityResponse>

    @GET("api/v1/reservations")
    suspend fun reservations(@Header("Authorization") token: String): Response<List<ReservationDto>>

    @GET("api/v1/admin/reservations")
    suspend fun adminReservations(
        @Header("Authorization") token: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("courtId") courtId: String? = null
    ): Response<List<AdminReservationDto>>

    @POST("api/v1/reservations")
    suspend fun createReservation(
        @Header("Authorization") token: String,
        @Body body: CreateReservationRequest
    ): Response<ReservationDto>
}
