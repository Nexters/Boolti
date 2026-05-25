package com.nexters.boolti.presentation.screen.place

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.Place
import com.nexters.boolti.domain.repository.PlaceRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<MainRoute.Place>()
    val placeId: String = route.placeId

    private val _uiState =
        MutableStateFlow(PlaceUiState(Place("", "", null, null, null, null, emptyList(), null)))
    val uiState = _uiState.asStateFlow()

    init {
        fetchPlace()
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index.coerceIn(0..1)) }
    }

    private fun fetchPlace() {
        placeRepository.getPlace(placeId)
            .onEach { place ->
                _uiState.update {
                    it.copy(
                        place = place,
                        isLoading = false,
                    )
                }
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}
