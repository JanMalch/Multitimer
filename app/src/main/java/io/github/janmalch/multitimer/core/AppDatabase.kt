package io.github.janmalch.multitimer.core

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

object AppTypeConverters {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilliseconds()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

}

@TypeConverters(AppTypeConverters::class)
@Database(
    entities = [Event::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        fun create(context: Context, name: String = "app-database") = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            name = name,
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}