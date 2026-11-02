package com.nexters.boolti.presentation.screen.navigation

import com.nexters.boolti.domain.model.UserCode
import kotlinx.serialization.Serializable

sealed interface VideoListRoute {
    @Serializable
    data class VideoList(
        val userCode: UserCode,
        val isEditMode: Boolean,
    ) : VideoListRoute

    /**
     * @param localId null이면 새 동영상 추가
     * @param closeListOnBack 빈 목록이라 자동으로 열렸을 때 true. 뒤로 가면 목록까지 닫는다
     */
    @Serializable
    data class VideoEdit(
        val localId: String? = null,
        val url: String = "",
        val closeListOnBack: Boolean = false,
    ) : VideoListRoute
}
