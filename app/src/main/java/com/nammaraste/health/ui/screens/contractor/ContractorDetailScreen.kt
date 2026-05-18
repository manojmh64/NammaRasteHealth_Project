package com.nammaraste.health.ui.screens.contractor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammaraste.health.ui.theme.*

/**
 * Contractor Detail Screen — full profile including contact info, ratings, and stats.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorDetailScreen(
    contractorId: Long,
    onBack: () -> Unit,
    viewModel: ContractorViewModel = hiltViewModel()
) {
    LaunchedEffect(contractorId) { viewModel.loadContractorDetail(contractorId) }
    val uiState by viewModel.detailState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contractor Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null, tint = TextPrimary)
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

        val c = uiState.contractor ?: run {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Contractor not found", color = TextSecondary)
            }
            return@Scaffold
        }

        val ratingColor = when {
            c.rating >= 4.0f -> HealthGood
            c.rating >= 3.0f -> HealthFair
            else             -> HealthPoor
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Profile Header Card ───────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, ratingColor.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brand700),
                        contentAlignment = Alignment.Center
                    ) { Text("🏗️", fontSize = 40.sp) }

                    Spacer(Modifier.height(14.dp))
                    Text(c.name, style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(c.specialisation, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        ProfileStat("⭐ Rating", "${c.rating}/5.0", ratingColor)
                        ProfileStat("Projects", "${c.projectsCompleted}", Brand300)
                        ProfileStat(
                            "Status",
                            if (c.rating >= 3.5f) "Active" else "Review",
                            if (c.rating >= 3.5f) HealthGood else HealthFair
                        )
                    }
                }
            }

            // ── Contact Information ───────────────────────────────────────────
            SectionLabel("Contact Information")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkDivider)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                    ContactInfoRow(Icons.Filled.Person, "Contact Person", c.contactPerson)
                    HorizontalDivider(color = DarkDivider)
                    ContactInfoRow(Icons.Filled.Phone, "Phone Number", c.phone)
                    HorizontalDivider(color = DarkDivider)
                    ContactInfoRow(Icons.Filled.Email, "Email Address", c.email)
                    HorizontalDivider(color = DarkDivider)
                    ContactInfoRow(Icons.Filled.LocationOn, "Office Address", c.address)
                    HorizontalDivider(color = DarkDivider)
                    ContactInfoRow(Icons.Filled.Badge, "Registration No.", c.registrationNumber)
                }
            }

            // ── Performance Card ──────────────────────────────────────────────
            SectionLabel("Performance Summary")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkDivider)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Quality Rating", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text("${c.rating} / 5.0", style = MaterialTheme.typography.bodyMedium,
                            color = ratingColor, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { c.rating / 5.0f },
                        modifier = Modifier.fillMaxWidth(),
                        color = ratingColor,
                        trackColor = DarkDivider
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Projects Completed", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text("${c.projectsCompleted}", style = MaterialTheme.typography.bodyMedium,
                            color = Brand300, fontWeight = FontWeight.Bold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Specialisation", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text(c.specialisation, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Private composables ────────────────────────────────────────────────────────

@Composable
private fun ProfileStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = Brand300,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun ContactInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Brand700.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Brand300, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
    }
}
