package com.nexters.boolti.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.nexters.boolti.data.network.api.PopupService
import com.nexters.boolti.data.network.response.PopupResponse
import javax.inject.Inject

internal class PopupDataSource @Inject constructor(
    private val service: PopupService,
    private val context: Context,
) {
//    private val dataStore: DataStore<Preferences> by context.pre

    suspend fun getPopup(): PopupResponse = service.getPopup()
    suspend fun shouldShowPopup(): Boolean {
        return true
    }

    suspend fun hidePopupToday() {

    }
}