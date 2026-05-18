package com.nammaraste.health.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.domain.model.DashboardStats
import com.nammaraste.health.domain.model.RoadHealthInfo
import com.nammaraste.health.domain.usecase.GetDashboardStatsUseCase
import com.nammaraste.health.domain.usecase.GetRoadHealthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val stats: DashboardStats? = null,
    val worstRoads: List<RoadHealthInfo> = emptyList(),
    val bestRoads: List<RoadHealthInfo> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardStats: GetDashboardStatsUseCase,
    private val getRoadHealth: GetRoadHealthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            combine(
                getDashboardStats(),
                getRoadHealth()
            ) { stats, healthList ->
                DashboardUiState(
                    isLoading = false,
                    stats = stats,
                    worstRoads = healthList.take(3),
                    bestRoads = healthList.sortedByDescending { it.healthScore }.take(3)
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadDashboard()
    }
}
