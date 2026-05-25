package com.apulum.tenis.services

import com.apulum.tenis.database.CourtsTable
import com.apulum.tenis.database.ReservationsTable
import com.apulum.tenis.database.UsersTable
import com.apulum.tenis.models.AdminReservationDto
import com.apulum.tenis.models.AvailabilityResponse
import com.apulum.tenis.models.CreateReservationRequest
import com.apulum.tenis.models.CourtDto
import com.apulum.tenis.models.ReservationDto
import com.apulum.tenis.models.TimeSlotDto
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReservationService {
    companion object {
        const val COURT_OUTDOOR = "exterior"
        const val COURT_INDOOR = "acoperit"
        val OPEN_TIME: LocalTime = LocalTime.of(7, 0)
        val LAST_SLOT_START: LocalTime = LocalTime.of(22, 0)
        /** Latest allowed end time (e.g. 22:00 + 60 min). */
        val CLOSE_TIME: LocalTime = LocalTime.of(23, 0)
        val SLOT_TIMES: List<LocalTime> = generateSequence(OPEN_TIME) { prev ->
            if (prev == LAST_SLOT_START) null else prev.plusHours(1)
        }.toList()
        val ALLOWED_DURATIONS = setOf(60, 90, 120)
        private val DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE
        private val TIME_FMT = DateTimeFormatter.ofPattern("HH:mm")
    }

    fun listCourts(): List<CourtDto> = transaction {
        CourtsTable.selectAll().map { row ->
            CourtDto(
                id = row[CourtsTable.id],
                nameRo = row[CourtsTable.nameRo],
                nameEn = row[CourtsTable.nameEn],
                surfaceRo = row[CourtsTable.surfaceRo],
                surfaceEn = row[CourtsTable.surfaceEn],
                type = row[CourtsTable.type],
                imageUrl = row[CourtsTable.imageUrl]
            )
        }
    }

    fun availability(courtId: String, dateStr: String, durationMinutes: Int): AvailabilityResponse? {
        if (durationMinutes !in ALLOWED_DURATIONS) return null
        val date = runCatching { LocalDate.parse(dateStr, DATE_FMT) }.getOrNull() ?: return null
        if (!courtExists(courtId)) return null

        val bookings = loadBookings(courtId, date)
        val slots = SLOT_TIMES.map { anchor ->
            resolveSlotForAnchor(anchor, bookings, durationMinutes)
        }
        return AvailabilityResponse(courtId, dateStr, durationMinutes, slots)
    }

    private fun resolveSlotForAnchor(
        anchor: LocalTime,
        bookings: List<BookingInterval>,
        durationMinutes: Int
    ): TimeSlotDto {
        val candidates = listOf(anchor, anchor.plusMinutes(30))
        for (start in candidates) {
            val end = start.plusMinutes(durationMinutes.toLong())
            if (end.isAfter(CLOSE_TIME)) continue
            if (!overlapsAny(bookings, start, end)) {
                return TimeSlotDto(time = start.format(TIME_FMT), available = true)
            }
        }
        return TimeSlotDto(time = anchor.format(TIME_FMT), available = false)
    }

    fun createReservation(userId: Long, request: CreateReservationRequest): ReservationDto {
        if (request.durationMinutes !in ALLOWED_DURATIONS) {
            throw IllegalArgumentException("Invalid duration")
        }
        val date = LocalDate.parse(request.date, DATE_FMT)
        val start = LocalTime.parse(request.startTime, TIME_FMT)
        val end = start.plusMinutes(request.durationMinutes.toLong())
        if (end.isAfter(CLOSE_TIME)) {
            throw IllegalArgumentException("Reservation exceeds closing time")
        }
        if (!courtExists(request.courtId)) {
            throw IllegalArgumentException("Unknown court")
        }

        return transaction {
            val bookings = loadBookings(request.courtId, date)
            if (overlapsAny(bookings, start, end)) {
                throw IllegalStateException("Time slot is no longer available")
            }
            val price = priceForDuration(request.durationMinutes)
            val id = ReservationsTable.insert {
                it[ReservationsTable.userId] = userId
                it[ReservationsTable.courtId] = request.courtId
                it[ReservationsTable.date] = date
                it[ReservationsTable.startTime] = start
                it[ReservationsTable.endTime] = end
                it[ReservationsTable.durationMinutes] = request.durationMinutes
                it[ReservationsTable.priceRon] = price
            } get ReservationsTable.id
            toDto(id, request.courtId, date, start, end, request.durationMinutes, price)
        }
    }

    fun adminReservations(dateStr: String, courtId: String?): List<AdminReservationDto>? {
        val date = runCatching { LocalDate.parse(dateStr, DATE_FMT) }.getOrNull() ?: return null
        return adminReservationsInRange(dateStr, dateStr, courtId)
    }

    fun adminReservationsInRange(fromStr: String, toStr: String, courtId: String?): List<AdminReservationDto>? {
        val from = runCatching { LocalDate.parse(fromStr, DATE_FMT) }.getOrNull() ?: return null
        val to = runCatching { LocalDate.parse(toStr, DATE_FMT) }.getOrNull() ?: return null
        if (to.isBefore(from)) return null
        if (!courtId.isNullOrBlank() && !courtExists(courtId)) return null
        return transaction {
            val rows = ReservationsTable.selectAll()
                .where {
                    val byRange = (ReservationsTable.date greaterEq from) and (ReservationsTable.date lessEq to)
                    if (courtId.isNullOrBlank()) byRange
                    else byRange and (ReservationsTable.courtId eq courtId)
                }
                .orderBy(
                    ReservationsTable.date to SortOrder.ASC,
                    ReservationsTable.startTime to SortOrder.ASC
                )
            rows.map { row -> toAdminDto(row) }
        }
    }

    private fun toAdminDto(row: ResultRow): AdminReservationDto {
        val user = UsersTable.selectAll()
            .first { it[UsersTable.id] == row[ReservationsTable.userId] }
        val court = CourtsTable.selectAll()
            .first { it[CourtsTable.id] == row[ReservationsTable.courtId] }
        return AdminReservationDto(
            id = row[ReservationsTable.id],
            courtId = row[ReservationsTable.courtId],
            courtNameRo = court[CourtsTable.nameRo],
            courtNameEn = court[CourtsTable.nameEn],
            date = row[ReservationsTable.date].format(DATE_FMT),
            startTime = row[ReservationsTable.startTime].format(TIME_FMT),
            endTime = row[ReservationsTable.endTime].format(TIME_FMT),
            durationMinutes = row[ReservationsTable.durationMinutes],
            status = "confirmed",
            clientName = user[UsersTable.displayName],
            clientPhone = user[UsersTable.phone],
            clientEmail = user[UsersTable.email]
        )
    }

    fun userReservations(userId: Long): List<ReservationDto> = transaction {
        ReservationsTable.selectAll()
            .where { ReservationsTable.userId eq userId }
            .orderBy(ReservationsTable.date to SortOrder.DESC, ReservationsTable.startTime to SortOrder.DESC)
            .map { row ->
                toDto(
                    id = row[ReservationsTable.id],
                    courtId = row[ReservationsTable.courtId],
                    date = row[ReservationsTable.date],
                    start = row[ReservationsTable.startTime],
                    end = row[ReservationsTable.endTime],
                    duration = row[ReservationsTable.durationMinutes],
                    price = row[ReservationsTable.priceRon]
                )
            }
    }

    private fun courtExists(courtId: String): Boolean = transaction {
        CourtsTable.selectAll().any { it[CourtsTable.id] == courtId }
    }

    private data class BookingInterval(val start: LocalTime, val end: LocalTime)

    private fun loadBookings(courtId: String, date: LocalDate): List<BookingInterval> = transaction {
        ReservationsTable.selectAll()
            .where { (ReservationsTable.courtId eq courtId) and (ReservationsTable.date eq date) }
            .map { BookingInterval(it[ReservationsTable.startTime], it[ReservationsTable.endTime]) }
    }

    private fun overlapsAny(bookings: List<BookingInterval>, start: LocalTime, end: LocalTime): Boolean =
        bookings.any { booking ->
            start.isBefore(booking.end) && end.isAfter(booking.start)
        }

    private fun priceForDuration(minutes: Int): Int = when (minutes) {
        60 -> 80
        90 -> 120
        120 -> 160
        else -> throw IllegalArgumentException("Invalid duration")
    }

    private fun toDto(
        id: Long,
        courtId: String,
        date: LocalDate,
        start: LocalTime,
        end: LocalTime,
        duration: Int,
        price: Int
    ): ReservationDto {
        val court = transaction {
            CourtsTable.selectAll().first { it[CourtsTable.id] == courtId }
        }
        return ReservationDto(
            id = id,
            courtId = courtId,
            courtNameRo = court[CourtsTable.nameRo],
            courtNameEn = court[CourtsTable.nameEn],
            date = date.format(DATE_FMT),
            startTime = start.format(TIME_FMT),
            endTime = end.format(TIME_FMT),
            durationMinutes = duration,
            priceRon = price,
            surfaceRo = court[CourtsTable.surfaceRo],
            surfaceEn = court[CourtsTable.surfaceEn]
        )
    }
}
