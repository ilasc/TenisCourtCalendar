package com.apulum.tenis.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apulum.tenis.ui.theme.ApulumGreen
import com.apulum.tenis.ui.theme.ApulumOrange

@Composable
fun ApulumLogo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "APULUM",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = ApulumGreen,
            letterSpacing = 2.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier.width(28.dp),
                thickness = 2.dp,
                color = ApulumOrange
            )
            Text(
                text = "TENIS",
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = ApulumOrange,
                letterSpacing = 1.sp
            )
            HorizontalDivider(
                modifier = Modifier.width(28.dp),
                thickness = 2.dp,
                color = ApulumOrange
            )
        }
    }
}

@Composable
fun ApulumLogoLarge(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ApulumLogo()
        Text(
            text = "Clubul Sportiv Apulum Tenis",
            fontSize = 12.sp,
            color = ApulumOrange,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
