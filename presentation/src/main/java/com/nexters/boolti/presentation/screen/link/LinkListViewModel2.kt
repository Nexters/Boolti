package com.nexters.boolti.presentation.screen.link

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LinkListViewModel2 @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(LinkListState2())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<LinkListEvent2>()
    val event = _event.receiveAsFlow()


    fun save() {}
    fun tryBack() {}
}
