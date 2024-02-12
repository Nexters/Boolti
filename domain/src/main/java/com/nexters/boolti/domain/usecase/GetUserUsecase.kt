package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetUserUsecase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): User = runBlocking { authRepository.getUser().first() }
}
