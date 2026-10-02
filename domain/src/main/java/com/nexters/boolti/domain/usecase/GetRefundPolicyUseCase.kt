package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRefundPolicyUsecase @Inject constructor(
    private val configRepository: ConfigRepository,
) {
    operator fun invoke(): Flow<List<String>> = configRepository.refundPolicy
}
