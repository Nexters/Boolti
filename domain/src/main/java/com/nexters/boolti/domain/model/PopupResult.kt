package com.nexters.boolti.domain.model

sealed interface PopupResult {
    /** 서버에 등록된 팝업이 없음 */
    data object None : PopupResult

    /** 사용자가 오늘 하루 숨긴 이벤트 */
    data class Hidden(val popup: Popup.Event) : PopupResult

    data class Show(val popup: Popup) : PopupResult
}
