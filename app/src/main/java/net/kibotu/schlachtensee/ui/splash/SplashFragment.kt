package net.kibotu.schlachtensee.ui.splash

import androidx.navigation.fragment.findNavController
import com.exozet.android.core.extensions.resLong
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.ui.base.BaseFragment

/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

class SplashFragment : BaseFragment() {

    override val layout = R.layout.fragmnet_splash

    val navigate = Runnable {
        navigateToNextScreen()
    }

    override fun onResume() {
        super.onResume()
        view?.postDelayed(navigate, R.integer.splash_duration.resLong)
    }

    override fun onPause() {
        super.onPause()
        view?.removeCallbacks(navigate)
    }

    private fun navigateToNextScreen() = findNavController().navigate(R.id.action_splash_to_current_temperature)
}