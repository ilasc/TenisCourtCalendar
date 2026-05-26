package com.apulum.tenis.database

import com.apulum.tenis.database.UserRole
import com.apulum.tenis.services.ReservationService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
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
            migrateReservationTimesToVarchar()
            seedIfEmpty()
            ensureCourts()
            ensureRoles()
        }
    }

    private fun migrateReservationTimesToVarchar() {
        try {
            java.sql.DriverManager.getConnection(
                "jdbc:h2:file:./data/apulum_tenis;MODE=PostgreSQL;AUTO_SERVER=TRUE",
                "sa",
                ""
            ).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate(
                        "ALTER TABLE reservations ALTER COLUMN start_time VARCHAR(8)"
                    )
                    statement.executeUpdate(
                        "ALTER TABLE reservations ALTER COLUMN end_time VARCHAR(8)"
                    )
                }
            }
        } catch (_: Exception) {
            // Column type already migrated or table not present yet.
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
            insertCourt(
                id = ReservationService.COURT_1,
                nameRo = "Teren 1",
                nameEn = "Court 1",
                courtType = "outdoor"
            )
            insertCourt(
                id = ReservationService.COURT_2,
                nameRo = "Teren 2",
                nameEn = "Court 2",
                courtType = "outdoor"
            )
            insertCourt(
                id = ReservationService.COURT_INDOOR,
                nameRo = "Acoperit",
                nameEn = "Indoor",
                courtType = "indoor"
            )
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

    private fun ensureCourts() {
        upsertCourt(
            id = ReservationService.COURT_1,
            nameRo = "Teren 1",
            nameEn = "Court 1",
            courtType = "outdoor"
        )
        upsertCourt(
            id = ReservationService.COURT_2,
            nameRo = "Teren 2",
            nameEn = "Court 2",
            courtType = "outdoor"
        )
        upsertCourt(
            id = ReservationService.COURT_INDOOR,
            nameRo = "Acoperit",
            nameEn = "Indoor",
            courtType = "indoor"
        )
        migrateLegacyOutdoorCourt()
    }

    private fun migrateLegacyOutdoorCourt() {
        val legacyId = "exterior"
        if (CourtsTable.selectAll().none { it[CourtsTable.id] == legacyId }) return
        ReservationsTable.update({ ReservationsTable.courtId eq legacyId }) {
            it[courtId] = ReservationService.COURT_1
        }
        CourtsTable.deleteWhere { CourtsTable.id eq legacyId }
    }

    private fun upsertCourt(
        id: String,
        nameRo: String,
        nameEn: String,
        courtType: String
    ) {
        if (CourtsTable.selectAll().any { it[CourtsTable.id] == id }) {
            CourtsTable.update({ CourtsTable.id eq id }) {
                it[CourtsTable.nameRo] = nameRo
                it[CourtsTable.nameEn] = nameEn
                it[CourtsTable.surfaceRo] = "zgură"
                it[CourtsTable.surfaceEn] = "clay"
                it[CourtsTable.type] = courtType
            }
        } else {
            insertCourt(id, nameRo, nameEn, courtType)
        }
    }

    private fun insertCourt(
        id: String,
        nameRo: String,
        nameEn: String,
        courtType: String
    ) {
        CourtsTable.insert {
            it[CourtsTable.id] = id
            it[CourtsTable.nameRo] = nameRo
            it[CourtsTable.nameEn] = nameEn
            it[CourtsTable.surfaceRo] = "zgură"
            it[CourtsTable.surfaceEn] = "clay"
            it[CourtsTable.type] = courtType
            it[CourtsTable.imageUrl] = id
        }
    }
}
