package com.nexters.boolti.presentation.screen.place.images

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.repository.PlaceRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaceImageDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<MainRoute.PlaceImageDetail>()

    private val _initialIndex = MutableStateFlow(0)
    val initialIndex = _initialIndex.asStateFlow()

    private val _uiState = MutableStateFlow(PlaceImagesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchImages()
    }

    private fun fetchImages() {
        placeRepository.getPlaceImages(route.placeId)
            .onEach { images ->
                val initialImageIndex = images.indexOfFirst { route.initialImageId == it.id }
                if (initialImageIndex != -1) {
                    _initialIndex.update { initialImageIndex }
                }

                _uiState.update {
                    it.copy(
                        images = images,
                        isLoading = false,
                    )
                }
            }
            .catch { e ->
                Timber.e(e, "공연장 사진 목록 조회 실패")
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
    }
}
