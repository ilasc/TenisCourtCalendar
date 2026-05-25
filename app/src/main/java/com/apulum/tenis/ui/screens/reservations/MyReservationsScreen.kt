package com.apulum.tenis.ui.screens.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apulum.tenis.R
import com.apulum.tenis.ui.theme.ApulumBackground
import com.apulum.tenis.ui.theme.ApulumGreen
import com.apulum.tenis.ui.theme.ApulumTextPrimary
import com.apulum.tenis.ui.theme.ApulumTextSecondary
import com.apulum.tenis.ui.viewmodel.ReservationsViewModel

@Composable
fun MyReservationsScreen(viewModel: ReservationsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApulumBackground)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.my_reservations_title),
            fontWeight = FontWeight.Bold,
            color = ApulumGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )
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
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                viewModel.courtName(item),
                                fontWeight = FontWeight.Bold,
                                color = ApulumTextPrimary
                            )
                            Text(
                                "${item.date} • ${item.startTime} – ${item.endTime}",
                                color = ApulumTextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                stringResource(R.string.price_ron, item.priceRon),
                                color = ApulumGreen,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
