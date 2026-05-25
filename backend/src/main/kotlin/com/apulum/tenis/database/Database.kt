package com.apulum.tenis.database

import com.apulum.tenis.database.UserRole
import com.apulum.tenis.services.ReservationService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:h2:file:./data/apulum_tenis;MODE=PostgreSQL;AUTO_SERVER=TRUE",
            driver = "org.h2.Driver",
            user = "sa",
            password = ""
        )
        transaction {
            SchemaUtils.create(UsersTable, CourtsTable, ReservationsTable)
            SchemaUtils.createMissingTablesAndColumns(UsersTable, CourtsTable, ReservationsTable)
            seedIfEmpty()
            ensureRoles()
        }
    }

    private fun ensureRoles() {
        UsersTable.update({ UsersTable.role eq "" }) {
            it[role] = UserRole.CLIENT
        }
        UsersTable.update({ UsersTable.email eq "catalin@apulum.ro" }) {
            it[role] = UserRole.ADMIN
        }
    }

    private fun seedIfEmpty() {
        if (CourtsTable.selectAll().empty()) {
            CourtsTable.insert {
                it[id] = ReservationService.COURT_OUTDOOR
                it[nameRo] = "Teren exterior"
                it[nameEn] = "Outdoor court"
                it[surfaceRo] = "zgură"
                it[surfaceEn] = "clay"
                it[type] = "outdoor"
                it[imageUrl] = "court_outdoor"
            }
            CourtsTable.insert {
                it[id] = ReservationService.COURT_INDOOR
                it[nameRo] = "Teren acoperit"
                it[nameEn] = "Indoor court"
                it[surfaceRo] = "zgură"
                it[surfaceEn] = "clay"
                it[type] = "indoor"
                it[imageUrl] = "court_indoor"
            }
        }
        if (UsersTable.selectAll().none { it[UsersTable.email] == "andrei@apulum.ro" }) {
            UsersTable.insert {
                it[email] = "andrei@apulum.ro"
                it[passwordHash] = BCrypt.hashpw("tenis123", BCrypt.gensalt())
                it[displayName] = "Andrei"
                it[role] = UserRole.CLIENT
            }
        }
        if (UsersTable.selectAll().none { it[UsersTable.email] == "catalin@apulum.ro" }) {
            UsersTable.insert {
                it[email] = "catalin@apulum.ro"
                it[passwordHash] = BCrypt.hashpw("tenis123", BCrypt.gensalt())
                it[displayName] = "Cătălin"
                it[role] = UserRole.ADMIN
                it[phone] = "0722 000 000"
            }
        }
    }
}
