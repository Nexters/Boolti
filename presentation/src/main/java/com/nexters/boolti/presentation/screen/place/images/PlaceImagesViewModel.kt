package com.nexters.boolti.presentation.screen.place.images

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.nexters.boolti.domain.repository.PlaceRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaceImagesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<MainRoute.PlaceImages>()

    private val _uiState = MutableStateFlow(PlaceImagesUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<PlaceImagesEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        fetchImages()
    }

    private fun fetchImages() {
        placeRepository.getPlaceImages(route.placeId)
            .onEach { images ->
                _uiState.update {
                    it.copy(
                        images = images,
                        isLoading = false,
                    )
                }

                val receivedImageId = route.imageId ?: return@onEach
                val imageIndex = images.indexOfFirst { it.id == receivedImageId }
                if (imageIndex != -1) {
                    sendEvent(PlaceImagesEvent.NavigateToDetail(index = imageIndex))
                }
            }
            .catch { e ->
                Firebase.crashlytics.recordException(e)
                Timber.e(e, "공연장 사진 목록 조회 실패")
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun sendEvent(event: PlaceImagesEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }
}
