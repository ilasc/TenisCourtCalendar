package com.apulum.tenis.ui.screens.reservations

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apulum.tenis.R
import com.apulum.tenis.data.api.ReservationDto
import com.apulum.tenis.ui.components.courtDrawableRes
import com.apulum.tenis.ui.theme.AdminInfoStripBg
import com.apulum.tenis.ui.theme.ApulumBackground
import com.apulum.tenis.ui.theme.ApulumGreen
import com.apulum.tenis.ui.theme.ApulumSummaryBorder
import com.apulum.tenis.ui.theme.ApulumTextPrimary
import com.apulum.tenis.ui.theme.ApulumTextSecondary
import com.apulum.tenis.ui.theme.apulumStatusBarPadding
import com.apulum.tenis.ui.viewmodel.ReservationsViewModel

@Composable
fun MyReservationsScreen(
    viewModel: ReservationsViewModel,
    onReservationDeleted: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var reservationToDelete by remember { mutableStateOf<ReservationDto?>(null) }

    reservationToDelete?.let { reservation ->
        val scheme = MaterialTheme.colorScheme
        AlertDialog(
            onDismissRequest = { reservationToDelete = null },
            title = { Text(stringResource(R.string.cancel_booking_title)) },
            text = { Text(stringResource(R.string.cancel_booking_message)) },
            dismissButton = {
                TextButton(onClick = { reservationToDelete = null }) {
                    Text(
                        text = stringResource(R.string.keep_booking),
                        color = ApulumGreen
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteReservation(reservation.id, onReservationDeleted)
                        reservationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scheme.errorContainer,
                        contentColor = scheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(R.string.cancel_booking_confirm))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApulumBackground)
            .apulumStatusBarPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 12.dp)
    ) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ApulumGreen)
            }
            state.error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.error_generic))
                TextButton(onClick = viewModel::load) {
                    Text(stringResource(R.string.retry))
                }
            }
            state.items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_reservations), color = ApulumTextSecondary)
            }
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 4.dp)
            ) {
                items(state.items, key = { it.id }) { item ->
                    MyBookingCard(
                        item = item,
                        courtName = viewModel.courtName(item),
                        dateLine = viewModel.formattedDate(item),
                        timeLine = viewModel.formattedTimeRange(item),
                        isDeleting = state.deletingId == item.id,
                        onCancelClick = { reservationToDelete = item }
                    )
                }
            }
        }
    }
}

@Composable
private fun MyBookingCard(
    item: ReservationDto,
    courtName: String,
    dateLine: String,
    timeLine: String,
    isDeleting: Boolean,
    onCancelClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    val scheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ApulumSummaryBorder, cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = AdminInfoStripBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateLine,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ApulumTextPrimary
                )
                Text(
                    text = timeLine,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApulumTextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = courtName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApulumGreen,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.price_ron, item.priceRon),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ApulumGreen
                )
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(20.dp),
                        color = scheme.onErrorContainer,
                        strokeWidth = 2.dp
                    )
                } else {
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.padding(top = 4.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = scheme.errorContainer,
                            contentColor = scheme.onErrorContainer
                        ),
                        border = BorderStroke(1.dp, scheme.error.copy(alpha = 0.35f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " " + stringResource(R.string.delete_booking),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Image(
                painter = painterResource(courtDrawableRes(item.courtId)),
                contentDescription = courtName,
                modifier = Modifier
                    .width(72.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
