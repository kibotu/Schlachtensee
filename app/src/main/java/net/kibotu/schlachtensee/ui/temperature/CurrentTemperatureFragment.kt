package net.kibotu.schlachtensee.ui.temperature

import androidx.lifecycle.observe
import com.exozet.android.core.extensions.onClick
import com.exozet.android.core.extensions.toJson
import kotlinx.android.synthetic.main.fragment_current_temperature.*
import net.kibotu.logger.Logger.logv
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.ui.base.BaseFragment
import net.kibotu.schlachtensee.viewmodels.SchlachtenseeApiViewModel
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

class CurrentTemperatureFragment : BaseFragment() {

    override val layout = R.layout.fragment_current_temperature

    val schlachtenseeApiViewModel by inject<SchlachtenseeApiViewModel>()

    override fun subscribeUi() {
        super.subscribeUi()

        /**
         * e.g.: 2019-05-11 00:59:59
         */
        val format = "yyyy-MM-dd"
        val formatter = SimpleDateFormat(format, Locale.GERMANY)

        schlachtenseeApiViewModel.temperatures.observe(this) {
            logv("temperatures ${it.toJson()}")

            val now = formatter.format(Date())

            val temperature = it.templist?.value?.firstOrNull { it.date?.startsWith(now) ?: false }?.wert?.toFloat()

            val last = it.templist?.value?.lastOrNull()?.wert?.toFloat() ?: 0f

            val t = temperature ?: last

            thermometer.setValueAndStartAnim(t)
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