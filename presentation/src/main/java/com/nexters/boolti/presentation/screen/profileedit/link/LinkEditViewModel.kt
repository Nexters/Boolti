package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nexters.boolti.presentation.screen.linkId
import com.nexters.boolti.presentation.screen.linkTitle
import com.nexters.boolti.presentation.screen.url
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LinkEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val editLinkId = savedStateHandle[linkId] ?: ""
    private val _uiState = MutableStateFlow(
        LinkEditState(
            linkName = savedStateHandle[linkTitle] ?: "",
            url = savedStateHandle[url] ?: "",
        )
    )
    val uiState = _uiState.asStateFlow()

    fun complete() {}

    fun onChangeLinkName(name: String) {
        _uiState.update { it.copy(linkName = name) }
    }

    fun onChangeLinkUrl(url: String) {
        _uiState.update { it.copy(url = url) }
    }

    fun remove() {

    }
}
