@file:JvmName("ApplicationExtensions")

package net.kibotu.schlachtensee.extensions

import android.app.Application
import android.content.res.Configuration
import com.crashlytics.android.Crashlytics
import com.exozet.android.core.extensions.initStrictMode
import com.exozet.android.core.extensions.inject
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.processphoenix.ProcessPhoenix
import io.fabric.sdk.android.Fabric
import net.kibotu.ContextHelper
import net.kibotu.logger.LogcatLogger
import net.kibotu.logger.Logger
import net.kibotu.logger.WebLogger
import net.kibotu.schlachtensee.BuildConfig
import net.kibotu.schlachtensee.services.storage.AppConfiguration
import net.kibotu.schlachtensee.services.storage.LocalUser

fun Application.initCrashlytics() {

    val localUser by inject<LocalUser>()

    if (!localUser.isTrackingAllowed && !BuildConfig.IS_LOCAL)
        return

    FirebaseApp.initializeApp(this)
    Fabric.with(this, Crashlytics())

    Crashlytics.setString("version", BuildConfig.CANONICAL_VERSION_NAME)
    Crashlytics.setString(
        "git-files",
        "${BuildConfig.VSC}${VSC.Github.filesUrl}/${BuildConfig.COMMIT_HASH}"
    )
    Crashlytics.setString(
        "git-commit",
        "${BuildConfig.VSC}${VSC.Github.commitUrl}/${BuildConfig.COMMIT_HASH}"
    )
}

fun Application.subscribePushNotificationTopics() {
    FirebaseMessaging.getInstance().subscribeToTopic(packageName)
    FirebaseMessaging.getInstance().subscribeToTopic("$packageName.android")
    if (BuildConfig.IS_LOCAL)
        FirebaseMessaging.getInstance().subscribeToTopic("$packageName.development")
}

fun Application.initLogger() {

    val appConfiguration by inject<AppConfiguration>()

    if (appConfiguration.enableLogging) {
        Logger.addLogger(LogcatLogger())
        Logger.addLogger(WebLogger())
    }

//    Logger.addLogger(CrashlyticsLogger(), Level.VERBOSE)
}

fun Application.initLeakCanary(): Boolean {

    initStrictMode()

    return false
}

// region build info

enum class VSC(val commitUrl: String, val filesUrl: String) {
    Github("/commits", "/src"),
    Gitlab("/commits", "/tree")
}

// endregion

/**
 * [determine-if-the-device-is-a-smartphone-or-tablet](http://stackoverflow.com/a/18387977)
 */
val isTablet: Boolean =
    ContextHelper.getApplication()!!.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

fun Application.restart() = ProcessPhoenix.triggerRebirth(this)