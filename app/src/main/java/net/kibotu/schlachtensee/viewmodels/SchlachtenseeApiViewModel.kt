package net.kibotu.schlachtensee.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exozet.android.core.extensions.inject
import com.exozet.android.core.extensions.stringFromAssets
import kotlinx.coroutines.*
import net.kibotu.logger.Logger
import net.kibotu.schlachtensee.models.TemperatureHistory
import net.kibotu.schlachtensee.services.network.RequestProvider
import org.simpleframework.xml.core.Persister

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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val requestProvider by inject<RequestProvider>()

    val temperatures = MutableLiveData<TemperatureHistory>()

    fun loadYearly() {

        loadOfflineData()

        uiScope.launch {

            val result: TemperatureHistory? = withContext(Dispatchers.IO) {
                try {
                    requestProvider.schlachtenseeApi.yearlyTemperature()
                } catch (e: Exception) {
                    Logger.e(e)
                    null
                }
            } ?: return@launch

            temperatures.value = result
        }

    }

    fun loadOfflineData() {

        uiScope.launch {

            val result: TemperatureHistory? = withContext(Dispatchers.IO) {
                try {
                    val xml = "estimated_temperatures.xml".stringFromAssets()
                    Persister().read(TemperatureHistory::class.java, xml, true)
                } catch (e: Exception) {
                    Logger.e(e)
                    null
                }
            } ?: return@launch

            temperatures.value = result
        }
    }

    init {
        loadYearly()
    }
}