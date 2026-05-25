package com.apulum.tenis.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apulum.tenis.R
import com.apulum.tenis.data.api.CourtDto
import com.apulum.tenis.ui.theme.ApulumBorder
import com.apulum.tenis.ui.theme.ApulumGreen
import com.apulum.tenis.ui.theme.ApulumSlotUnavailable
import com.apulum.tenis.ui.theme.ApulumTextPrimary
import com.apulum.tenis.ui.theme.ApulumTextSecondary

@Composable
fun CourtCard(
    court: CourtDto,
    courtName: String,
    isSelected: Boolean,
    isOutdoor: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) ApulumGreen else ApulumBorder
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val courtPhoto = courtDrawableRes(court.id)
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(22.dp)
                        .background(ApulumGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isOutdoor) Icons.Default.WbSunny else Icons.Outlined.Warehouse,
                        contentDescription = null,
                        tint = ApulumTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = courtName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ApulumTextPrimary
                    )
                }
                Image(
                    painter = painterResource(courtPhoto),
                    contentDescription = courtName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(108.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun DateCard(
    dayAbbrev: String,
    dayNumber: String,
    monthName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasBookings: Boolean = false
) {
    val borderColor = if (isSelected) ApulumGreen else ApulumBorder
    val textColor = if (isSelected) ApulumGreen else ApulumTextPrimary
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    dayAbbrev,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    dayNumber,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    monthName,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = textColor
                )
            }
            if (hasBookings) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(7.dp)
                        .background(ApulumGreen, CircleShape)
                )
            }
        }
    }
}

@Composable
fun CalendarPickerCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ApulumBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = stringResource(R.string.pick_date_calendar),
                tint = ApulumTextPrimary,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = stringResource(R.string.pick_date_calendar),
                fontSize = 10.sp,
                lineHeight = 12.sp,
                textAlign = TextAlign.Center,
                color = ApulumTextSecondary,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
fun TimeSlotChip(
    time: String,
    available: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val background = when {
        selected -> ApulumGreen
        !available -> ApulumSlotUnavailable
        else -> Color.White
    }
    val textColor = when {
        selected -> Color.White
        !available -> Color.White.copy(alpha = 0.9f)
        else -> ApulumGreen
    }
    val border = when {
        selected || !available -> null
        else -> BorderStroke(1.5.dp, ApulumGreen)
    }
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .then(
                if (border != null) Modifier.border(border, shape) else Modifier
            )
            .background(background)
            .clickable(enabled = available, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            color = textColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun AvailabilityLegend(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(width = 18.dp, height = 14.dp)
                .border(1.5.dp, ApulumGreen, RoundedCornerShape(3.dp))
        )
        Text(
            text = stringResource(R.string.available),
            fontSize = 11.sp,
            color = ApulumTextSecondary,
            modifier = Modifier.padding(start = 4.dp, end = 12.dp)
        )
        Box(
            modifier = Modifier
                .size(width = 18.dp, height = 14.dp)
                .background(ApulumSlotUnavailable, RoundedCornerShape(3.dp))
        )
        Text(
            text = stringResource(R.string.unavailable),
            fontSize = 11.sp,
            color = ApulumTextSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun DurationChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) ApulumGreen else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) ApulumGreen else ApulumBorder,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else ApulumTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
