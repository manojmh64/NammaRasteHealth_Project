package com.nammaraste.health.ui.screens.report

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.*
import com.nammaraste.health.domain.model.IssueType
import com.nammaraste.health.domain.model.Severity
import com.nammaraste.health.ui.components.SectionHeader
import com.nammaraste.health.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ReportScreen(
    roadId: Long,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Navigate on success
    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) onSubmitSuccess()
    }

    // Permissions
    val locationPermState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPermState = rememberPermissionState(Manifest.permission.CAMERA)

    // Camera temp file
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraImageFile by remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraImageUri != null) {
            viewModel.onPhotoSelected(cameraImageUri, cameraImageFile)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.onPhotoSelected(it, null) }
    }

    // Fetch location when permission is granted
    LaunchedEffect(locationPermState.status) {
        if (locationPermState.status.isGranted) viewModel.fetchLocation(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Damage", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Photo Section ───────────────────────────────────────────────
            SectionHeader("📸 Photo Evidence")
            Spacer(Modifier.height(4.dp))

            if (uiState.photoUri != null) {
                Box(Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp))) {
                    AsyncImage(
                        model = uiState.photoUri,
                        contentDescription = "Report photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { viewModel.onPhotoSelected(null, null) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Filled.Cancel, null, tint = Color.White)
                    }
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            if (cameraPermState.status.isGranted) {
                                val file = createTempImageFile(context)
                                cameraImageFile = file
                                cameraImageUri = FileProvider.getUriForFile(
                                    context, "${context.packageName}.fileprovider", file
                                )
                                cameraLauncher.launch(cameraImageUri!!)
                            } else cameraPermState.launchPermissionRequest()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Brand300),
                        border = BorderStroke(1.dp, Brand300)
                    ) {
                        Icon(Icons.Filled.CameraAlt, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Camera")
                    }
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Brand300),
                        border = BorderStroke(1.dp, Brand300)
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Gallery")
                    }
                }
            }

            // ── AI Classification result ─────────────────────────────────────
            AnimatedVisibility(visible = uiState.isClassifying) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(Modifier.size(16.dp), color = Brand300, strokeWidth = 2.dp)
                    Text("AI analysing image...", style = MaterialTheme.typography.bodySmall, color = Brand300)
                }
            }
            uiState.classification?.let { cls ->
                Card(
                    Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Brand700.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, Brand300.copy(alpha = 0.4f))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.SmartToy, null, tint = Brand300, modifier = Modifier.size(16.dp))
                            Text("AI Suggestion", style = MaterialTheme.typography.labelMedium,
                                color = Brand300, fontWeight = FontWeight.Bold)
                            Text("(${(cls.confidence * 100).toInt()}% confidence)",
                                style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(cls.reasoning, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            // ── Issue Type ───────────────────────────────────────────────────
            SectionHeader("🔍 Issue Type")
            Spacer(Modifier.height(4.dp))
            val issueTypes = IssueType.entries.filter { it != IssueType.UNKNOWN }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                issueTypes.forEach { type ->
                    FilterChip(
                        selected = uiState.selectedIssueType == type,
                        onClick = { viewModel.onIssueTypeChanged(type) },
                        label = { Text("${type.emoji} ${type.displayName}", maxLines = 1) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand700,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkCard, labelColor = TextSecondary
                        )
                    )
                }
            }

            // ── Severity ─────────────────────────────────────────────────────
            SectionHeader("⚡ Severity")
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Severity.entries.forEach { sev ->
                    val sevColor = when (sev) {
                        Severity.LOW -> SeverityLow; Severity.MEDIUM -> SeverityMedium
                        Severity.HIGH -> SeverityHigh; Severity.CRITICAL -> SeverityCritical
                    }
                    FilterChip(
                        selected = uiState.selectedSeverity == sev,
                        onClick = { viewModel.onSeverityChanged(sev) },
                        label = { Text(sev.displayName) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = sevColor.copy(alpha = 0.25f),
                            selectedLabelColor = sevColor,
                            containerColor = DarkCard, labelColor = TextSecondary
                        )
                    )
                }
            }

            // ── Description ──────────────────────────────────────────────────
            SectionHeader("📝 Description")
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = { Text("Describe the road damage...", color = TextTertiary) },
                minLines = 3, maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Brand300, unfocusedBorderColor = DarkDivider,
                    focusedContainerColor = DarkCard, unfocusedContainerColor = DarkCard,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                )
            )

            // ── GPS Location ─────────────────────────────────────────────────
            SectionHeader("📍 GPS Location")
            Spacer(Modifier.height(4.dp))
            if (!locationPermState.status.isGranted) {
                OutlinedButton(
                    onClick = { locationPermState.launchPermissionRequest() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Accent500)
                ) {
                    Icon(Icons.Filled.LocationOn, null, tint = Accent500, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Grant Location Permission", color = Accent500)
                }
            } else {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, DarkDivider)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Filled.LocationOn, null, tint = if (uiState.location != null) HealthGood else TextTertiary)
                        if (uiState.location != null) {
                            Column {
                                Text("Location captured ✓", style = MaterialTheme.typography.bodyMedium,
                                    color = HealthGood, fontWeight = FontWeight.Medium)
                                Text("${"%.5f".format(uiState.location!!.latitude)}, ${"%.5f".format(uiState.location!!.longitude)}",
                                    style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            }
                        } else {
                            Column {
                                Text("Fetching GPS location...", color = TextSecondary)
                                LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 4.dp), color = Brand300)
                            }
                        }
                    }
                }
            }

            uiState.error?.let {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HealthCritical.copy(alpha = 0.1f))) {
                    Text(it, color = HealthCritical, modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall)
                }
            }

            // ── Submit Button ─────────────────────────────────────────────────
            Button(
                onClick = { viewModel.submitReport(roadId) },
                enabled = !uiState.isSubmitting && uiState.location != null,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Brand300, contentColor = Brand900)
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(Modifier.size(22.dp), color = Brand900, strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Submitting...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Filled.Send, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Report", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun createTempImageFile(context: Context): File {
    val dir = File(context.cacheDir, "road_photos").also { it.mkdirs() }
    return File(dir, "photo_${System.currentTimeMillis()}.jpg")
}
