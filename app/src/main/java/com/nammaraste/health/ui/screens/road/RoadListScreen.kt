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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammaraste.health.domain.model.RoadHealthInfo
import com.nammaraste.health.ui.components.*
import com.nammaraste.health.ui.theme.*

/**
 * Road Directory Screen — searchable list of all roads with health indicators.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadListScreen(
    onRoadClick: (Long) -> Unit,
    viewModel: RoadListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Road Directory",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Search bar ───────────────────────────────────────────────────
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Search roads, talukas…", color = TextTertiary) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = TextTertiary)
                },
                trailingIcon = if (uiState.searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Brand300,
                    unfocusedBorderColor = DarkDivider,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Brand300
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // ── Taluka filter chips ───────────────────────────────────────────
            if (uiState.talukas.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.talukas) { taluka ->
                        FilterChip(
                            selected = uiState.selectedTaluka == taluka,
                            onClick = { viewModel.onTalukaSelected(taluka) },
                            label = { Text(taluka) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Brand700,
                                selectedLabelColor = TextPrimary,
                                containerColor = DarkCard,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Result count ──────────────────────────────────────────────────
            Text(
                text = "${uiState.filteredRoads.size} road${if (uiState.filteredRoads.size != 1) "s" else ""} found",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // ── Roads list ────────────────────────────────────────────────────
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Brand300)
                }
            } else if (uiState.filteredRoads.isEmpty()) {
                EmptyRoadsState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = uiState.filteredRoads,
                        key = { it.roadId }
                    ) { road ->
                        RoadHealthCard(
                            info = road,
                            onClick = { onRoadClick(road.roadId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRoadsState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🛣️", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text("No roads found", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
            Text(
                "Try a different search term or filter",
                style = MaterialTheme.typography.bodySmall, color = TextTertiary
            )
        }
    }
}
