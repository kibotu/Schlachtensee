package net.kibotu.schlachtensee.viewmodels

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import net.kibotu.logger.Logger
import net.kibotu.resourceextension.stringFromAssets
import net.kibotu.schlachtensee.models.app.Temperature
import net.kibotu.schlachtensee.models.yearly.TemperatureHistory
import net.kibotu.schlachtensee.services.network.SchlachtenseeApi
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.simpleframework.xml.core.Persister
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

@OptIn(KoinApiExtension::class)
class SchlachtenseeApiViewModel : ViewModel(), KoinComponent {

    private val api: SchlachtenseeApi by inject()

    val temperatures = flow {

        val offline = loadOfflineData()
        emit(offline)

        val response = kotlin.runCatching { api.lastTemperature() }
        Logger.v("Temperature", "t=${response}")

        (loadDaily() ?: loadYearly())
            ?.let { emit(it) }
    }.flowOn(Dispatchers.IO)

    suspend fun currentTemperature(): Float? = withContext(Dispatchers.IO) {
        runCatching { api.lastTemperature().toFloat() }.getOrNull()
    }

    @WorkerThread
    private suspend fun loadDaily(): Temperature? = try {
        api.dailyTemperature().toTemperature("daily")
    } catch (e: Exception) {
        Logger.e(e)
        null
    }

    @WorkerThread
    private suspend fun loadYearly(): Temperature? = try {
        api.yearlyTemperature().toTemperature("yearly")
    } catch (e: Exception) {
        Logger.e(e)
        null
    }

    @WorkerThread
    private suspend fun loadOfflineData(): Temperature = Persister().read(
        TemperatureHistory::class.java,
        "estimated_temperatures.xml".stringFromAssets(),
        true
    ).toTemperature("offline")

    companion object {

        /**
         * e.g.: 2019-05-11 00:59:59
         */
        private const val format = "yyyy-MM-dd"

        private val dateFormatter by lazy { SimpleDateFormat(format, Locale.GERMANY) }

        private fun TemperatureHistory.toTemperature(source: String): Temperature {

            val now = dateFormatter.format(Date())

            val temperature = templist?.value?.firstOrNull {
                it.date?.startsWith(now) ?: false
            }?.wert?.toFloat()

            val last = templist?.value?.lastOrNull()?.wert?.toFloat() ?: 0f

            val t = temperature ?: last

            val temperatures = templist?.value?.mapNotNull { it.wert?.toFloat() }
            val min = temperatures?.min() ?: 0f
            val max = temperatures?.max() ?: 0f

            return Temperature(min, max + 5, t, source)
        }
    }
}