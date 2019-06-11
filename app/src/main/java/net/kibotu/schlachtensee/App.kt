package net.kibotu.schlachtensee

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.exozet.android.core.extensions.installServiceProviderIfNeeded
import com.exozet.android.core.extensions.resBoolean
import net.kibotu.logger.Logger
import net.kibotu.schlachtensee.extensions.initCrashlytics
import net.kibotu.schlachtensee.extensions.initLogger
import net.kibotu.schlachtensee.extensions.subscribePushNotificationTopics
import net.kibotu.schlachtensee.koin.configuration
import net.kibotu.schlachtensee.koin.navigationModule
import net.kibotu.schlachtensee.koin.uiModule
import net.kibotu.schlachtensee.koin.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger

class App : MultiDexApplication() {

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate() {
        super.onCreate()

        insertKoin()

        initLogger()

        initCrashlytics()
        subscribePushNotificationTopics()
        installServiceProviderIfNeeded()
    }

    private fun insertKoin() {
        startKoin {
            when (R.bool.enable_logging.resBoolean) {
                true -> AndroidLogger()
                else -> EmptyLogger()
            }
            androidContext(this@App)
            modules(
                configuration,
                uiModule,
                viewModelModule,
                navigationModule
            )
        }
    }

    override fun onTerminate() {
        @Suppress("DEPRECATION")
        Logger.onTerminate()
        super.onTerminate()
    }
}