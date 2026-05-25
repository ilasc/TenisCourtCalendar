package com.apulum.tenis.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apulum.tenis.data.api.AdminReservationDto
import com.apulum.tenis.data.repository.TenisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class AdminDateOption(
    val date: LocalDate,
    val dayAbbrev: String,
    val dayNumber: String,
    val monthName: String,
    val hasReservations: Boolean
)

data class AdminDashboardUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val dateOptions: List<AdminDateOption> = emptyList(),
    val courtFilter: String? = null,
    val reservations: List<AdminReservationDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: Boolean = false
)

class AdminDashboardViewModel(
    private val repository: TenisRepository,
    private val token: String,
    private val locale: Locale
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val displayDateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", locale)
    private val rangeDays = 14L

    init {
        load()
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun setCourtFilter(courtId: String?) {
        _uiState.value = _uiState.value.copy(courtFilter = courtId)
        load()
    }

    fun refresh() = load()

    fun formattedSelectedDate(): String =
        _uiState.value.selectedDate.format(displayDateFormatter)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

    fun courtFilterLabel(isRo: Boolean): String = when (_uiState.value.courtFilter) {
        COURT_OUTDOOR -> if (isRo) "Exterior" else "Outdoor"
        COURT_INDOOR -> if (isRo) "Acoperit" else "Indoor"
        else -> if (isRo) "Toate" else "All"
    }

    fun reservationsForCourt(courtId: String): List<AdminReservationDto> {
        val dateStr = _uiState.value.selectedDate.format(dateFormatter)
        return _uiState.value.reservations
            .filter { it.courtId == courtId && it.date == dateStr }
            .sortedBy { it.startTime }
    }

    private fun load() {
        viewModelScope.launch {
            val current = _uiState.value
            _uiState.value = current.copy(isLoading = true, error = false)
            val from = LocalDate.now().format(dateFormatter)
            val to = LocalDate.now().plusDays(rangeDays).format(dateFormatter)
            val result = repository.getAdminReservations(
                token = token,
                from = from,
                to = to,
                courtId = current.courtFilter
            )
            _uiState.value = if (result.isSuccess) {
                val list = result.getOrElse { emptyList() }
                current.copy(
                    reservations = list,
                    dateOptions = buildDateOptions(list),
                    isLoading = false,
                    error = false
                )
            } else {
                current.copy(isLoading = false, error = true)
            }
        }
    }

    private fun buildDateOptions(reservations: List<AdminReservationDto>): List<AdminDateOption> {
        val formatterDay = DateTimeFormatter.ofPattern("EEE", locale)
        val formatterMonth = DateTimeFormatter.ofPattern("MMM", locale)
        val today = LocalDate.now()
        return (0..rangeDays.toInt()).map { offset ->
            val date = today.plusDays(offset.toLong())
            val dateStr = date.format(dateFormatter)
            AdminDateOption(
                date = date,
                dayAbbrev = formatterDay.format(date).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                },
                dayNumber = date.dayOfMonth.toString(),
                monthName = formatterMonth.format(date).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                },
                hasReservations = reservations.any { it.date == dateStr }
            )
        }
    }

    companion object {
        const val COURT_OUTDOOR = "exterior"
        const val COURT_INDOOR = "acoperit"
    }
}
