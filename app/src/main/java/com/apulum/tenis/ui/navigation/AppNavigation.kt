package com.apulum.tenis.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apulum.tenis.ApulumTenisApp
import com.apulum.tenis.data.local.UserSession
import com.apulum.tenis.data.model.UserRole
import com.apulum.tenis.ui.components.AdminBottomNavBar
import com.apulum.tenis.ui.components.ApulumBottomNavBar
import com.apulum.tenis.ui.components.ApulumSnackbarHost
import com.apulum.tenis.ui.theme.apulumTopSafeAreaPadding
import com.apulum.tenis.ui.screens.admin.AdminDashboardScreen
import com.apulum.tenis.ui.screens.booking.BookingScreen
import com.apulum.tenis.ui.screens.favorites.FavoritesScreen
import com.apulum.tenis.ui.screens.login.LoginScreen
import com.apulum.tenis.ui.screens.profile.ProfileScreen
import com.apulum.tenis.ui.screens.reservations.MyReservationsScreen
import com.apulum.tenis.ui.viewmodel.AdminDashboardViewModel
import com.apulum.tenis.ui.viewmodel.BookingViewModel
import com.apulum.tenis.ui.viewmodel.LoginViewModel
import com.apulum.tenis.ui.viewmodel.ReservationsViewModel
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apulum.tenis.R
import kotlinx.coroutines.launch
import java.util.Locale

enum class MainTab { Home, Reservations, Favorites, Profile }

enum class AdminTab { Dashboard, Reservations, Clients, Statistics, Settings }

object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as ApulumTenisApp
    val session by app.sessionStore.sessionFlow.collectAsState(initial = null)

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            val loginVm = viewModel { LoginViewModel(app.repository) }
            LoginScreen(
                viewModel = loginVm,
                onLoggedIn = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MAIN) {
            val current = session
            if (current == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            } else if (current.role == UserRole.ADMIN) {
                AdminShell(session = current, onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                })
            } else {
                MainShell(session = current, onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                })
            }
        }
    }

    LaunchedEffect(session) {
        if (session != null && navController.currentDestination?.route == Routes.LOGIN) {
            navController.navigate(Routes.MAIN) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }
}

@Composable
private fun MainShell(session: UserSession, onLoggedOut: () -> Unit) {
    val app = LocalContext.current.applicationContext as ApulumTenisApp
    var selectedTab by remember { mutableStateOf(MainTab.Home) }
    var homeRefreshKey by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val locale = Locale.getDefault()
    val isRo = locale.language == "ro"

    val bookingVm = viewModel {
        BookingViewModel(app.repository, session, locale)
    }
    val reservationsVm = viewModel {
        ReservationsViewModel(app.repository, session.token, isRo)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ApulumBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    if (tab == MainTab.Home) homeRefreshKey++
                }
            )
        }
    ) { padding ->
        val bottomPadding = PaddingValues(bottom = padding.calculateBottomPadding())
        Box(Modifier.fillMaxSize()) {
            when (selectedTab) {
            MainTab.Home -> BookingScreen(
                viewModel = bookingVm,
                bottomBarPadding = bottomPadding,
                availabilityRefreshKey = homeRefreshKey,
                onReservationConfirmed = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = app.getString(R.string.reservation_confirmed),
                            duration = SnackbarDuration.Short
                        )
                    }
                    reservationsVm.load()
                }
            )
            MainTab.Reservations -> Box(Modifier.padding(padding)) {
                MyReservationsScreen(
                    viewModel = reservationsVm,
                    onReservationDeleted = { bookingVm.refreshAvailability() }
                )
            }
            MainTab.Favorites -> FavoritesScreen()
            MainTab.Profile -> ProfileScreen(
                displayName = session.displayName,
                email = session.email,
                onLogout = {
                    scope.launch {
                        app.repository.logout()
                        onLoggedOut()
                    }
                }
            )
            }
            ApulumSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .zIndex(10f)
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .apulumTopSafeAreaPadding(extra = 8.dp)
            )
        }
    }
}

@Composable
private fun AdminShell(session: UserSession, onLoggedOut: () -> Unit) {
    val app = LocalContext.current.applicationContext as ApulumTenisApp
    var selectedTab by remember { mutableStateOf(AdminTab.Reservations) }
    val locale = Locale.getDefault()
    val isRo = locale.language == "ro"
    val scope = rememberCoroutineScope()

    val adminVm = viewModel {
        AdminDashboardViewModel(app.repository, session.token, locale)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AdminBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        val bottomPadding = PaddingValues(bottom = padding.calculateBottomPadding())
        when (selectedTab) {
            AdminTab.Reservations -> AdminDashboardScreen(
                viewModel = adminVm,
                displayName = session.displayName,
                bottomBarPadding = bottomPadding,
                isRo = isRo
            )
            AdminTab.Dashboard -> AdminPlaceholderScreen(
                title = stringResource(R.string.admin_nav_dashboard),
                bottomBarPadding = bottomPadding
            )
            AdminTab.Clients -> AdminPlaceholderScreen(
                title = stringResource(R.string.admin_nav_clients),
                bottomBarPadding = bottomPadding
            )
            AdminTab.Statistics -> AdminPlaceholderScreen(
                title = stringResource(R.string.admin_nav_stats),
                bottomBarPadding = bottomPadding
            )
            AdminTab.Settings -> Box(Modifier.padding(bottom = bottomPadding.calculateBottomPadding())) {
                ProfileScreen(
                    displayName = session.displayName,
                    email = session.email,
                    onLogout = {
                        scope.launch {
                            app.repository.logout()
                            onLoggedOut()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminPlaceholderScreen(title: String, bottomBarPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomBarPadding.calculateBottomPadding()),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.admin_placeholder, title))
    }
}
