package net.kibotu.schlachtensee.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exozet.android.core.extensions.inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.kibotu.logger.Logger
import net.kibotu.resourceextension.stringFromAssets
import net.kibotu.schlachtensee.models.app.Temperature
import net.kibotu.schlachtensee.models.yearly.TemperatureHistory
import net.kibotu.schlachtensee.services.network.RequestProvider
import org.simpleframework.xml.core.Persister
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

class SchlachtenseeApiViewModel : ViewModel() {
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val requestProvider by inject<RequestProvider>()

    val temperatures = MutableLiveData<Temperature>()

    fun loadYearly() {

//        loadOfflineData()

        ioScope.launch {

            try {
                val result =
                    getTemperatureFrom(requestProvider.schlachtenseeApi.yearlyTemperature())
                temperatures.postValue(result)
            } catch (e: Exception) {
                Logger.e(e)
            }
        }
    }

    private fun getTemperatureFrom(yearlyTemperature: TemperatureHistory): Temperature {

        /**
         * e.g.: 2019-05-11 00:59:59
         */
        val format = "yyyy-MM-dd"
        val formatter = SimpleDateFormat(format, Locale.GERMANY)

        val now = formatter.format(Date())

        val temperature = yearlyTemperature.templist?.value?.firstOrNull {
            it.date?.startsWith(now) ?: false
        }?.wert?.toFloat()

        val last = yearlyTemperature.templist?.value?.lastOrNull()?.wert?.toFloat() ?: 0f

        val t = temperature ?: last

        val temperatures = yearlyTemperature.templist?.value?.mapNotNull { it.wert?.toFloat() }
        val min = temperatures?.min() ?: 0f
        val max = temperatures?.max() ?: 0f

        return Temperature(min, max + 5, t)
    }

    fun loadOfflineData() {

        ioScope.launch {

            try {
                val xml = "estimated_temperatures.xml".stringFromAssets()
                val temperatureHistory = Persister().read(TemperatureHistory::class.java, xml, true)
                temperatures.postValue(getTemperatureFrom(temperatureHistory))
            } catch (e: Exception) {
                Logger.e(e)
            }
        }
    }

    init {
        loadYearly()
    }
}