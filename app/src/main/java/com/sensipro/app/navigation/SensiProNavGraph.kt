package com.sensipro.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.sensipro.app.R
import com.sensipro.app.ui.MainViewModel
import com.sensipro.app.ui.screens.history.HistoryScreen
import com.sensipro.app.ui.screens.home.HomeScreen
import com.sensipro.app.ui.screens.onboarding.OnboardingAnalyzeScreen
import com.sensipro.app.ui.screens.onboarding.OnboardingInfoScreen
import com.sensipro.app.ui.screens.onboarding.OnboardingPrivacyScreen
import com.sensipro.app.ui.screens.onboarding.OnboardingWelcomeScreen
import com.sensipro.app.ui.screens.result.ResultScreen
import com.sensipro.app.ui.screens.settings.SettingsScreen
import com.sensipro.app.ui.theme.BackgroundDeep
import com.sensipro.app.ui.theme.NeonCyan
import com.sensipro.app.ui.theme.SurfaceGlassElevated
import com.sensipro.app.ui.theme.TextSecondary

private data class BottomNavItem(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, R.string.nav_home, Icons.Filled.Home),
    BottomNavItem(Routes.HISTORY, R.string.nav_history, Icons.Filled.History),
    BottomNavItem(Routes.SETTINGS, R.string.nav_settings, Icons.Filled.Settings)
)

@Composable
fun SensiProNavGraph(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination

    val showBottomBar = bottomNavItems.any { currentRoute?.hierarchy?.any { dest -> dest.route == it.route } == true }

    Scaffold(
        containerColor = BackgroundDeep,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = SurfaceGlassElevated) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.labelRes)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BackgroundDeep,
                                selectedTextColor = NeonCyan,
                                indicatorColor = NeonCyan,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.ONBOARDING_WELCOME,
            modifier = Modifier
                .fillMaxSize()
                .then(Modifier.padding(innerPadding))
        ) {
            composable(Routes.ONBOARDING_WELCOME) {
                OnboardingWelcomeScreen(onNext = { navController.navigate(Routes.ONBOARDING_INFO) })
            }
            composable(Routes.ONBOARDING_INFO) {
                OnboardingInfoScreen(onNext = { navController.navigate(Routes.ONBOARDING_PRIVACY) })
            }
            composable(Routes.ONBOARDING_PRIVACY) {
                OnboardingPrivacyScreen(onNext = { navController.navigate(Routes.ONBOARDING_ANALYZE) })
            }
            composable(Routes.ONBOARDING_ANALYZE) {
                OnboardingAnalyzeScreen(onAnalyze = {
                    viewModel.analyzeDevice()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING_WELCOME) { inclusive = true }
                    }
                })
            }
            composable(Routes.HOME) {
                HomeScreen(viewModel = viewModel, onSeeResult = { navController.navigate(Routes.RESULT) })
            }
            composable(Routes.RESULT) {
                ResultScreen(viewModel = viewModel)
            }
            composable(Routes.HISTORY) {
                HistoryScreen(viewModel = viewModel)
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
