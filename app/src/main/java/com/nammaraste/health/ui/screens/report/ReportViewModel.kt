package com.nammaraste.health.ui.screens.report

import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.nammaraste.health.data.db.entity.DamageReport
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.domain.model.ClassificationResult
import com.nammaraste.health.domain.model.IssueType
import com.nammaraste.health.domain.model.Severity
import com.nammaraste.health.domain.usecase.ClassifyDamageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

data class ReportUiState(
    val isLoading: Boolean = false,
    val photoUri: Uri? = null,
    val photoFile: File? = null,
    val description: String = "",
    val selectedIssueType: IssueType = IssueType.POTHOLE,
    val selectedSeverity: Severity = Severity.MEDIUM,
    val location: Location? = null,
    val locationError: String? = null,
    val classification: ClassificationResult? = null,
    val isClassifying: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: RoadRepository,
    private val classifyDamage: ClassifyDamageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    // ─── Photo handling ───────────────────────────────────────────────────────

    fun onPhotoSelected(uri: Uri?, file: File?) {
        _uiState.update { it.copy(photoUri = uri, photoFile = file, classification = null) }
        // Trigger AI classification when photo is selected
        file?.let { classifyPhoto(it) }
    }

    private fun classifyPhoto(file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isClassifying = true) }
            val result = classifyDamage(file)
            _uiState.update {
                it.copy(
                    isClassifying = false,
                    classification = result,
                    selectedIssueType = result.issueType,
                    selectedSeverity = result.severity
                )
            }
        }
    }

    // ─── Form updates ─────────────────────────────────────────────────────────

    fun onDescriptionChange(desc: String) {
        _uiState.update { it.copy(description = desc) }
        // Auto-classify from description if no photo
        if (_uiState.value.photoUri == null && desc.length > 10) {
            viewModelScope.launch {
                val result = classifyDamage.fromDescription(desc)
                _uiState.update {
                    it.copy(
                        classification = result,
                        selectedIssueType = result.issueType,
                        selectedSeverity = result.severity
                    )
                }
            }
        }
    }

    fun onIssueTypeChanged(type: IssueType) = _uiState.update { it.copy(selectedIssueType = type) }
    fun onSeverityChanged(sev: Severity) = _uiState.update { it.copy(selectedSeverity = sev) }

    // ─── Location ─────────────────────────────────────────────────────────────

    fun fetchLocation(context: Context) {
        viewModelScope.launch {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                val cts = CancellationTokenSource()
                val location = fusedClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, cts.token
                ).await()
                _uiState.update { it.copy(location = location, locationError = null) }
            } catch (e: SecurityException) {
                _uiState.update { it.copy(locationError = "Location permission denied") }
            } catch (e: Exception) {
                _uiState.update { it.copy(locationError = "Could not get location: ${e.message}") }
            }
        }
    }

    // ─── Submit ───────────────────────────────────────────────────────────────

    fun submitReport(roadId: Long) {
        val state = _uiState.value
        val location = state.location

        if (location == null) {
            _uiState.update { it.copy(error = "Location is required. Please enable GPS and try again.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            try {
                val report = DamageReport(
                    roadId = roadId,
                    photoPath = state.photoFile?.absolutePath,
                    issueType = state.selectedIssueType.name,
                    severity = state.selectedSeverity.name,
                    description = state.description,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = System.currentTimeMillis(),
                    status = "PENDING"
                )
                repository.submitReport(report)
                _uiState.update { it.copy(isSubmitting = false, submitSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSubmitting = false, error = "Failed to submit: ${e.message}")
                }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
