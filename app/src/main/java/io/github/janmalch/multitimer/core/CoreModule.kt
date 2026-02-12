package io.github.janmalch.multitimer.core

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.TimeZone
import javax.inject.Singleton
import kotlin.random.Random
import kotlin.time.Clock

@InstallIn(SingletonComponent::class)
@Module
object CoreModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.create(context)

    @Provides
    @Singleton
    fun providesEventDao(db: AppDatabase): EventDao = db.eventDao()

    @Provides
    @Singleton
    fun providesClock(): Clock = Clock.System

    @Provides
    @Singleton
    fun providesTimeZone(): TimeZone = TimeZone.currentSystemDefault()
}