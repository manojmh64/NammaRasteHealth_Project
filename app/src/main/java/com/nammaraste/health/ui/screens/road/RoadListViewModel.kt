package com.nammaraste.health.ui.screens.road

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.db.entity.Road
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.domain.model.RoadHealthInfo
import com.nammaraste.health.domain.usecase.GetRoadHealthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoadListUiState(
    val isLoading: Boolean = true,
    val roads: List<RoadHealthInfo> = emptyList(),
    val filteredRoads: List<RoadHealthInfo> = emptyList(),
    val searchQuery: String = "",
    val selectedTaluka: String = "All",
    val talukas: List<String> = emptyList(),
    val error: String? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class RoadListViewModel @Inject constructor(
    private val repository: RoadRepository,
    private val getRoadHealth: GetRoadHealthUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedTaluka = MutableStateFlow("All")

    private val _uiState = MutableStateFlow(RoadListUiState())
    val uiState: StateFlow<RoadListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getRoadHealth(),
                repository.getAllTalukas(),
                _searchQuery.debounce(300),
                _selectedTaluka
            ) { roads, talukas, query, taluka ->
                val filtered = roads.filter { road ->
                    val matchesSearch = query.isBlank() ||
                            road.roadName.contains(query, ignoreCase = true) ||
                            road.taluka.contains(query, ignoreCase = true)
                    val matchesTaluka = taluka == "All" || road.taluka == taluka
                    matchesSearch && matchesTaluka
                }
                RoadListUiState(
                    isLoading = false,
                    roads = roads,
                    filteredRoads = filtered,
                    searchQuery = query,
                    selectedTaluka = taluka,
                    talukas = listOf("All") + talukas
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTalukaSelected(taluka: String) {
        _selectedTaluka.value = taluka
    }
}
