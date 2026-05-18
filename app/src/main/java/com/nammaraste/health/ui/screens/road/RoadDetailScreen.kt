package com.nammaraste.health.ui.screens.road

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammaraste.health.data.db.entity.DamageReport
import com.nammaraste.health.ui.components.*
import com.nammaraste.health.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadDetailScreen(
    roadId: Long,
    onBack: () -> Unit,
    onReportClick: () -> Unit,
    viewModel: RoadDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(roadId) { viewModel.loadRoad(roadId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.roadHealth?.roadName ?: "Road Detail",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary, fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onReportClick,
                containerColor = Brand300, contentColor = Brand900,
                icon = { Icon(Icons.Filled.ReportProblem, null) },
                text = { Text("Report Damage", fontWeight = FontWeight.Bold) }
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
        val health = uiState.roadHealth ?: return@Scaffold

        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, healthColor(health.healthStatus).copy(alpha = 0.4f))
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        HealthScoreRing(score = health.healthScore, health = health.healthStatus, size = 100.dp)
                        Column(Modifier.weight(1f)) {
                            HealthBadge(health.healthStatus)
                            Spacer(Modifier.height(8.dp))
                            InfoRow("Length", "${health.lengthKm} km")
                            InfoRow("Surface", health.surfaceType)
                            InfoRow("Complaints/km", "%.2f".format(health.complaintsPerKm))
                            InfoRow("Total Reports", "${health.complaintCount}")
                        }
                    }
                }
            }
            item {
                SectionHeader("Road Information")
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, DarkDivider)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoRow("Taluka", health.taluka)
                        InfoRow("District", health.district)
                        InfoRow("Year Built", "${health.constructionYear}")
                        InfoRow("Warranty Expiry", health.warrantyExpiryMs.toShortDate(),
                            if (health.warrantyExpiryMs < System.currentTimeMillis()) HealthCritical else HealthGood)
                    }
                }
            }
            uiState.contractor?.let { c ->
                item {
                    SectionHeader("Contractor")
                    Spacer(Modifier.height(8.dp))
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = BorderStroke(1.dp, DarkDivider)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(Modifier.size(44.dp).clip(CircleShape).background(Brand700),
                                    contentAlignment = Alignment.Center) {
                                    Text("🏗️", fontSize = 20.sp)
                                }
                                Column {
                                    Text(c.name, style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                    Text(c.contactPerson, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            InfoRow("📞 Phone", c.phone)
                            InfoRow("✉️ Email", c.email)
                            InfoRow("⭐ Rating", "${c.rating}/5.0")
                        }
                    }
                }
            }
            item { SectionHeader("Damage Reports (${uiState.reports.size})") }
            if (uiState.reports.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✅", fontSize = 32.sp)
                            Text("No damage reports", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(uiState.reports) { report -> DamageReportCard(report) }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
        Text(value, style = MaterialTheme.typography.bodySmall, color = valueColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DamageReportCard(report: DamageReport) {
    val sevColor = when (report.severity) {
        "LOW" -> SeverityLow; "MEDIUM" -> SeverityMedium
        "HIGH" -> SeverityHigh; "CRITICAL" -> SeverityCritical
        else -> TextSecondary
    }
    val statusColor = when (report.status) {
        "RESOLVED" -> HealthGood; "IN_PROGRESS" -> HealthFair
        "ACKNOWLEDGED" -> Brand300; else -> TextTertiary
    }
    val emoji = when (report.issueType) {
        "POTHOLE" -> "🕳️"; "CRACK" -> "🪨"
        "WATERLOGGING" -> "💧"; "DEBRIS" -> "🪵"; else -> "❓"
    }
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
        border = BorderStroke(1.dp, sevColor.copy(alpha = 0.25f))) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(emoji, fontSize = 18.sp)
                    Text(report.issueType.replace("_", " "), style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
                Surface(shape = RoundedCornerShape(6.dp), color = sevColor.copy(alpha = 0.15f)) {
                    Text(report.severity, style = MaterialTheme.typography.labelSmall,
                        color = sevColor, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            if (report.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(report.description, style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary, maxLines = 2)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(report.timestamp.toFormattedDate(), style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Surface(shape = RoundedCornerShape(6.dp), color = statusColor.copy(alpha = 0.12f)) {
                    Text(report.status.replace("_", " "), style = MaterialTheme.typography.labelSmall,
                        color = statusColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}
