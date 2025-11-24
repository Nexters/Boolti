package com.nexters.boolti.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nexters.boolti.data.db.converter.LocalDateTimeConverter
import com.nexters.boolti.data.db.dao.SearchHistoryDao
import com.nexters.boolti.data.db.entity.SearchHistoryEntity

@Database(
    entities = [SearchHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class BooltiDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        const val DB_NAME = "boolti_database"
    }
}
