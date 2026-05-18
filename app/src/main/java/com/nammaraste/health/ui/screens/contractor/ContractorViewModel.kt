package com.nammaraste.health.ui.screens.contractor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.db.entity.Contractor
import com.nammaraste.health.data.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContractorListUiState(
    val isLoading: Boolean = true,
    val contractors: List<Contractor> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)

data class ContractorDetailUiState(
    val isLoading: Boolean = true,
    val contractor: Contractor? = null,
    val error: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ContractorViewModel @Inject constructor(
    private val repository: RoadRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _listState = MutableStateFlow(ContractorListUiState())
    val listState: StateFlow<ContractorListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ContractorDetailUiState())
    val detailState: StateFlow<ContractorDetailUiState> = _detailState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery.debounce(300)
                .flatMapLatest { query ->
                    if (query.isBlank()) repository.getAllContractors()
                    else repository.searchContractors(query)
                }
                .collect { contractors ->
                    _listState.update {
                        it.copy(isLoading = false, contractors = contractors)
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _listState.update { it.copy(searchQuery = query) }
    }

    fun loadContractorDetail(id: Long) {
        viewModelScope.launch {
            repository.getContractorById(id).collect { contractor ->
                _detailState.update {
                    it.copy(isLoading = false, contractor = contractor)
                }
            }
        }
    }
}
