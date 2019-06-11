package net.kibotu.schlachtensee

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.exozet.android.core.extensions.*
import com.exozet.android.core.interfaces.BackPress
import com.exozet.android.core.interfaces.DispatchTouchEventHandler
import com.google.firebase.iid.FirebaseInstanceId
import com.roger.catloadinglibrary.CatLoadingView
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.logger.Logger.logi
import net.kibotu.logger.Logger.logv
import net.kibotu.logger.Logger.logw
import net.kibotu.logger.TAG
import net.kibotu.schlachtensee.services.storage.LocalUser
import net.kibotu.schlachtensee.ui.base.BaseFragment
import net.kibotu.schlachtensee.viewmodels.AppViewModel
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    @LayoutRes
    val layout = R.layout.activity_main

    val appViewModel by inject<AppViewModel>()

    val navController by inject<NavController>()

    val localUser by inject<LocalUser>()

    private val currentFragment: Fragment?
        get() = navHost.childFragmentManager.findFragmentById(R.id.navHost)

    var newIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            application.unlockScreen()

            if (!resources.getBoolean(R.bool.flag_keep_screen_on)) {
                setKeepOnScreenFlag()
            }
        }
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        newIntent = intent
        logi("[onCreate] savedInstanceState=$savedInstanceState intent=$newIntent")

        setContentView(layout)

        NavigationUI.setupWithNavController(bottomNavBar, navController)

        addOnDestinationChangedListener()
        addBackStackChangeListener()
        addFcmTokenListener()
        appViewModel.isLoading.observe(this, onLoading)
    }

    override fun onResume() {
        super.onResume()

        if (!checkGooglePlayServices()) {
            logw("checkGooglePlayServices failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (BuildConfig.DEBUG && BuildConfig.IS_LOCAL) {
            logv("[onTouchEvent] currentFocus=${this@MainActivity.currentFocus} event=$event")
        }

        return super.onTouchEvent(event)
    }

    // region touch handler

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val fragment = currentFragment
        return when {
            fragment is DispatchTouchEventHandler && ev != null -> fragment.dispatchTouchEvent(ev) || super.dispatchTouchEvent(
                ev
            )
            else -> super.dispatchTouchEvent(ev)
        }
    }

    // endregion

    // region back press
    override fun onBackPressed() {

        hideKeyboard()

        val fragment = currentFragment
        when {
            fragment is BackPress && fragment.consumeBackPress() -> return
            else -> super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp() || super.onSupportNavigateUp()

    // endregion

    // region OnBackStackChangedListener

    private fun addBackStackChangeListener() {
        navHost.childFragmentManager.addOnBackStackChangedListener {
            val fragment = currentFragment
            logv("onBackStackChanged currentFragment=${fragment?.TAG}")
            if (fragment is FragmentManager.OnBackStackChangedListener) {
                fragment.onBackStackChanged()
            }
        }
    }

    private fun addOnDestinationChangedListener() =
        navController.addOnDestinationChangedListener { _, destination, _ ->
            logv("[onDestinationChange] id=${destination.id.resName} label=${destination.label}")
        }

    // endregion

    // region loading spinner

    val onLoading = Observer<Boolean> { isLoading ->
        logv("[OnLoading] isLoading=$isLoading")

        if (BuildConfig.DEBUG) {
            if (isLoading) {
                if (!loader.isShowing) {
                    loader?.dismiss()
                    loader = CatLoadingView()
                    loader?.isCancelable = false
                    loader?.show(navHost.childFragmentManager, "catloader")
                }
            } else
                loader?.dismiss()
        } else {
            loaderView.gone(!isLoading)
            progressBar.indeterminateDrawableColor(R.color.white)
        }
    }

    var loader: CatLoadingView? = null

    // endregion

    // region fcm token

    private fun addFcmTokenListener() {
//        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) {
//            localUser.fcmToken = it.token
//        }
    }

    // endregion

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        setTransparentStatusBarFlags()

        if (hasFocus && (currentFragment as? BaseFragment)?.isFullScreen == true) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    // region intent handling

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        logv("[onNewIntent] $intent")

        newIntent = intent

        consumeIntent()
    }

    private fun consumeIntent(): Boolean {

        logv("[consumeIntent] ${newIntent?.extras?.string}")

        val isConsumed: Boolean

        val intentOrigin = newIntent?.getStringExtra(Intent::class.java.simpleName)
        if (newIntent == null || intentOrigin == null)
            return false

        isConsumed = when (intentOrigin) {
            "assessment_notification" -> {
                true

            }
            else -> false
        }

        newIntent = null

        return isConsumed
    }

    // endregion
}