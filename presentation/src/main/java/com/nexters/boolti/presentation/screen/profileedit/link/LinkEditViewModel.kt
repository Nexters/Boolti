package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LinkEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<ProfileRoute.ProfileLinkEdit>()
    val editLinkId = route.linkId ?: ""
    private val _uiState = MutableStateFlow(
        LinkEditState(
            linkName = route.linkTitle ?: "",
            url = route.url ?: "",
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onChangeLinkName(name: String) {
        _uiState.update { it.copy(linkName = name) }
    }

    fun onChangeLinkUrl(url: String) {
        _uiState.update { it.copy(url = url) }
    }
}
