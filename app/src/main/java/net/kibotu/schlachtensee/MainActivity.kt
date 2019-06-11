package net.kibotu.schlachtensee

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.exozet.android.core.extensions.*
import com.google.firebase.iid.FirebaseInstanceId
import com.roger.catloadinglibrary.CatLoadingView
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.logger.Logger.logv
import net.kibotu.schlachtensee.services.storage.LocalUser
import net.kibotu.schlachtensee.viewmodels.AppViewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    @LayoutRes
    val layout = R.layout.activity_main

    val appViewModel by inject<AppViewModel>()

    val navController by inject<NavController>()

    val localUser by inject<LocalUser>()

    override fun onCreate(savedInstanceState: Bundle?) {

        if (BuildConfig.DEBUG) {
            application.unlockScreen()

            if (!resources.getBoolean(R.bool.flag_keep_screen_on)) {
                setKeepOnScreenFlag()
            }
        }
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        setContentView(layout)

        NavigationUI.setupWithNavController(bottomNavBar, navController)

        addFcmTokenListener()
        appViewModel.isLoading.observe(this, onLoading)
    }
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
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) {
            localUser.fcmToken = it.token
        }
    }

    // endregion
}