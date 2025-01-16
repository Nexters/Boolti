package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.Popup
import com.nexters.boolti.domain.repository.PopupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPopupUseCase @Inject constructor(
    private val popupRepository: PopupRepository,
) {
    operator fun invoke(): Flow<Popup> = popupRepository
        .getPopup()
        .filter { popup ->
            popup is Popup.Notice || popupRepository.shouldShowEvent().first()
        }
}