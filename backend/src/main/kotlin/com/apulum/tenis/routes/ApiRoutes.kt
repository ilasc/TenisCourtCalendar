package com.apulum.tenis.routes

import com.apulum.tenis.database.UserRole
import com.apulum.tenis.models.AdminReservationDto
import com.apulum.tenis.models.AuthResponse
import com.apulum.tenis.models.AvailabilityResponse
import com.apulum.tenis.models.CreateReservationRequest
import com.apulum.tenis.models.CourtDto
import com.apulum.tenis.models.ErrorResponse
import com.apulum.tenis.models.LoginRequest
import com.apulum.tenis.models.RegisterRequest
import com.apulum.tenis.models.ReservationDto
import com.apulum.tenis.services.AuthService
import com.apulum.tenis.services.JwtConfig
import com.apulum.tenis.services.ReservationService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.apiRoutes(
    authService: AuthService,
    reservationService: ReservationService
) {
    route("/api/v1") {
        post("/auth/login") {
            val body = call.receive<LoginRequest>()
            val result = authService.login(body)
            if (result == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid email or password"))
            } else {
                call.respond(result)
            }
        }

        post("/auth/register") {
            val body = call.receive<RegisterRequest>()
            val result = authService.register(body)
            if (result == null) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("Email already registered"))
            } else {
                call.respond(result)
            }
        }

        authenticate("auth-jwt") {
            get("/courts") {
                call.respond<List<CourtDto>>(reservationService.listCourts())
            }

            get("/availability") {
                val courtId = call.request.queryParameters["courtId"]
                val date = call.request.queryParameters["date"]
                val duration = call.request.queryParameters["durationMinutes"]?.toIntOrNull()
                if (courtId.isNullOrBlank() || date.isNullOrBlank() || duration == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing parameters"))
                    return@get
                }
                val availability = reservationService.availability(courtId, date, duration)
                if (availability == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request"))
                } else {
                    call.respond<AvailabilityResponse>(availability)
                }
            }

            get("/reservations") {
                val userId = call.principal<JWTPrincipal>()?.let(JwtConfig::userId)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(reservationService.userReservations(userId))
            }

            post("/reservations") {
                val userId = call.principal<JWTPrincipal>()?.let(JwtConfig::userId)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val body = call.receive<CreateReservationRequest>()
                try {
                    val created = reservationService.createReservation(userId, body)
                    call.respond<ReservationDto>(created)
                } catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.Conflict, ErrorResponse(e.message ?: "Conflict"))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Bad request"))
                }
            }

            delete("/reservations/{id}") {
                val userId = call.principal<JWTPrincipal>()?.let(JwtConfig::userId)
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val reservationId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid reservation id"))
                val deleted = reservationService.deleteUserReservation(userId, reservationId)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Reservation not found or cannot be deleted"))
                }
            }

            get("/admin/reservations") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val userId = JwtConfig.userId(principal)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                if (JwtConfig.role(principal) != UserRole.ADMIN && !authService.isAdmin(userId)) {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse("Admin access required"))
                    return@get
                }
                val courtId = call.request.queryParameters["courtId"]
                val from = call.request.queryParameters["from"]
                val to = call.request.queryParameters["to"]
                val date = call.request.queryParameters["date"]
                val reservations = when {
                    !from.isNullOrBlank() && !to.isNullOrBlank() ->
                        reservationService.adminReservationsInRange(from, to, courtId)
                    !date.isNullOrBlank() ->
                        reservationService.adminReservations(date, courtId)
                    else -> null
                }
                if (reservations == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Provide date=YYYY-MM-DD or from= and to=")
                    )
                } else {
                    call.respond<List<AdminReservationDto>>(reservations)
                }
            }
        }
    }
}
