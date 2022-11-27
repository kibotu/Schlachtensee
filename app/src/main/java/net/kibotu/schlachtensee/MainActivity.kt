package net.kibotu.schlachtensee

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import net.kibotu.logger.Logger
import net.kibotu.schlachtensee.databinding.ActivityMainBinding
import net.kibotu.schlachtensee.ui.base.ViewBindingActivity


class MainActivity : ViewBindingActivity<ActivityMainBinding>() {

    override val inflate: (LayoutInflater, ViewGroup?, Boolean) -> ActivityMainBinding =
        ActivityMainBinding::inflate

    var newIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        newIntent = intent
        Logger.i("[onCreate] savedInstanceState=$savedInstanceState intent=$newIntent")
    }

    // region intent handling
}