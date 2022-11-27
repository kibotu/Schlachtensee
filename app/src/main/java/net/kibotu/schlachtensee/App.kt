package net.kibotu.schlachtensee

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp
import net.kibotu.resourceextension.resBoolean
import net.kibotu.schlachtensee.extensions.initLogger
import net.kibotu.schlachtensee.koin.remoteDataSourceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        initLogger()
        insertKoin()
    }

    private fun insertKoin() {
        startKoin {
            when (R.bool.enable_logging.resBoolean) {
                true -> AndroidLogger()
                else -> EmptyLogger()
            }
            androidContext(this@App)
            modules(
                remoteDataSourceModule
            )
        }
    }
}