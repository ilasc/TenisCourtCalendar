package com.apulum.tenis.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apulum.tenis.data.api.ReservationDto
import com.apulum.tenis.data.repository.TenisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReservationsUiState(
    val isLoading: Boolean = true,
    val items: List<ReservationDto> = emptyList(),
    val error: Boolean = false
)

class ReservationsViewModel(
    private val repository: TenisRepository,
    private val token: String,
    private val isRomanian: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationsUiState())
    val uiState: StateFlow<ReservationsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ReservationsUiState(isLoading = true)
            repository.getReservations(token)
                .onSuccess { list ->
                    _uiState.value = ReservationsUiState(isLoading = false, items = list)
                }
                .onFailure {
                    _uiState.value = ReservationsUiState(isLoading = false, error = true)
                }
        }
    }

    fun courtName(item: ReservationDto): String =
        if (isRomanian) item.courtNameRo else item.courtNameEn
}
