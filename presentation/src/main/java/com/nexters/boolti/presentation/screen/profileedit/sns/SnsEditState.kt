package com.nexters.boolti.presentation.screen.profileedit.sns

import com.nexters.boolti.domain.model.Sns

data class SnsEditState(
    val snsId: String? = null,
    val selectedSns: Sns.SnsType = Sns.SnsType.INSTAGRAM,
    val username: String,
    val inUseSnsTypes: List<Sns.SnsType> = emptyList(),
) {
    val isEditMode: Boolean
        get() = snsId != null
    val usernameHasError: Boolean
        get() = when (selectedSns) {
            Sns.SnsType.INSTAGRAM -> username.contains(Regex("[^0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣._]+"))
            Sns.SnsType.YOUTUBE -> username.contains(Regex("[^0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣._-]+"))
        }
}
