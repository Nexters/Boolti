package com.nexters.boolti.presentation.screen.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nexters.boolti.domain.usecase.GetUserUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserUseCase: GetUserUsecase,
) : ViewModel() {

}
