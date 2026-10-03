package com.nexters.boolti.presentation.screen.navigation

import com.nexters.boolti.domain.model.UserCode
import kotlinx.serialization.Serializable

sealed interface LinkListRoute {
    @Serializable
    data class LinkList(
        val userCode: UserCode,
        val isEditMode: Boolean,
    ) : LinkListRoute

    /**
     * @param linkId null이면 새 링크 추가
     * @param closeListOnBack 빈 목록이라 자동으로 열렸을 때 true. 뒤로 가면 목록까지 닫는다
     */
    @Serializable
    data class LinkEdit(
        val linkId: String? = null,
        val name: String = "",
        val url: String = "",
        val closeListOnBack: Boolean = false,
    ) : LinkListRoute
}
