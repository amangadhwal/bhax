package com.bhax.app.ui.explorer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bhax.app.data.LocationData
import com.bhax.app.data.LocationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ExplorerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LocationRepository.getInstance(application)

    private val _searchResults = MutableStateFlow<List<LocationData>>(emptyList())
    val searchResults: StateFlow<List<LocationData>> = _searchResults

    private val _selectedLocation = MutableStateFlow<LocationData?>(null)
    val selectedLocation: StateFlow<LocationData?> = _selectedLocation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            try {
                repository.initializeData()
            } catch (e: Exception) {
                _error.value = "Failed to initialize data: ${e.message}"
            }
        }
    }

    fun searchByPincode(pincode: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val result = repository.searchByPincode(pincode)
                _searchResults.value = listOfNotNull(result)
                result?.let { _selectedLocation.value = it }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchByAreaName(query: String) {
        viewModelScope.launch {
            try {
                if (query.length < 3) {
                    _searchResults.value = emptyList()
                    return@launch
                }
                _isLoading.value = true
                _error.value = null
                val results = repository.searchByAreaName(query)
                _searchResults.value = results
                if (results.isNotEmpty()) {
                    _selectedLocation.value = results.first()
                }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun observeLocationsInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ) {
        viewModelScope.launch {
            try {
                repository.observeLocationsInBounds(minLat, maxLat, minLng, maxLng)
                    .collectLatest { locations ->
                        _searchResults.value = locations
                    }
            } catch (e: Exception) {
                _error.value = "Failed to observe locations: ${e.message}"
            }
        }
    }

    fun selectLocation(location: LocationData) {
        _selectedLocation.value = location
    }

    fun clearSelection() {
        _selectedLocation.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
