package com.nammaraste.health.ui.screens.map

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.nammaraste.health.domain.model.*
import com.nammaraste.health.ui.components.*
import com.nammaraste.health.ui.theme.*

/**
 * Map Screen — Google Maps with colored polylines showing road health.
 *
 * If the Maps API key is not set, falls back to a colour-coded list view
 * so the app runs fully without a key during development.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onRoadClick: (Long) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Toggle between map and list view
    var showListFallback by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Road Health Map", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text(
                            if (showListFallback) "List view (no Maps key)" else "Tap a road for details",
                            style = MaterialTheme.typography.bodySmall, color = TextSecondary
                        )
                    }
                },
                actions = {
                    // Toggle button: switch between map and list
                    IconButton(onClick = { showListFallback = !showListFallback }) {
                        Icon(
                            imageVector = if (showListFallback) Icons.Filled.Map else Icons.Filled.List,
                            contentDescription = "Toggle view",
                            tint = Brand300
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Brand300)
            }
            return@Scaffold
        }

        if (showListFallback) {
            // ── Fallback list view (no API key needed) ─────────────────────────
            RoadHealthListView(
                roads = uiState.roads,
                onRoadClick = onRoadClick,
                modifier = Modifier.padding(padding)
            )
        } else {
            // ── Google Maps view ───────────────────────────────────────────────
            Box(Modifier.fillMaxSize().padding(padding)) {
                val cameraState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(15.45, 75.01), 9f)
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState,
                    properties = MapProperties(mapType = MapType.NORMAL),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true),
                    onMapClick = { viewModel.onRoadSelected(null) }
                ) {
                    uiState.roads.forEach { road ->
                        val color = healthColor(road.healthStatus)
                        val start = LatLng(road.startLat, road.startLng)
                        val end   = LatLng(road.endLat, road.endLng)

                        Polyline(
                            points = listOf(start, end),
                            color = color, width = 12f, clickable = true,
                            onClick = { viewModel.onRoadSelected(road) }
                        )
                        Marker(
                            state = MarkerState(position = start),
                            title = road.roadName,
                            snippet = "${road.healthStatus.displayName} — Score: ${road.healthScore.toInt()}",
                            onClick = { viewModel.onRoadSelected(road); false }
                        )
                    }
                }

                // Legend overlay
                MapLegend(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp))

                // No-key warning banner
                Surface(
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Accent500.copy(alpha = 0.92f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.Info, null, tint = Brand900, modifier = Modifier.size(14.dp))
                        Text(
                            "Map tiles need a real API key. Tap ☰ for list view.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Brand900, fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Selected road bottom card
                AnimatedVisibility(
                    visible = uiState.selectedRoad != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit  = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    uiState.selectedRoad?.let { road ->
                        SelectedRoadCard(
                            road = road,
                            onDetailsClick = { onRoadClick(road.roadId) },
                            onClose = { viewModel.onRoadSelected(null) }
                        )
                    }
                }
            }
        }
    }
}

// ── Fallback: colour-coded road list ──────────────────────────────────────────

@Composable
private fun RoadHealthListView(
    roads: List<RoadHealthInfo>,
    onRoadClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Health legend card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkDivider)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🗺️ Health Legend", style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        RoadHealth.entries.forEach { h ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(healthColor(h))
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(h.displayName, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                "${roads.size} roads sorted by health (worst first)",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(roads, key = { it.roadId }) { road ->
            RoadHealthCard(info = road, onClick = { onRoadClick(road.roadId) })
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ── Reusable composables ───────────────────────────────────────────────────────

@Composable
private fun MapLegend(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, DarkDivider)
    ) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Legend", style = MaterialTheme.typography.labelSmall,
                color = TextSecondary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            RoadHealth.entries.forEach { h ->
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(12.dp, 4.dp).background(healthColor(h), RoundedCornerShape(2.dp)))
                    Text(h.displayName, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun SelectedRoadCard(
    road: RoadHealthInfo,
    onDetailsClick: () -> Unit,
    onClose: () -> Unit
) {
    val hColor = healthColor(road.healthStatus)
    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, hColor.copy(alpha = 0.4f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(road.roadName, style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("${road.taluka} · ${road.lengthKm} km",
                        style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Close, null, tint = TextTertiary)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HealthBadge(road.healthStatus)
                    Text("${road.complaintCount} reports",
                        style = MaterialTheme.typography.labelSmall, color = TextTertiary,
                        modifier = Modifier.align(Alignment.CenterVertically))
                }
                Button(
                    onClick = onDetailsClick, shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brand300, contentColor = Brand900)
                ) {
                    Text("View Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
