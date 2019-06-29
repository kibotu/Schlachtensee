package net.kibotu.schlachtensee.ui.temperature

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.exozet.android.core.extensions.onClick
import com.exozet.android.core.gson.toJson
import kotlinx.android.synthetic.main.fragment_current_temperature.*
import kotlinx.android.synthetic.main.waves.*
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import net.kibotu.logger.Logger.logv
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.ui.base.BaseFragment
import net.kibotu.schlachtensee.viewmodels.SchlachtenseeApiViewModel
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

class CurrentTemperatureFragment : BaseFragment() {

    override val layout = R.layout.fragment_current_temperature

    override val hasLightStatusBar = true

    override val isFullScreen = true

    val schlachtenseeApiViewModel by inject<SchlachtenseeApiViewModel>()

    val random = Random()

    override fun subscribeUi() {
        super.subscribeUi()

        /**
         * e.g.: 2019-05-11 00:59:59
         */
        val format = "yyyy-MM-dd"
        val formatter = SimpleDateFormat(format, Locale.GERMANY)

        schlachtenseeApiViewModel.temperatures.observe(this, Observer {
            logv { "temperature ${it.toJson()}" }

            val now = formatter.format(Date())

            val temperature = it.templist?.value?.firstOrNull { it.date?.startsWith(now) ?: false }?.wert?.toFloat()

            val last = it.templist?.value?.lastOrNull()?.wert?.toFloat() ?: 0f

            val t = temperature ?: last

            val temperatures = it.templist?.value?.mapNotNull { it.wert?.toFloat() }
            val min = temperatures?.min() ?: 0f
            val max = temperatures?.max() ?: 0f

            thermometer.minScaleValue = min
            thermometer.maxScaleValue = max + 5
            logv { "temperature=$temperature last=$last => $t min=$min max$max" }

            thermometer.setValueAndStartAnim(t)
        })

        thermometer.curScaleValue = 10f

        thermometer.onClick {
            //            thermometer.setValueAndStartAnim(random.nextFloat() * thermometer.maxScaleValue)
        }

        fluid.enableCalming = true

        fluid.onShakeEnded = Runnable {
            fluid.onShake(random.nextFloat() * 0.7f + 0.3f)
        }

        wave1.start()
        wave2.start()
        wave3.start()

        startFish()
    }

    private fun startFish() {
        val tickerChannel = ticker(delayMillis = 10_000, initialDelayMillis = 10_000)

        lifecycleScope.launch {
            for (event in tickerChannel) {
                fish.cancelAnimation()
                fish.playAnimation()
            }
        }
    }

    private fun shakingFluid() {
        val tickerChannel = ticker(delayMillis = 1_000, initialDelayMillis = 0)

        fluid.enableCalming = true

        lifecycleScope.launch {
            for (event in tickerChannel) { // event is of type Unit, so we don't really care about it
                fluid.onShake(random.nextFloat())
            }
        }
    }

    override fun unsubscribeUi() {
        super.unsubscribeUi()
        schlachtenseeApiViewModel.temperatures.removeObservers(this)

        wave1.stop()
        wave2.stop()
        wave3.stop()
    }
}