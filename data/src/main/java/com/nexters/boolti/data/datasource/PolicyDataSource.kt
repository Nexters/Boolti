package com.nexters.boolti.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.nexters.boolti.data.db.AppSettings
import com.nexters.boolti.data.db.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class PolicyDataSource @Inject constructor(
    private val context: Context,
) {
    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    suspend fun cacheRefundPolicy(refundPolicy: List<String>) {
        dataStore.updateData {
            it.copy(refundPolicy = refundPolicy)
        }
    }

    val refundPolicy: Flow<List<String>>
        get() = data.map { it.refundPolicy }
}
