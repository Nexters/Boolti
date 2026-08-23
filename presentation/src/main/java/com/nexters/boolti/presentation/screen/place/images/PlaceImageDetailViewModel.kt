package com.nexters.boolti.presentation.screen.place.images

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.orderedBy
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
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaceImageDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<MainRoute.PlaceImageDetail>()

    /** 목록에서 선택한 사진의 위치. 이미지 개수보다 크면 첫 장부터 보여준다. */
    val initialIndex: Int = route.initialIndex.coerceAtLeast(0)

    private val _uiState = MutableStateFlow(PlaceImagesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchImages()
    }

    private fun fetchImages() {
        placeRepository.getPlaceImages(route.placeId)
            .onEach { images ->
                _uiState.update {
                    it.copy(
                        images = images.orderedBy(route.imageIds),
                        isLoading = false,
                    )
                }
            }
            .catch { e ->
                Timber.e(e, "공연장 사진 목록 조회 실패")
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}
