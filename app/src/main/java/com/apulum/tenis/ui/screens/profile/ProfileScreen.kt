package com.apulum.tenis.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apulum.tenis.R
import com.apulum.tenis.ui.components.ApulumLogoLarge
import com.apulum.tenis.ui.theme.ApulumBackground
import com.apulum.tenis.ui.theme.ApulumGreen
import com.apulum.tenis.ui.theme.ApulumTextPrimary

@Composable
fun ProfileScreen(
    displayName: String,
    email: String,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApulumBackground)
            .padding(24.dp)
    ) {
        ApulumLogoLarge()
        Text(
            text = displayName,
            fontWeight = FontWeight.Bold,
            color = ApulumGreen,
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            text = stringResource(R.string.profile_email, email),
            color = ApulumTextPrimary,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = ApulumGreen)
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}
