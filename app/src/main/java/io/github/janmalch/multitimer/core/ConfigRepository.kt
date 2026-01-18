package io.github.janmalch.multitimer.core

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.janmalch.multitimer.models.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import io.github.janmalch.multitimer.models.Timer
import java.io.InputStream
import java.io.OutputStream

private object AppConfigSerializer : Serializer<AppConfig> {
    override val defaultValue: AppConfig = AppConfig.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AppConfig {
        try {
            return AppConfig.parseFrom(input)
        } catch (exception: IOException) {
            throw CorruptionException("Cannot read proto of AppConfigSerializer.", exception)
        }
    }

    override suspend fun writeTo(t: AppConfig, output: OutputStream) = t.writeTo(output)
}


private val Context.appConfigStore by dataStore(
    fileName = "app_config.pb",
    serializer = AppConfigSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler { AppConfigSerializer.defaultValue },
)

class ConfigRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val timers: Flow<List<Timer>> =
        context.appConfigStore.data.map { it.timerList.orEmpty() }.distinctUntilChanged()

}
