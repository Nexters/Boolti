package com.nexters.boolti.presentation.screen.qr

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.HostRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostedShowViewModel @Inject constructor(
    private val repository: HostRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(HostedShowState.EMPTY)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch(recordExceptionHandler) {
            repository.getHostedShows()
                .onStart {
                    _uiState.update { it.copy(loading = true, error = false) }
                }.catch { e ->
                    _uiState.update { it.copy(loading = false, error = true) }
                    throw e
                }.singleOrNull()?.let { shows ->
                    _uiState.update { it.copy(loading = false, shows = shows) }
                }
        }
    }
}
