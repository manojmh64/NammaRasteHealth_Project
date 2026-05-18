package com.nammaraste.health.ui.screens.contractor

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
import com.nammaraste.health.data.db.entity.Contractor
import com.nammaraste.health.ui.components.SectionHeader
import com.nammaraste.health.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorListScreen(
    onContractorClick: (Long) -> Unit,
    viewModel: ContractorViewModel = hiltViewModel()
) {
    val uiState by viewModel.listState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contractors", color = TextPrimary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Search contractors…", color = TextTertiary) },
                leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextTertiary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Brand300, unfocusedBorderColor = DarkDivider,
                    focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Brand300)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(uiState.contractors, key = { it.id }) { contractor ->
                        ContractorCard(contractor = contractor, onClick = { onContractorClick(contractor.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractorCard(contractor: Contractor, onClick: () -> Unit) {
    val ratingColor = when {
        contractor.rating >= 4.0f -> HealthGood
        contractor.rating >= 3.0f -> HealthFair
        else -> HealthPoor
    }
    Card(onClick = onClick, Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, DarkDivider)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(Brand700),
                contentAlignment = Alignment.Center) {
                Text("🏗️", fontSize = 24.sp)
            }
            Column(Modifier.weight(1f)) {
                Text(contractor.name, style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text(contractor.contactPerson, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                Text(contractor.specialisation, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐ ${contractor.rating}", style = MaterialTheme.typography.titleSmall,
                    color = ratingColor, fontWeight = FontWeight.Bold)
                Text("${contractor.projectsCompleted} projects",
                    style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            }
        }
    }
}


