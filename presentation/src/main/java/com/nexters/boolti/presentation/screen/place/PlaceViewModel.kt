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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<MainRoute.Place>()
    val placeId: String = route.placeId

    private val _uiState = MutableStateFlow(PlaceUiState(Place("", "", null, null, null, null, null, null)))
    val uiState = _uiState.asStateFlow()

    init {
        fetchPlace()
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index.coerceIn(0..1)) }
    }

    private fun fetchPlace() {
        // TODO: 더미 데이터 제거 후 실제 API 연동
        _uiState.update {
            it.copy(
                place = com.nexters.boolti.domain.model.Place(
                    id = placeId,
                    name = "벨로드롬",
                    imageUrl = null,
                    rentalFee = "평일 30만원 / 주말 50만원",
                    capacity = 300,
                    streetAddress = "서울시 마포구 와우산로 94",
                    subwayStation = "홍대입구역 2번 출구",
                    contact = "02-1234-5678",
                ),
                isLoading = false,
            )
        }
    }
}
