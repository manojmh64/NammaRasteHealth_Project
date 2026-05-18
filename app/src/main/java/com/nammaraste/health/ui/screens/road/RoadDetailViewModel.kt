package com.nammaraste.health.ui.screens.road

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.db.entity.Contractor
import com.nammaraste.health.data.db.entity.DamageReport
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.domain.model.RoadHealthInfo
import com.nammaraste.health.domain.usecase.GetRoadHealthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoadDetailUiState(
    val isLoading: Boolean = true,
    val roadHealth: RoadHealthInfo? = null,
    val contractor: Contractor? = null,
    val reports: List<DamageReport> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class RoadDetailViewModel @Inject constructor(
    private val repository: RoadRepository,
    private val getRoadHealth: GetRoadHealthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoadDetailUiState())
    val uiState: StateFlow<RoadDetailUiState> = _uiState.asStateFlow()

    fun loadRoad(roadId: Long) {
        viewModelScope.launch {
            combine(
                getRoadHealth.forRoad(roadId),
                repository.getReportsByRoad(roadId)
            ) { healthInfo, reports ->
                // Load contractor separately when we have the health info
                val contractor = healthInfo?.contractorId?.let { cId ->
                    repository.getContractorById(cId).firstOrNull()
                }
                RoadDetailUiState(
                    isLoading = false,
                    roadHealth = healthInfo,
                    contractor = contractor,
                    reports = reports
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
