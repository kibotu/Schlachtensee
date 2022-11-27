package net.kibotu.schlachtensee.ui.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.kibotu.resourceextension.resLong
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.databinding.FragmnetSplashBinding
import net.kibotu.schlachtensee.ui.base.ViewBindingFragment

/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

class SplashFragment : ViewBindingFragment<FragmnetSplashBinding>() {

    override val inflate: (LayoutInflater, ViewGroup?, Boolean) -> FragmnetSplashBinding =
        FragmnetSplashBinding::inflate

    private val navigate by lazy {
        Runnable {
            navigateToNextScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        view?.postDelayed(navigate, R.integer.splash_duration.resLong)
    }

    override fun onPause() {
        super.onPause()
        view?.removeCallbacks(navigate)
    }

    private fun navigateToNextScreen() =
        findNavController().navigate(R.id.action_splash_to_current_temperature)

}