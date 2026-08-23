package com.nexters.boolti.presentation.screen.place

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.Place
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.PlaceRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository,
    private val authRepository: AuthRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<MainRoute.Place>()
    val placeId: String = route.placeId

    private val _uiState =
        MutableStateFlow(PlaceUiState(Place("", "", null, null, null, null, emptyList(), null)))
    val uiState = _uiState.asStateFlow()

    init {
        fetchPlace()
    }

    /**
     * 웹 브릿지의 토큰 요청에 응답한다.
     *
     * 공연장 화면은 비로그인 사용자도 진입할 수 있으므로, 토큰 갱신에 실패하면 빈 문자열을 반환한다.
     */
    suspend fun refreshAndGetToken(): String = runCatching {
        authRepository.refreshToken().first().token
    }.getOrElse { e ->
        Timber.tag("bridge").w(e, "토큰 갱신 실패 (비로그인 상태일 수 있음)")
        ""
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
