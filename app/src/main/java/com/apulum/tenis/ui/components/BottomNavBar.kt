package com.apulum.tenis.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apulum.tenis.R
import com.apulum.tenis.ui.navigation.MainTab
import com.apulum.tenis.ui.theme.ApulumGreen
import com.apulum.tenis.ui.theme.ApulumTextSecondary

private val barHeight = 56.dp

@Composable
fun ApulumBottomNavBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), clip = false)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .padding(bottom = 8.dp, start = 2.dp, end = 2.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            SideNavItem(
                icon = if (selectedTab == MainTab.Home) Icons.Filled.Home else Icons.Outlined.Home,
                label = stringResource(R.string.nav_home),
                selected = selectedTab == MainTab.Home,
                onClick = { onTabSelected(MainTab.Home) },
                modifier = Modifier.weight(1f)
            )
            SideNavItem(
                icon = Icons.Outlined.CalendarMonth,
                label = stringResource(R.string.nav_my_reservations),
                selected = selectedTab == MainTab.Reservations,
                onClick = { onTabSelected(MainTab.Reservations) },
                modifier = Modifier.weight(1f)
            )
            SideNavItem(
                icon = Icons.Outlined.FavoriteBorder,
                label = stringResource(R.string.nav_favorites),
                selected = selectedTab == MainTab.Favorites,
                onClick = { onTabSelected(MainTab.Favorites) },
                modifier = Modifier.weight(1f)
            )
            SideNavItem(
                icon = Icons.Outlined.Person,
                label = stringResource(R.string.nav_profile),
                selected = selectedTab == MainTab.Profile,
                onClick = { onTabSelected(MainTab.Profile) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SideNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) ApulumGreen else ApulumTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (selected) ApulumGreen else ApulumTextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}
