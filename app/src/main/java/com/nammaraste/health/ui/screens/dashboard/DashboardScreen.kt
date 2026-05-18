package com.nammaraste.health.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammaraste.health.domain.model.*
import com.nammaraste.health.ui.components.*
import com.nammaraste.health.ui.theme.*

/**
 * Dashboard Screen — Taluka-level overview, stats, and top/worst roads.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onRoadClick: (Long) -> Unit,
    onSignOut: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Namma-Raste Health",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Rural Road Monitoring",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            tint = Brand300
                        )
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = Brand300
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Brand300)
                    Spacer(Modifier.height(12.dp))
                    Text("Loading dashboard...", color = TextSecondary)
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Hero Banner ──────────────────────────────────────────────────
            item {
                HeroBanner(
                    totalRoads = uiState.stats?.totalRoads ?: 0,
                    totalComplaints = uiState.stats?.totalComplaints ?: 0
                )
            }

            // ── Stats Row ────────────────────────────────────────────────────
            item {
                SectionHeader("Overview", Modifier.padding(bottom = 8.dp))
                val stats = uiState.stats
                if (stats != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            title = "Total Roads",
                            value = "${stats.totalRoads}",
                            icon = Icons.Filled.Route,
                            color = Brand300,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Complaints",
                            value = "${stats.totalComplaints}",
                            icon = Icons.Filled.Report,
                            color = SeverityHigh,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            title = "Critical Roads",
                            value = "${stats.criticalRoads}",
                            icon = Icons.Filled.Dangerous,
                            color = HealthCritical,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Good Roads",
                            value = "${stats.goodRoads}",
                            icon = Icons.Filled.CheckCircle,
                            color = HealthGood,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── Worst Roads ──────────────────────────────────────────────────
            if (uiState.worstRoads.isNotEmpty()) {
                item {
                    SectionHeader("⚠️ Needs Attention", Modifier.padding(bottom = 8.dp))
                }
                items(uiState.worstRoads) { road ->
                    RoadHealthCard(info = road, onClick = { onRoadClick(road.roadId) })
                }
            }

            // ── Best Roads ───────────────────────────────────────────────────
            if (uiState.bestRoads.isNotEmpty()) {
                item {
                    SectionHeader("✅ Best Maintained", Modifier.padding(bottom = 8.dp, top = 4.dp))
                }
                items(uiState.bestRoads) { road ->
                    RoadHealthCard(info = road, onClick = { onRoadClick(road.roadId) })
                }
            }

            // ── Taluka Summary ───────────────────────────────────────────────
            val talukas = uiState.stats?.talukaSummaries ?: emptyList()
            if (talukas.isNotEmpty()) {
                item {
                    SectionHeader("📍 Taluka Summary", Modifier.padding(bottom = 8.dp, top = 4.dp))
                }
                items(talukas) { summary ->
                    TalukaSummaryCard(summary = summary)
                }
            }

            // Bottom spacing
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun HeroBanner(totalRoads: Int, totalComplaints: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Brand700, Color(0xFF1E5F74), Brand400),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🛣️", fontSize = 32.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Karnataka Rural Roads",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Infrastructure Monitoring Dashboard",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HeroStat(label = "Roads Monitored", value = "$totalRoads")
                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )
                HeroStat(label = "Total Reports", value = "$totalComplaints")
                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )
                HeroStat(label = "Talukas", value = "4")
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun TalukaSummaryCard(summary: TalukaSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
        border = BorderStroke(1.dp, DarkDivider)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = summary.taluka,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Brand700.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "${summary.totalRoads} roads",
                        style = MaterialTheme.typography.labelSmall,
                        color = Brand300,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TalukaStatItem(icon = "🔴", label = "Complaints", value = "${summary.totalComplaints}")
                TalukaStatItem(icon = "📊", label = "Avg Health", value = "${summary.averageHealthScore.toInt()}%")
                TalukaStatItem(icon = "✅", label = "Best Road", value = summary.bestRoadName.take(14))
            }
        }
    }
}

@Composable
private fun TalukaStatItem(icon: String, label: String, value: String) {
    Column {
        Text(
            text = "$icon $label",
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
