package com.nexters.boolti.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.exception.QrErrorType
import com.nexters.boolti.domain.exception.QrScanException
import com.nexters.boolti.domain.repository.HostRepository
import com.nexters.boolti.domain.request.QrScanRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class QrScanViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val hostRepository: HostRepository,
) : ViewModel() {
    private var lastCode: String? = null // 테스트 코드: wkjai-qoxzaz

    private val showId: String = requireNotNull(savedStateHandle["showId"])
    val showName: String = requireNotNull(savedStateHandle["showName"])

    private val _eventChannel = Channel<QrScanEvent>()
    val event = _eventChannel.receiveAsFlow()

    private fun requestEntrance(entryCode: String) {
        viewModelScope.launch {
            hostRepository.requestEntrance(
                QrScanRequest(showId = showId, entryCode = entryCode)
            ).catch { e ->
                when (e) {
                    is QrScanException -> {
                        e.errorType?.let { type ->
                            event(QrScanEvent.ScanError(type))
                        }
                    }
                }
            }.singleOrNull()?.let { success ->
                event(QrScanEvent.ScanSuccess)
            }
        }
    }

    fun scan(entryCode: String) {
        if (entryCode != lastCode) {
            lastCode = entryCode
            Timber.tag("mangbaam_QrScanActivity").d("스캔 결과: $entryCode")
            requestEntrance(entryCode)
        }
    }

    private fun event(event: QrScanEvent) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }
}

sealed interface QrScanEvent {
    data object ScanSuccess : QrScanEvent
    data class ScanError(val errorType: QrErrorType) : QrScanEvent
}
