package com.apulum.tenis.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apulum.tenis.data.api.CourtDto
import com.apulum.tenis.data.api.CreateReservationRequest
import com.apulum.tenis.data.api.TimeSlotDto
import com.apulum.tenis.data.local.UserSession
import com.apulum.tenis.data.repository.TenisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DateOption(
    val date: LocalDate,
    val dayAbbrev: String,
    val dayNumber: String,
    val monthName: String
)

data class BookingUiState(
    val isLoading: Boolean = true,
    val courts: List<CourtDto> = emptyList(),
    val selectedCourtId: String? = null,
    val dateOptions: List<DateOption> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val durationMinutes: Int = 60,
    val slots: List<TimeSlotDto> = emptyList(),
    val selectedTime: String? = null,
    val priceRon: Int = 80,
    val isSubmitting: Boolean = false,
    val bookingSuccess: Boolean = false,
    val error: String? = null
)

class BookingViewModel(
    private val repository: TenisRepository,
    private val session: UserSession,
    private val locale: Locale
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    val displayName: String = session.displayName
    val token: String = session.token

    init {
        buildDateOptions()
        loadCourts()
    }

    private fun buildDateOptions() {
        val formatterDay = DateTimeFormatter.ofPattern("EEE", locale)
        val formatterMonth = DateTimeFormatter.ofPattern("MMM", locale)
        val today = LocalDate.now()
        val options = (0..3).map { offset ->
            val date = today.plusDays(offset.toLong())
            DateOption(
                date = date,
                dayAbbrev = formatterDay.format(date).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                },
                dayNumber = date.dayOfMonth.toString(),
                monthName = formatterMonth.format(date).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                }
            )
        }
        _uiState.update { it.copy(dateOptions = options, selectedDate = today) }
    }

    fun loadCourts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getCourts(token)
                .onSuccess { courts ->
                    val defaultCourt = courts.firstOrNull { it.bookable }?.id
                    _uiState.update { state ->
                        val selectedStillValid = state.selectedCourtId != null &&
                            courts.any { court ->
                                court.id == state.selectedCourtId && court.bookable
                            }
                        state.copy(
                            isLoading = false,
                            courts = courts,
                            selectedCourtId = if (selectedStillValid) state.selectedCourtId else defaultCourt
                        )
                    }
                    refreshAvailability()
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, error = "load_failed") }
                }
        }
    }

    fun selectCourt(courtId: String) {
        val court = _uiState.value.courts.find { it.id == courtId } ?: return
        if (!court.bookable) return
        if (_uiState.value.selectedCourtId == courtId) return
        _uiState.update { it.copy(selectedCourtId = courtId, selectedTime = null) }
        refreshAvailability()
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, selectedTime = null) }
        refreshAvailability()
    }

    fun selectDuration(minutes: Int) {
        val price = when (minutes) {
            60 -> 80
            90 -> 120
            120 -> 160
            else -> 80
        }
        _uiState.update {
            it.copy(durationMinutes = minutes, priceRon = price, selectedTime = null)
        }
        refreshAvailability()
    }

    fun selectTime(time: String) {
        val slot = _uiState.value.slots.find { it.time == time }
        if (slot?.available == true) {
            _uiState.update { it.copy(selectedTime = time) }
        }
    }

    fun refreshAvailability() {
        val state = _uiState.value
        val courtId = state.selectedCourtId ?: return
        viewModelScope.launch {
            val dateStr = state.selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            repository.getAvailability(token, courtId, dateStr, state.durationMinutes)
                .onSuccess { response ->
                    _uiState.update {
                        val stillValid = response.slots.any { s ->
                            s.time == it.selectedTime && s.available
                        }
                        it.copy(
                            slots = response.slots,
                            selectedTime = if (stillValid) it.selectedTime else null
                        )
                    }
                }
        }
    }

    fun confirmReservation(onDone: () -> Unit) {
        val state = _uiState.value
        val courtId = state.selectedCourtId ?: return
        val time = state.selectedTime ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            val request = CreateReservationRequest(
                courtId = courtId,
                date = state.selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                startTime = time,
                durationMinutes = state.durationMinutes
            )
            repository.createReservation(token, request)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false, bookingSuccess = true, selectedTime = null) }
                    refreshAvailability()
                    onDone()
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isSubmitting = false, error = "booking_failed")
                    }
                    refreshAvailability()
                }
        }
    }

    fun selectedCourt(): CourtDto? =
        _uiState.value.courts.find { it.id == _uiState.value.selectedCourtId }

    fun formattedSelectedDate(): String {
        val date = _uiState.value.selectedDate
        val day = DateTimeFormatter.ofPattern("EEE", locale).format(date)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        val month = DateTimeFormatter.ofPattern("MMM", locale).format(date)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        return "$day, ${date.dayOfMonth} $month"
    }
}
