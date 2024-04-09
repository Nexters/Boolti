package com.nexters.boolti.data.datasource

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.get
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class RemoteConfigDataSource(
    private val remoteConfig: FirebaseRemoteConfig,
) {

    suspend fun getValue(key: String): FirebaseRemoteConfigValue? = suspendCoroutine { continuation ->
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(remoteConfig[key].also {
                    Timber.tag("MANGBAAM-RemoteConfigDataSource(getValue)").d(it.asString())
                })
            } else {
                continuation.resume(null)
            }
        }
    }

    suspend fun getString(key: String): String? = getValue(key)?.asString()
    suspend fun getString(key: String, defaultValue: String): String = getValue(key)?.asString() ?: defaultValue

    suspend fun getLong(key: String): Long? = getValue(key)?.asLong()
    suspend fun getLong(key: String, defaultValue: Long): Long = getValue(key)?.asLong() ?: defaultValue

    suspend fun getBoolean(key: String): Boolean? = getValue(key)?.asBoolean()
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = getValue(key)?.asBoolean() ?: defaultValue

    suspend fun getDouble(key: String): Double? = getValue(key)?.asDouble()
    suspend fun getDouble(key: String, defaultValue: Double): Double = getValue(key)?.asDouble() ?: defaultValue
}
