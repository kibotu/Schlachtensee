package net.kibotu.schlachtensee.ui.temperature

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.exozet.android.core.extensions.onClick
import kotlinx.android.synthetic.main.fragment_current_temperature.*
import kotlinx.android.synthetic.main.waves.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import net.kibotu.logger.Logger.logv
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.ui.base.BaseFragment
import net.kibotu.schlachtensee.viewmodels.SchlachtenseeApiViewModel
import org.koin.android.ext.android.inject
import java.util.*


/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

class CurrentTemperatureFragment : BaseFragment() {

    override val layout = R.layout.fragment_current_temperature

    override val hasLightStatusBar = true

    override val isFullScreen = true

    private val schlachtenseeApiViewModel by inject<SchlachtenseeApiViewModel>()

    private val random by lazy { Random() }

    override fun subscribeUi() {
        super.subscribeUi()

        schlachtenseeApiViewModel.temperatures.observe(this, Observer {

            logv { "$it" }

            thermometer.minScaleValue = it.minScaleValue
            thermometer.maxScaleValue = it.maxScaleValue

            thermometer.setValueAndStartAnim(it.temperature)
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

        direction.onClick {
            // @52.4659522,13.1487598,11z
            // https://developers.google.com/maps/documentation/urls/android-intents#kotlin
            val uri = Uri.parse("http://maps.google.com/maps?daddr=52.4659522,13.1487598")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun startFish() {
        val tickerChannel = ticker(delayMillis = 10_000, initialDelayMillis = 10_000)

        lifecycleScope.launch {
            for (event in tickerChannel) {
                fish.cancelAnimation()
                fish.playAnimation()
            }
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
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