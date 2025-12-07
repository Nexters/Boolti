package com.nexters.boolti.data.di

import android.content.Context
import androidx.room.Room
import com.nexters.boolti.data.db.BooltiDatabase
import com.nexters.boolti.data.db.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object DatabaseModule {
    @Singleton
    @Provides
    fun provideBooltiDatabase(@ApplicationContext context: Context): BooltiDatabase {
        return Room.databaseBuilder(
            context,
            BooltiDatabase::class.java,
            BooltiDatabase.DB_NAME,
        ).build()
    }

    @Singleton
    @Provides
    fun provideSearchHistoryDao(database: BooltiDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }
}
