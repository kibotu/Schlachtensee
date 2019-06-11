package net.kibotu.schlachtensee.ui.temperature

import androidx.lifecycle.observe
import com.exozet.android.core.extensions.onClick
import kotlinx.android.synthetic.main.fragment_current_temperature.*
import net.kibotu.logger.Logger.logv
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.ui.base.BaseFragment
import net.kibotu.schlachtensee.viewmodels.SchlachtenseeApiViewModel
import org.koin.android.ext.android.inject
import kotlin.random.Random


/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

class CurrentTemperatureFragment : BaseFragment() {

    override val layout = R.layout.fragment_current_temperature

    val schlachtenseeApiViewModel by inject<SchlachtenseeApiViewModel>()

    override fun subscribeUi() {
        super.subscribeUi()

        schlachtenseeApiViewModel.temperatures.observe(this) {
            logv("temperatures $it")
            thermometer.setValueAndStartAnim(40f)
        }

        thermometer.onClick {
            thermometer.setValueAndStartAnim(Random.nextFloat() * 7 + 35)
        }
    }

    override fun unsubscribeUi() {
        super.unsubscribeUi()
        schlachtenseeApiViewModel.temperatures.removeObservers(this)
    }
}