package com.nammaraste.health.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.domain.model.RoadHealthInfo
import com.nammaraste.health.domain.usecase.GetRoadHealthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val isLoading: Boolean = true,
    val roads: List<RoadHealthInfo> = emptyList(),
    val selectedRoad: RoadHealthInfo? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getRoadHealth: GetRoadHealthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getRoadHealth().collect { roads ->
                _uiState.update { it.copy(isLoading = false, roads = roads) }
            }
        }
    }

    fun onRoadSelected(road: RoadHealthInfo?) {
        _uiState.update { it.copy(selectedRoad = road) }
    }
}
