package com.apulum.tenis.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apulum.tenis.R
import com.apulum.tenis.ui.theme.AdminInfoStripBg
import com.apulum.tenis.ui.theme.ApulumBorder
import com.apulum.tenis.ui.theme.ApulumGreen
import com.apulum.tenis.ui.theme.ApulumSlotUnavailable
import com.apulum.tenis.ui.theme.ApulumSummaryBorder
import com.apulum.tenis.ui.theme.ApulumTextPrimary
import com.apulum.tenis.ui.theme.ApulumTextSecondary

@Composable
fun CourtCard(
    courtId: String,
    courtName: String,
    isSelected: Boolean,
    isOutdoor: Boolean,
    enabled: Boolean,
    comingSoonLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val borderColor = when {
        !enabled -> ApulumBorder
        isSelected -> ApulumGreen
        else -> ApulumBorder
    }
    val borderWidth = if (enabled && isSelected) 2.dp else 1.dp
    val cardPadding = if (compact) 6.dp else 12.dp
    val iconSize = if (compact) 14.dp else 18.dp
    val nameSize = if (compact) 11.sp else 13.sp
    val imageHeight = if (compact) 56.dp else 108.dp
    val contentColor = if (enabled) ApulumTextPrimary else ApulumTextSecondary
    val courtPhoto = courtDrawableRes(courtId)
    Card(
        modifier = modifier.then(
            if (enabled) Modifier.clickable(onClick = onClick) else Modifier
        ),
        shape = RoundedCornerShape(if (compact) 12.dp else 14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color.White else Color(0xFFF5F5F5)
        ),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            if (enabled && isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(if (compact) 6.dp else 8.dp)
                        .size(if (compact) 18.dp else 22.dp)
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
            Column(modifier = Modifier.padding(cardPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isOutdoor) Icons.Default.WbSunny else Icons.Outlined.Warehouse,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = courtName,
                        fontWeight = FontWeight.Bold,
                        fontSize = nameSize,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (enabled) {
                    Image(
                        painter = painterResource(courtPhoto),
                        contentDescription = courtName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .height(imageHeight)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .height(imageHeight)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ApulumSlotUnavailable.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comingSoonLabel,
                            fontSize = if (compact) 10.sp else 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ApulumTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
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
    hasBookings: Boolean = false,
    compact: Boolean = false
) {
    val borderColor = if (isSelected) ApulumGreen else ApulumBorder
    val textColor = if (isSelected) ApulumGreen else ApulumTextPrimary
    val cardHeight = if (compact) 72.dp else 96.dp
    Card(
        modifier = modifier
            .height(cardHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = if (compact) 6.dp else 10.dp,
                        horizontal = 4.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    dayAbbrev,
                    fontSize = if (compact) 11.sp else 12.sp,
                    lineHeight = if (compact) 12.sp else 14.sp,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    dayNumber,
                    fontSize = if (compact) 16.sp else 20.sp,
                    lineHeight = if (compact) 18.sp else 22.sp,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = if (compact) 2.dp else 4.dp)
                )
                Text(
                    monthName,
                    fontSize = if (compact) 11.sp else 12.sp,
                    lineHeight = if (compact) 12.sp else 14.sp,
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
fun CalendarPickerCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val cardHeight = if (compact) 72.dp else 96.dp
    Card(
        modifier = modifier
            .height(cardHeight)
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
                modifier = Modifier.size(if (compact) 22.dp else 26.dp)
            )
            Text(
                text = stringResource(R.string.pick_date_calendar),
                fontSize = if (compact) 9.sp else 10.sp,
                lineHeight = if (compact) 10.sp else 12.sp,
                textAlign = TextAlign.Center,
                color = ApulumTextSecondary,
                modifier = Modifier.padding(top = if (compact) 4.dp else 6.dp)
            )
        }
    }
}

@Composable
fun TimeSlotChip(
    time: String,
    available: Boolean,
    occupied: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val shape = RoundedCornerShape(if (compact) 8.dp else 10.dp)
    val chipHeight = if (compact) 32.dp else 44.dp
    val fontSize = if (compact) 11.sp else 14.sp
    val background = when {
        selected -> ApulumGreen
        occupied -> ApulumSlotUnavailable
        available -> AdminInfoStripBg
        else -> Color.White
    }
    val textColor = when {
        selected -> Color.White
        occupied -> Color.White.copy(alpha = 0.9f)
        available -> ApulumGreen
        else -> ApulumTextSecondary
    }
    val border = when {
        selected || occupied -> null
        available -> BorderStroke(1.dp, ApulumSummaryBorder)
        else -> BorderStroke(1.dp, ApulumBorder)
    }
    Box(
        modifier = modifier
            .height(chipHeight)
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
            fontSize = fontSize
        )
    }
}

@Composable
fun AvailabilityLegend(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(width = 18.dp, height = 14.dp)
                .border(1.dp, ApulumSummaryBorder, RoundedCornerShape(3.dp))
                .background(AdminInfoStripBg, RoundedCornerShape(3.dp))
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
