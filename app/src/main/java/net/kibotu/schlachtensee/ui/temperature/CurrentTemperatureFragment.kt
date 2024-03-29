package net.kibotu.schlachtensee.ui.temperature

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kibotu.logger.Logger
import net.kibotu.resourceextension.dp
import net.kibotu.schlachtensee.databinding.FragmentCurrentTemperatureBinding
import net.kibotu.schlachtensee.extensions.setOnClickListenerThrottled
import net.kibotu.schlachtensee.ui.base.ViewBindingFragment
import net.kibotu.schlachtensee.viewmodels.SchlachtenseeApiViewModel
import java.util.*


/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

class CurrentTemperatureFragment : ViewBindingFragment<FragmentCurrentTemperatureBinding>() {

    override val inflate: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCurrentTemperatureBinding =
        FragmentCurrentTemperatureBinding::inflate

    private val viewModel: SchlachtenseeApiViewModel by viewModels()

    private val random by lazy { Random() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = requireNotNull(binding)

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.temperatures.collectLatest {

                Logger.v("t=$it")

                withContext(Dispatchers.Main) {
                    binding.thermometer.minScaleValue = it.minScaleValue
                    binding.thermometer.maxScaleValue = it.maxScaleValue
                    binding.thermometer.setValueAndStartAnim(it.temperature)
                }
            }
        }

        binding.thermometer.curScaleValue = 10f

        binding.thermometer.setOnClickListenerThrottled {
            viewLifecycleOwner.lifecycleScope.launch {

                val t = viewModel.currentTemperature() ?: return@launch
                withContext(Dispatchers.Main) {
                    binding.thermometer.setValueAndStartAnim(t)
                }
            }
        }

        binding.fluid.enableCalming = true

        binding.fluid.onShakeEnded = Runnable {
            binding.fluid.onShake(random.nextFloat() * 0.7f + 0.3f)
        }

        binding.waveLayout.wave1.start()
        binding.waveLayout.wave2.start()
        binding.waveLayout.wave3.start()

        startFish()

        binding.direction.setOnClickListenerThrottled {
            // @52.4659522,13.1487598,11z
            // https://developers.google.com/maps/documentation/urls/android-intents#kotlin
            val uri = Uri.parse("http://maps.google.com/maps?daddr=52.439978,13.2126509")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

            binding.direction.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = insets.bottom + 16.dp
            }

            binding.thermometer.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = insets.bottom + 16.dp
            }

            WindowInsetsCompat.CONSUMED
        }

        // https://developer.android.com/develop/ui/views/layout/edge-to-edge
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
//            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
//            // Apply the insets as padding to the view. Here we're setting all of the
//            // dimensions, but apply as appropriate to your layout. You could also
//            // update the views margin if more appropriate.
//            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
//
//            // Return CONSUMED if we don't want the window insets to keep being passed
//            // down to descendant views.
//            WindowInsetsCompat.CONSUMED
//        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun startFish() {
        val tickerChannel = ticker(delayMillis = 10_000, initialDelayMillis = 10_000)

        lifecycleScope.launch {
            for (event in tickerChannel) {
                binding?.fish?.cancelAnimation()
                binding?.fish?.playAnimation()
            }
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun shakingFluid() {
        val tickerChannel = ticker(delayMillis = 1_000, initialDelayMillis = 0)

        binding?.fluid?.enableCalming = true

        lifecycleScope.launch {
            for (event in tickerChannel) { // event is of type Unit, so we don't really care about it
                binding?.fluid?.onShake(random.nextFloat())
            }
        }
    }

    override fun onDestroyView() {
        binding?.waveLayout?.wave1?.stop()
        binding?.waveLayout?.wave2?.stop()
        binding?.waveLayout?.wave3?.stop()
        binding = null
        super.onDestroyView()
    }
}