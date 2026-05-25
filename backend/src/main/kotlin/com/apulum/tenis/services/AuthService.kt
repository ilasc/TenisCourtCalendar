package com.apulum.tenis.services

import com.apulum.tenis.database.UserRole
import com.apulum.tenis.database.UsersTable
import com.apulum.tenis.models.AuthResponse
import com.apulum.tenis.models.LoginRequest
import com.apulum.tenis.models.RegisterRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class AuthService {
    fun isAdmin(userId: Long): Boolean = transaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq userId }
            .firstOrNull()
            ?.get(UsersTable.role) == UserRole.ADMIN
    }

    fun login(request: LoginRequest): AuthResponse? = transaction {
        val user = UsersTable.selectAll()
            .find { it[UsersTable.email].equals(request.email, ignoreCase = true) }
            ?: return@transaction null
        if (!BCrypt.checkpw(request.password, user[UsersTable.passwordHash])) {
            return@transaction null
        }
        val role = user[UsersTable.role].ifBlank { UserRole.CLIENT }
        AuthResponse(
            token = JwtConfig.createToken(
                user[UsersTable.id],
                user[UsersTable.email],
                role
            ),
            userId = user[UsersTable.id],
            displayName = user[UsersTable.displayName],
            email = user[UsersTable.email],
            role = role
        )
    }

    fun register(request: RegisterRequest): AuthResponse? = transaction {
        val exists = UsersTable.selectAll()
            .any { it[UsersTable.email].equals(request.email, ignoreCase = true) }
        if (exists) return@transaction null
        val id = UsersTable.insert {
            it[email] = request.email.trim().lowercase()
            it[passwordHash] = BCrypt.hashpw(request.password, BCrypt.gensalt())
            it[displayName] = request.displayName.trim()
            it[role] = UserRole.CLIENT
        } get UsersTable.id
        AuthResponse(
            token = JwtConfig.createToken(id, request.email.trim().lowercase(), UserRole.CLIENT),
            userId = id,
            displayName = request.displayName.trim(),
            email = request.email.trim().lowercase(),
            role = UserRole.CLIENT
        )
    }
}
