package com.nexters.boolti.data.repository

import android.util.Log
import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.data.datasource.RemoteConfigDataSource
import com.nexters.boolti.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val remoteConfigDataSource: RemoteConfigDataSource,
) : ConfigRepository {
    override fun shouldUpdate(): Flow<Boolean> = flow {
        val required = remoteConfigDataSource.getBoolean(KEY_UPDATE_REQUIRED, false)
        if (required) {
            remoteConfigDataSource.getString(KEY_MIN_VERSION)?.let { minVersion ->
                val currentVersion = BuildConfig.APP_VERSION
                emit(checkMinVersion(currentVersion, minVersion))
            } ?: run {
                Log.d("mangbaam_ConfigRepositoryImpl", "shouldUpdate: min version 가져오기 실패")
                emit(false)
            }
        } else {
            Log.d("mangbaam_ConfigRepositoryImpl", "shouldUpdate: 업데이트 필요 X")
            emit(false)
        }
    }

    companion object {
        const val KEY_UPDATE_REQUIRED = "UpdateRequired"
        const val KEY_MIN_VERSION = "MinVersion"

        /**
         * @return true 이면 업데이트 필요
         */
        fun checkMinVersion(currentVersion: String, minVersion: String): Boolean {
            Log.d("mangbaam_ConfigRepositoryImpl", "checkMinVersion: current: $currentVersion, min: $minVersion")
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
