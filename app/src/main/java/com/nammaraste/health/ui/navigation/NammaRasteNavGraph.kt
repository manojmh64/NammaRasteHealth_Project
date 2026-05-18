package com.nammaraste.health.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.compose.*
import com.nammaraste.health.ui.screens.auth.SignInScreen
import com.nammaraste.health.ui.screens.contractor.ContractorDetailScreen
import com.nammaraste.health.ui.screens.contractor.ContractorListScreen
import com.nammaraste.health.ui.screens.dashboard.DashboardScreen
import com.nammaraste.health.ui.screens.map.MapScreen
import com.nammaraste.health.ui.screens.report.ReportScreen
import com.nammaraste.health.ui.screens.road.RoadDetailScreen
import com.nammaraste.health.ui.screens.road.RoadListScreen

/**
 * Root navigation graph with animated transitions and bottom navigation bar.
 */
@Composable
fun NammaRasteNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route, Screen.Roads.route,
        Screen.Map.route, Screen.Contractors.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NammaRasteBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SignIn.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(200))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(200))
            }
        ) {
            composable(Screen.SignIn.route) {
                SignInScreen(onSignInSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onRoadClick = { id ->
                        navController.navigate(Screen.RoadDetail.createRoute(id))
                    },
                    onSignOut = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Roads.route) {
                RoadListScreen(
                    onRoadClick = { id -> navController.navigate(Screen.RoadDetail.createRoute(id)) }
                )
            }
            composable(Screen.Map.route) {
                MapScreen(
                    onRoadClick = { id -> navController.navigate(Screen.RoadDetail.createRoute(id)) }
                )
            }
            composable(Screen.Contractors.route) {
                ContractorListScreen(
                    onContractorClick = { id ->
                        navController.navigate(Screen.ContractorDetail.createRoute(id))
                    }
                )
            }
            composable(
                route = Screen.RoadDetail.route,
                arguments = listOf(navArgument("roadId") { type = NavType.LongType })
            ) { backStack ->
                val roadId = backStack.arguments?.getLong("roadId") ?: 0L
                RoadDetailScreen(
                    roadId = roadId,
                    onBack = { navController.popBackStack() },
                    onReportClick = { navController.navigate(Screen.Report.createRoute(roadId)) }
                )
            }
            composable(
                route = Screen.Report.route,
                arguments = listOf(navArgument("roadId") { type = NavType.LongType })
            ) { backStack ->
                val roadId = backStack.arguments?.getLong("roadId") ?: 0L
                ReportScreen(
                    roadId = roadId,
                    onBack = { navController.popBackStack() },
                    onSubmitSuccess = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.ContractorDetail.route,
                arguments = listOf(navArgument("contractorId") { type = NavType.LongType })
            ) { backStack ->
                val contractorId = backStack.arguments?.getLong("contractorId") ?: 0L
                ContractorDetailScreen(
                    contractorId = contractorId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun NammaRasteBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Triple(Screen.Dashboard.route, Icons.Filled.Dashboard, "Dashboard"),
        Triple(Screen.Roads.route, Icons.Filled.Route, "Roads"),
        Triple(Screen.Map.route, Icons.Filled.Map, "Map"),
        Triple(Screen.Contractors.route, Icons.Filled.Engineering, "Contractors")
    )

    NavigationBar(
        containerColor = androidx.compose.ui.graphics.Color(0xFF111827),
        tonalElevation = 0.dp
    ) {
        items.forEach { (route, icon, label) ->
            val selected = currentRoute == route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon as ImageVector,
                        contentDescription = label
                    )
                },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = com.nammaraste.health.ui.theme.Brand300,
                    selectedTextColor = com.nammaraste.health.ui.theme.Brand300,
                    indicatorColor = com.nammaraste.health.ui.theme.Brand700.copy(alpha = 0.3f),
                    unselectedIconColor = com.nammaraste.health.ui.theme.TextTertiary,
                    unselectedTextColor = com.nammaraste.health.ui.theme.TextTertiary
                )
            )
        }
    }
}
