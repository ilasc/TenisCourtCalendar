package com.apulum.tenis

import com.apulum.tenis.database.DatabaseFactory
import com.apulum.tenis.routes.apiRoutes
import com.apulum.tenis.services.AuthService
import com.apulum.tenis.services.JwtConfig
import com.apulum.tenis.services.ReservationService
import com.auth0.jwt.JWT
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    val authService = AuthService()
    val reservationService = ReservationService()

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            }
        )
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled error", cause)
            call.respond(io.ktor.http.HttpStatusCode.InternalServerError, mapOf("message" to "Internal error"))
        }
    }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Apulum Tenis"
            verifier(
                JWT.require(JwtConfig.algorithm)
                    .withIssuer("apulum-tenis")
                    .withAudience("apulum-tenis-users")
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains("apulum-tenis-users")) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
        apiRoutes(authService, reservationService)
    }
}
