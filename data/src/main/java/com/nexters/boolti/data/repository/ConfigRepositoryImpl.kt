package com.nexters.boolti.data.repository

import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.data.datasource.PolicyDataSource
import com.nexters.boolti.data.datasource.RemoteConfigDataSource
import com.nexters.boolti.domain.extension.json
import com.nexters.boolti.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

internal class ConfigRepositoryImpl @Inject constructor(
    private val remoteConfigDataSource: RemoteConfigDataSource,
    private val policyDataSource: PolicyDataSource,
) : ConfigRepository {
    override fun shouldUpdate(): Flow<Boolean> = flow {
        val required = remoteConfigDataSource.getBoolean(KEY_UPDATE_REQUIRED, false)
        if (required) {
            remoteConfigDataSource.getString(KEY_MIN_VERSION)?.let { minVersion ->
                val currentVersion = BuildConfig.APP_VERSION
                emit(checkMinVersion(currentVersion, minVersion))
            } ?: run {
                Timber.tag("mangbaam_ConfigRepositoryImpl").d("shouldUpdate: min version 가져오기 실패")
                emit(false)
            }
        } else {
            Timber.tag("mangbaam_ConfigRepositoryImpl").d("shouldUpdate: 업데이트 필요 X")
            emit(false)
        }
    }

    override suspend fun cacheRefundPolicy() {
        val refundPolicyJson = remoteConfigDataSource.getString(KEY_REFUND_POLICY, "[]")
        val refundPolicy = json.decodeFromString<List<String>>(refundPolicyJson)
        policyDataSource.cacheRefundPolicy(refundPolicy)
    }

    override val refundPolicy: Flow<List<String>>
        get() = policyDataSource.refundPolicy

    companion object {
        const val KEY_UPDATE_REQUIRED = "UpdateRequired"
        const val KEY_MIN_VERSION = "MinVersion"
        const val KEY_REFUND_POLICY = "RefundPolicy"

        /**
         * @return true 이면 업데이트 필요
         */
        fun checkMinVersion(currentVersion: String, minVersion: String): Boolean {
            Timber.tag("mangbaam_ConfigRepositoryImpl").d("checkMinVersion: current: $currentVersion, min: $minVersion")
            if (!Regex("""^\d+\.\d+(\.\d+)?$""").matches(currentVersion)) return false
            if (!Regex("""^\d+\.\d+(\.\d+)?$""").matches(minVersion)) return false

            return currentVersion.split('.').map(String::toInt).let {
                val min = minVersion.split('.').map(String::toInt)
                !KotlinVersion(it[0], it[1], it.getOrNull(2) ?: 0)
                    .isAtLeast(min[0], min[1], min.getOrNull(2) ?: 0)
            }
        }
    }
}
