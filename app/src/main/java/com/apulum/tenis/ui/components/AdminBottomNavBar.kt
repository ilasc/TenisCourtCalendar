package com.apulum.tenis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apulum.tenis.R
import com.apulum.tenis.ui.navigation.AdminTab
import com.apulum.tenis.ui.theme.AdminAccentGreen
import com.apulum.tenis.ui.theme.AdminNavInactive
import com.apulum.tenis.ui.theme.ApulumBackground

@Composable
fun AdminBottomNavBar(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ApulumBackground)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = androidx.compose.ui.graphics.Color(0xFFEDF0ED))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdminNavItem(
                icon = Icons.Outlined.Home,
                label = stringResource(R.string.admin_nav_dashboard),
                selected = selectedTab == AdminTab.Dashboard,
                onClick = { onTabSelected(AdminTab.Dashboard) },
                modifier = Modifier.weight(1f)
            )
            AdminNavItem(
                icon = Icons.Outlined.CalendarMonth,
                label = stringResource(R.string.admin_nav_reservations),
                selected = selectedTab == AdminTab.Reservations,
                onClick = { onTabSelected(AdminTab.Reservations) },
                modifier = Modifier.weight(1f)
            )
            AdminNavItem(
                icon = Icons.Outlined.Groups,
                label = stringResource(R.string.admin_nav_clients),
                selected = selectedTab == AdminTab.Clients,
                onClick = { onTabSelected(AdminTab.Clients) },
                modifier = Modifier.weight(1f)
            )
            AdminNavItem(
                icon = Icons.Outlined.BarChart,
                label = stringResource(R.string.admin_nav_stats),
                selected = selectedTab == AdminTab.Statistics,
                onClick = { onTabSelected(AdminTab.Statistics) },
                modifier = Modifier.weight(1f)
            )
            AdminNavItem(
                icon = Icons.Outlined.Settings,
                label = stringResource(R.string.admin_nav_settings),
                selected = selectedTab == AdminTab.Settings,
                onClick = { onTabSelected(AdminTab.Settings) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AdminNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (selected) AdminAccentGreen else AdminNavInactive
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = color,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
