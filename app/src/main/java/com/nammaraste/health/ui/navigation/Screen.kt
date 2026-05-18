package com.nammaraste.health.ui.navigation

/**
 * Sealed class defining all navigation destinations.
 * Route strings must be unique across the graph.
 */
sealed class Screen(val route: String) {

    // ── Main bottom nav destinations ──────────────────────────────────────────
    object SignIn      : Screen("sign_in")
    object Dashboard   : Screen("dashboard")
    object Roads       : Screen("roads")
    object Map         : Screen("map")
    object Contractors : Screen("contractors")

    // ── Detail destinations ───────────────────────────────────────────────────
    object RoadDetail  : Screen("road_detail/{roadId}") {
        fun createRoute(roadId: Long) = "road_detail/$roadId"
    }
    object Report      : Screen("report/{roadId}") {
        fun createRoute(roadId: Long) = "report/$roadId"
    }
    object ContractorDetail : Screen("contractor_detail/{contractorId}") {
        fun createRoute(contractorId: Long) = "contractor_detail/$contractorId"
    }
}

/** Bottom navigation items shown in the main scaffold */
val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Dashboard,
        label = "Dashboard",
        iconRes = "dashboard"
    ),
    BottomNavItem(
        screen = Screen.Roads,
        label = "Roads",
        iconRes = "roads"
    ),
    BottomNavItem(
        screen = Screen.Map,
        label = "Map",
        iconRes = "map"
    ),
    BottomNavItem(
        screen = Screen.Contractors,
        label = "Contractors",
        iconRes = "contractors"
    )
)

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val iconRes: String
)
