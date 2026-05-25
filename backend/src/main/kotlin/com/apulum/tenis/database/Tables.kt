package com.apulum.tenis.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time

object UsersTable : Table("users") {
    val id = long("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val displayName = varchar("display_name", 120)
    val role = varchar("role", 32).default("client")
    val phone = varchar("phone", 32).nullable()
    override val primaryKey = PrimaryKey(id)
}

object UserRole {
    const val CLIENT = "client"
    const val ADMIN = "admin"
}

object CourtsTable : Table("courts") {
    val id = varchar("id", 32)
    val nameRo = varchar("name_ro", 120)
    val nameEn = varchar("name_en", 120)
    val surfaceRo = varchar("surface_ro", 80)
    val surfaceEn = varchar("surface_en", 80)
    val type = varchar("type", 32)
    val imageUrl = varchar("image_url", 512)
    override val primaryKey = PrimaryKey(id)
}

object ReservationsTable : Table("reservations") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UsersTable.id)
    val courtId = varchar("court_id", 32).references(CourtsTable.id)
    val date = date("date")
    val startTime = time("start_time")
    val endTime = time("end_time")
    val durationMinutes = integer("duration_minutes")
    val priceRon = integer("price_ron")
    override val primaryKey = PrimaryKey(id)
}
