package com.apulum.tenis.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apulum.tenis.R
import com.apulum.tenis.data.api.AdminReservationDto
import com.apulum.tenis.ui.components.CalendarPickerCard
import com.apulum.tenis.ui.components.DateCard
import com.apulum.tenis.ui.theme.AdminAccentGreen
import com.apulum.tenis.ui.theme.AdminCardClayTint
import com.apulum.tenis.ui.theme.AdminCardGreenTint
import com.apulum.tenis.ui.theme.AdminClayAccent
import com.apulum.tenis.ui.theme.AdminGreenBright
import com.apulum.tenis.ui.theme.AdminGreenDeep
import com.apulum.tenis.ui.theme.AdminGreenMid
import com.apulum.tenis.ui.theme.AdminHeaderSubtitle
import com.apulum.tenis.ui.theme.AdminInfoStripBg
import com.apulum.tenis.ui.theme.AdminStatusConfirmedBg
import com.apulum.tenis.ui.theme.AdminStatusPendingBg
import com.apulum.tenis.ui.theme.AdminStatusPendingText
import com.apulum.tenis.ui.theme.ApulumBackground
import com.apulum.tenis.ui.theme.ApulumOrange
import com.apulum.tenis.ui.theme.ApulumTextPrimary
import com.apulum.tenis.ui.theme.ApulumTextSecondary
import com.apulum.tenis.ui.theme.apulumStatusBarPadding
import com.apulum.tenis.ui.viewmodel.AdminDashboardViewModel
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel,
    displayName: String,
    bottomBarPadding: PaddingValues,
    isRo: Boolean
) {
    val state by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showCourtMenu by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ApulumBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AdminHeader(displayName = displayName)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(y = (-24).dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                        .background(Color.White)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(
                            top = 20.dp,
                            bottom = bottomBarPadding.calculateBottomPadding() + 16.dp
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.admin_reservations_today),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ApulumTextPrimary
                        )
                        Row(
                            modifier = Modifier.clickable { viewModel.refresh() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = AdminAccentGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.admin_refresh),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AdminAccentGreen,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            modifier = Modifier.weight(1.4f),
                            icon = Icons.Outlined.CalendarMonth,
                            label = viewModel.formattedSelectedDate(),
                            onClick = { showDatePicker = true }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            FilterChip(
                                icon = Icons.Outlined.FilterList,
                                label = viewModel.courtFilterLabel(isRo),
                                onClick = { showCourtMenu = true }
                            )
                            DropdownMenu(
                                expanded = showCourtMenu,
                                onDismissRequest = { showCourtMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.admin_filter_all)) },
                                    onClick = {
                                        viewModel.setCourtFilter(null)
                                        showCourtMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.court_1)) },
                                    onClick = {
                                        viewModel.setCourtFilter(AdminDashboardViewModel.COURT_1)
                                        showCourtMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.court_2)) },
                                    onClick = {
                                        viewModel.setCourtFilter(AdminDashboardViewModel.COURT_2)
                                        showCourtMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.court_indoor)) },
                                    onClick = {
                                        viewModel.setCourtFilter(AdminDashboardViewModel.COURT_INDOOR)
                                        showCourtMenu = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.admin_date_hint),
                        fontSize = 12.sp,
                        color = ApulumTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.dateOptions, key = { it.date.toString() }) { option ->
                            DateCard(
                                dayAbbrev = option.dayAbbrev,
                                dayNumber = option.dayNumber,
                                monthName = option.monthName,
                                isSelected = option.date == state.selectedDate,
                                hasBookings = option.hasReservations,
                                onClick = { viewModel.selectDate(option.date) }
                            )
                        }
                        item {
                            CalendarPickerCard(onClick = { showDatePicker = true })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    when {
                        state.isLoading -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AdminAccentGreen)
                        }
                        state.error -> Text(
                            text = stringResource(R.string.error_generic),
                            color = ApulumTextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                        else -> {
                            val court1 = viewModel.reservationsForCourt(AdminDashboardViewModel.COURT_1)
                            val court2 = viewModel.reservationsForCourt(AdminDashboardViewModel.COURT_2)
                            val indoor = viewModel.reservationsForCourt(AdminDashboardViewModel.COURT_INDOOR)
                            if (state.courtFilter == null || state.courtFilter == AdminDashboardViewModel.COURT_1) {
                                CourtReservationCard(
                                    title = stringResource(R.string.admin_court_1_full),
                                    count = court1.size,
                                    reservations = court1,
                                    isOutdoor = true,
                                    isRo = isRo
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                            if (state.courtFilter == null || state.courtFilter == AdminDashboardViewModel.COURT_2) {
                                CourtReservationCard(
                                    title = stringResource(R.string.admin_court_2_full),
                                    count = court2.size,
                                    reservations = court2,
                                    isOutdoor = true,
                                    isRo = isRo
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                            if (state.courtFilter == null || state.courtFilter == AdminDashboardViewModel.COURT_INDOOR) {
                                CourtReservationCard(
                                    title = stringResource(R.string.admin_court_indoor_full),
                                    count = indoor.size,
                                    reservations = indoor,
                                    isOutdoor = false,
                                    isRo = isRo
                                )
                            }
                            if (court1.isEmpty() && court2.isEmpty() && indoor.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.admin_no_reservations),
                                    color = ApulumTextSecondary,
                                    modifier = Modifier.padding(vertical = 24.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoStrip()
                }
            }
        }
    }
}

@Composable
private fun AdminHeader(displayName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(AdminGreenDeep, AdminGreenMid, AdminGreenBright)
                )
            )
            .apulumStatusBarPadding()
            .padding(bottom = 36.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "APULUM",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White,
                        letterSpacing = 3.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(
                            modifier = Modifier.width(24.dp),
                            thickness = 2.dp,
                            color = ApulumOrange
                        )
                        Text(
                            text = "TENIS",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ApulumOrange,
                            letterSpacing = 1.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier.width(24.dp),
                            thickness = 2.dp,
                            color = ApulumOrange
                        )
                    }
                }
                Box {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 10.dp)
                            .size(18.dp)
                            .background(AdminAccentGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("2", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = stringResource(R.string.greeting, displayName),
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.admin_panel_subtitle),
                    fontSize = 14.sp,
                    color = AdminHeaderSubtitle,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE1E7E2), RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = AdminAccentGreen, modifier = Modifier.size(16.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = ApulumTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f, fill = false)
        )
    }
}

@Composable
private fun CourtReservationCard(
    title: String,
    count: Int,
    reservations: List<AdminReservationDto>,
    isOutdoor: Boolean,
    isRo: Boolean
) {
    val headerTint = if (isOutdoor) AdminCardGreenTint else AdminCardClayTint
    val accent = if (isOutdoor) AdminAccentGreen else AdminClayAccent
    val tableHeaderBg = if (isOutdoor) Color(0xFFF3F7F4) else Color(0xFFF6F1EA)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp), clip = false)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFEEF1EE), RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            if (isOutdoor) Color(0xFFF2FBF5) else Color(0xFFFFF8F1),
                            Color.White
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(headerTint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isOutdoor) Icons.Outlined.WbSunny else Icons.Outlined.Home,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isOutdoor) Color(0xFF064A2A) else ApulumTextPrimary,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            )
            Box(
                modifier = Modifier
                    .background(headerTint, RoundedCornerShape(7.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_res_count, count),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(tableHeaderBg)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            HeaderCell(stringResource(R.string.admin_col_time), Modifier.width(72.dp))
            HeaderCell(stringResource(R.string.admin_col_client), Modifier.weight(1.1f))
            HeaderCell(stringResource(R.string.admin_col_contact), Modifier.weight(1f))
            HeaderCell(stringResource(R.string.admin_col_duration), Modifier.width(44.dp))
            HeaderCell(stringResource(R.string.admin_col_status), Modifier.width(56.dp))
        }
        if (reservations.isEmpty()) {
            Text(
                text = stringResource(R.string.admin_no_reservations),
                fontSize = 13.sp,
                color = ApulumTextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            reservations.forEachIndexed { index, res ->
                if (index > 0) {
                    HorizontalDivider(color = Color(0xFFE7ECE8))
                }
                ReservationRow(reservation = res, isRo = isRo)
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = ApulumTextPrimary,
        modifier = modifier
    )
}

@Composable
private fun ReservationRow(reservation: AdminReservationDto, isRo: Boolean) {
    val confirmed = reservation.status == "confirmed"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${reservation.startTime}–${reservation.endTime}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AdminAccentGreen,
            modifier = Modifier.width(72.dp)
        )
        Text(
            text = reservation.clientName,
            fontSize = 12.sp,
            color = ApulumTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.1f)
        )
        Text(
            text = reservation.clientPhone ?: reservation.clientEmail,
            fontSize = 11.sp,
            color = ApulumTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatDuration(reservation.durationMinutes, isRo),
            fontSize = 11.sp,
            color = ApulumTextPrimary,
            modifier = Modifier.width(44.dp)
        )
        Box(
            modifier = Modifier
                .width(56.dp)
                .background(
                    if (confirmed) AdminStatusConfirmedBg else AdminStatusPendingBg,
                    RoundedCornerShape(6.dp)
                )
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    if (confirmed) R.string.admin_status_confirmed else R.string.admin_status_pending
                ),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (confirmed) AdminAccentGreen else AdminStatusPendingText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InfoStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(9.dp))
            .background(AdminInfoStripBg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .border(1.5.dp, AdminAccentGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("i", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AdminAccentGreen)
            }
            Text(
                text = stringResource(R.string.admin_timezone_info),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminAccentGreen,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Text(
            text = stringResource(R.string.admin_updated_ago),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = ApulumTextSecondary
        )
    }
}

private fun formatDuration(minutes: Int, isRo: Boolean): String = when (minutes) {
    60 -> if (isRo) "1 oră" else "1 h"
    90 -> if (isRo) "1 oră 30" else "1.5 h"
    120 -> if (isRo) "2 ore" else "2 h"
    else -> "$minutes min"
}
