package net.kibotu.schlachtensee.ui.temperature

import com.exozet.android.core.extensions.onClick
import kotlinx.android.synthetic.main.fragment_current_temperature.*
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.ui.base.BaseFragment
import kotlin.random.Random


/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

class CurrentTemperatureFragment : BaseFragment() {

    override val layout = R.layout.fragment_current_temperature

    override fun subscribeUi() {
        super.subscribeUi()

        thermometer.onClick {
            thermometer.setValueAndStartAnim(Random.nextFloat() * 7 + 35)
        }
    }

    override fun unsubscribeUi() {
        super.unsubscribeUi()
    }
}