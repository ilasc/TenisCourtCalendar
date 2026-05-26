package com.apulum.tenis.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apulum.tenis.data.api.ReservationDto
import com.apulum.tenis.data.repository.TenisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class ReservationsUiState(
    val isLoading: Boolean = true,
    val items: List<ReservationDto> = emptyList(),
    val error: Boolean = false,
    val deletingId: Long? = null
)

class ReservationsViewModel(
    private val repository: TenisRepository,
    private val token: String,
    private val isRomanian: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationsUiState())
    val uiState: StateFlow<ReservationsUiState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val clubZone = ZoneId.of("Europe/Bucharest")

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ReservationsUiState(isLoading = true)
            repository.getReservations(token)
                .onSuccess { list ->
                    val upcoming = list
                        .filter { it.isUpcoming() }
                        .sortedWith(compareBy({ it.date }, { it.startTime }))
                    _uiState.value = ReservationsUiState(isLoading = false, items = upcoming)
                }
                .onFailure {
                    _uiState.value = ReservationsUiState(isLoading = false, error = true)
                }
        }
    }

    fun courtName(item: ReservationDto): String =
        if (isRomanian) item.courtNameRo else item.courtNameEn

    fun deleteReservation(reservationId: Long, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(deletingId = reservationId, error = false)
            repository.deleteReservation(token, reservationId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        deletingId = null,
                        items = _uiState.value.items.filter { it.id != reservationId }
                    )
                    onDeleted()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(deletingId = null, error = true)
                }
        }
    }

    private fun ReservationDto.isUpcoming(): Boolean {
        val date = runCatching { LocalDate.parse(date) }.getOrNull() ?: return false
        val end = runCatching {
            LocalTime.parse(endTime.trim().take(5), timeFormatter)
        }.getOrNull() ?: return false
        val endAt = LocalDateTime.of(date, end)
        return endAt.isAfter(LocalDateTime.now(clubZone))
    }
}
