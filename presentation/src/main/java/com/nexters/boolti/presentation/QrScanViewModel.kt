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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(
        QrScanState(
            showName = requireNotNull(savedStateHandle["showName"]),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<QrScanEvent>()
    val event = _eventChannel.receiveAsFlow()

    init {
        getManagerCode()
    }

    /**
     * 스캐너가 QR코드를 스캔하면 호출하는 함수
     *
     * @param entryCode 스캔한 QR 의 데이터
     */
    fun scan(entryCode: String) {
        Timber.tag("mangbaam_QrScanActivity").d("스캔 결과: $entryCode")
        requestEntrance(entryCode)
    }

    /**
     * 입장 확인
     */
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
            }.singleOrNull()?.let {
                event(QrScanEvent.ScanSuccess)
            }
        }
    }

    private fun getManagerCode() {
        viewModelScope.launch {
            hostRepository.getManagerCode(showId).catch { e ->
                e.printStackTrace()
            }.singleOrNull()?.let { code ->
                _uiState.update { it.copy(managerCode = code) }
            }
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
