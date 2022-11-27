package net.kibotu.schlachtensee.viewmodels

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.flow
import net.kibotu.logger.Logger
import net.kibotu.resourceextension.stringFromAssets
import net.kibotu.schlachtensee.models.app.Temperature
import net.kibotu.schlachtensee.models.yearly.TemperatureHistory
import net.kibotu.schlachtensee.services.network.RequestProvider
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

    /**
     * This is the job for all coroutines started by this ViewModel.
     * Cancelling this will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by this ViewModel.
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling viewModelJob.cancel()
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val requestProvider by inject<RequestProvider>()

    val temperatures = flow {

        val offline = loadOfflineData()
        emit(offline)

        // val t = requestProvider.schlachtenseeApi.lastTemperature()
        // Log.v("Temperature", "t=${t.body()}")

        (loadDaily() ?: loadYearly())
            ?.let { emit(it) }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    @WorkerThread
    private suspend fun loadDaily(): Temperature? = try {
        requestProvider.schlachtenseeApi.dailyTemperature().toTemperature("daily")
    } catch (e: Exception) {
        Logger.e(e)
        null
    }

    @WorkerThread
    private suspend fun loadYearly(): Temperature? = try {
        requestProvider.schlachtenseeApi.yearlyTemperature().toTemperature("yearly")
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