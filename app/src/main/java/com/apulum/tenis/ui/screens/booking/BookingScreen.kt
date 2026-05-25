package com.apulum.tenis.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apulum.tenis.R
import com.apulum.tenis.data.api.CourtDto
import com.apulum.tenis.ui.components.AvailabilityLegend
import com.apulum.tenis.ui.components.CalendarPickerCard
import com.apulum.tenis.ui.components.CourtCard
import com.apulum.tenis.ui.components.DateCard
import com.apulum.tenis.ui.components.DurationChip
import com.apulum.tenis.ui.components.TimeSlotChip
import com.apulum.tenis.ui.theme.ApulumBackground
import com.apulum.tenis.ui.theme.apulumStatusBarPadding
import com.apulum.tenis.ui.theme.ApulumGreen
import com.apulum.tenis.ui.theme.AdminInfoStripBg
import com.apulum.tenis.ui.theme.ApulumSummaryBorder
import com.apulum.tenis.ui.theme.ApulumTextPrimary
import com.apulum.tenis.ui.theme.ApulumTextSecondary
import com.apulum.tenis.ui.viewmodel.BookingViewModel
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    bottomBarPadding: PaddingValues,
    onReservationConfirmed: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val isRo = remember { java.util.Locale.getDefault().language == "ro" }
    val horizontalPadding = 16.dp

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.selectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.selectDate(date)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ApulumBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ApulumGreen)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ApulumBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .apulumStatusBarPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding)
                .padding(bottom = bottomBarPadding.calculateBottomPadding() + 16.dp)
        ) {
            SectionTitle(title = stringResource(R.string.choose_court))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.courts.forEach { court ->
                    val selected = court.id == state.selectedCourtId
                    CourtCard(
                        modifier = Modifier.weight(1f),
                        court = court,
                        courtName = courtDisplayName(court, isRo),
                        isSelected = selected,
                        isOutdoor = court.type == "outdoor",
                        onClick = { viewModel.selectCourt(court.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle(title = stringResource(R.string.choose_date))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                state.dateOptions.forEach { option ->
                    DateCard(
                        modifier = Modifier.weight(1f),
                        dayAbbrev = option.dayAbbrev,
                        dayNumber = option.dayNumber,
                        monthName = option.monthName,
                        isSelected = option.date == state.selectedDate,
                        onClick = { viewModel.selectDate(option.date) }
                    )
                }
                CalendarPickerCard(
                    modifier = Modifier.weight(1f),
                    onClick = { showDatePicker = true }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.choose_time),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ApulumTextPrimary
                )
                AvailabilityLegend()
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DurationChip(
                    label = stringResource(R.string.duration_60),
                    selected = state.durationMinutes == 60,
                    onClick = { viewModel.selectDuration(60) }
                )
                DurationChip(
                    label = stringResource(R.string.duration_90),
                    selected = state.durationMinutes == 90,
                    onClick = { viewModel.selectDuration(90) }
                )
                DurationChip(
                    label = stringResource(R.string.duration_120),
                    selected = state.durationMinutes == 120,
                    onClick = { viewModel.selectDuration(120) }
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(state.slots) { slot ->
                    TimeSlotChip(
                        time = slot.time,
                        available = slot.available,
                        selected = slot.time == state.selectedTime,
                        onClick = { viewModel.selectTime(slot.time) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        ReservationSummaryBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = horizontalPadding)
                .padding(bottom = bottomBarPadding.calculateBottomPadding() + 8.dp),
            courtName = viewModel.selectedCourt()?.let { courtDisplayName(it, isRo) } ?: "—",
            dateLine = viewModel.formattedSelectedDate(),
            time = state.selectedTime ?: "—",
            durationLabel = durationLabel(state.durationMinutes),
            surface = viewModel.selectedCourt()?.let {
                if (isRo) it.surfaceRo else it.surfaceEn
            } ?: "",
            priceRon = state.priceRon,
            enabled = state.selectedTime != null && !state.isSubmitting,
            isSubmitting = state.isSubmitting,
            onContinue = {
                viewModel.confirmReservation(onReservationConfirmed)
            }
        )
    }
}


@Composable
private fun SectionTitle(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ApulumTextPrimary
        )
        subtitle?.let {
            Text(
                text = it,
                fontSize = 13.sp,
                color = ApulumTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun ReservationSummaryBar(
    modifier: Modifier = Modifier,
    courtName: String,
    dateLine: String,
    time: String,
    durationLabel: String,
    surface: String,
    priceRon: Int,
    enabled: Boolean,
    isSubmitting: Boolean,
    onContinue: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, ApulumSummaryBorder, cardShape)
            .background(AdminInfoStripBg, cardShape)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.your_reservation),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ApulumTextPrimary
            )
            Text(
                text = stringResource(
                    R.string.reservation_summary_line,
                    courtName,
                    dateLine,
                    time
                ),
                fontSize = 11.sp,
                color = ApulumTextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = stringResource(R.string.duration_label, durationLabel) +
                    " • " + stringResource(R.string.surface_label, surface),
                fontSize = 11.sp,
                color = ApulumTextSecondary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(R.string.price_ron, priceRon),
                fontWeight = FontWeight.Bold,
                color = ApulumGreen,
                fontSize = 16.sp
            )
            Button(
                onClick = onContinue,
                enabled = enabled,
                modifier = Modifier.padding(top = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ApulumGreen),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.continue_button) + " →",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun courtDisplayName(court: CourtDto, isRo: Boolean): String =
    if (isRo) court.nameRo else court.nameEn

@Composable
private fun durationLabel(minutes: Int): String = when (minutes) {
    60 -> stringResource(R.string.duration_60)
    90 -> stringResource(R.string.duration_90)
    120 -> stringResource(R.string.duration_120)
    else -> stringResource(R.string.duration_60)
}
