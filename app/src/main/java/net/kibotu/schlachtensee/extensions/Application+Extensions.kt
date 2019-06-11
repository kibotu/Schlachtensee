@file:JvmName("ApplicationExtensions")

package net.kibotu.schlachtensee.extensions

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import com.crashlytics.android.Crashlytics
import com.exozet.android.core.extensions.initStrictMode
import com.exozet.android.core.extensions.inject
import com.exozet.android.core.extensions.isRightToLeft
import com.exozet.android.core.misc.DefaultUserAgent
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.processphoenix.ProcessPhoenix
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import net.kibotu.ContextHelper
import net.kibotu.android.deviceinfo.library.buildinfo.BuildInfo
import net.kibotu.logger.*
import net.kibotu.schlachtensee.BuildConfig
import net.kibotu.schlachtensee.services.storage.AppConfiguration
import net.kibotu.schlachtensee.services.storage.LocalUser
import java.util.*

fun Application.initCrashlytics() {

    val localUser by inject<LocalUser>()

    if (!localUser.isTrackingAllowed && !BuildConfig.DEBUG)
        return

    FirebaseApp.initializeApp(this)
    Fabric.with(this, Crashlytics())

    Crashlytics.setString("version", BuildConfig.CANONICAL_VERSION_NAME)
    Crashlytics.setString("git-files", "${BuildConfig.VSC}${VSC.Gitlab.filesUrl}/${BuildConfig.COMMIT_HASH}")
    Crashlytics.setString("git-commit", "${BuildConfig.VSC}${VSC.Gitlab.commitUrl}/${BuildConfig.COMMIT_HASH}")
}

fun Application.subscribePushNotificationTopics() {
    FirebaseMessaging.getInstance().subscribeToTopic(packageName)
    FirebaseMessaging.getInstance().subscribeToTopic("$packageName.android")
    if (BuildConfig.DEBUG)
        FirebaseMessaging.getInstance().subscribeToTopic("$packageName.development")
}

fun Application.initLogger() {

    val appConfiguration by inject<AppConfiguration>()

    if (appConfiguration.enableLogging) {
        Logger.addLogger(LogcatLogger())
        Logger.addLogger(WebLogger())
    }

    Logger.addLogger(CrashlyticsLogger(), Level.VERBOSE)
}

fun Application.initLeakCanary(): Boolean {
    if (LeakCanary.isInAnalyzerProcess(this)) {
        // This process is dedicated to LeakCanary for heap analysis.
        // You should not init your app in this process.
        return true
    }

    initStrictMode()

    LeakCanary.install(this)

    return false
}

// region build info

enum class VSC(val commitUrl: String, val filesUrl: String) {
    Github("/commits", "/src"),
    Gitlab("/commits", "/tree")
}

fun VSC.createAppBuildInfo(): Map<String, Any> {
    val info = LinkedHashMap<String, Any>()
    @Suppress("DEPRECATION")
    info["DEVICE_ID"] = Build.SERIAL
    info["VERSION_NAME"] = "" + BuildConfig.VERSION_NAME
    info["VERSION_CODE"] = "" + BuildConfig.VERSION_CODE
    info["BUILD_TYPE"] = BuildConfig.BUILD_TYPE
    info["FLAVOR"] = BuildConfig.FLAVOR
    val d = Calendar.getInstance()
    d.timeInMillis = java.lang.Long.parseLong(BuildConfig.BUILD_DATE)
    info["BUILD_DATE"] = "" + d.time
    info["BRANCH"] = BuildConfig.BRANCH
    info["COMMIT_HASH"] = BuildConfig.COMMIT_HASH
    info["COMMIT_URL"] = "${BuildConfig.VSC}$commitUrl/${BuildConfig.COMMIT_HASH}"
    info["TREE_URL"] = "${BuildConfig.VSC}$filesUrl/${BuildConfig.COMMIT_HASH}"
    info["IS_LOCAL"] = BuildConfig.IS_LOCAL
    info["IS_CI"] = BuildConfig.IS_CI
    info["Permissions"] = BuildInfo.getPermissions()
    return info
}

fun createDeviceBuild(): Map<String, Any> {
    val info = LinkedHashMap<String, Any>()
    // http://developer.android.com/reference/android/os/Build.html
    info["Model"] = Build.MODEL
    info["Manufacturer"] = Build.MANUFACTURER
    info["Release"] = Build.VERSION.RELEASE
    info["SDK_INT"] = Build.VERSION.SDK_INT.toString()
    info["Android Id"] = BuildInfo.getAndroidId()
    info["TIME"] = Date(Build.TIME).toString()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        info["SUPPORTED_ABIS"] = Arrays.toString(Build.SUPPORTED_ABIS)
    else {
        @Suppress("DEPRECATION")
        info["CPU_ABI"] = Build.CPU_ABI
        @Suppress("DEPRECATION")
        info["CPU_ABI2"] = Build.CPU_ABI2
    }

    info["Board"] = Build.BOARD
    info["Bootloader"] = Build.BOOTLOADER
    info["Brand"] = Build.BRAND
    info["Device"] = Build.DEVICE
    info["Display"] = Build.DISPLAY
    info["Fingerprint"] = Build.FINGERPRINT
    info["Hardware"] = Build.HARDWARE
    info["Host"] = Build.HOST
    info["Id"] = Build.ID
    info["Product"] = Build.PRODUCT
    @Suppress("DEPRECATION")
    info["Serial"] = Build.SERIAL
    info["Tags"] = Build.TAGS
    info["Type"] = Build.TYPE
    info["User"] = Build.USER

    // http://developer.android.com/reference/android/os/Build.VERSION.html
    info["Codename"] = Build.VERSION.CODENAME
    info["Incremental"] = Build.VERSION.INCREMENTAL
    info["User Agent"] = DefaultUserAgent.getDefaultUserAgent(ContextHelper.getContext()!!)
    info["HTTP Agent"] = System.getProperty("http.agent")!!
    info["RTL"] = isRightToLeft()
    info["isTablet"] = isTablet

    return info
}

// endregion

/**
 * [determine-if-the-device-is-a-smartphone-or-tablet](http://stackoverflow.com/a/18387977)
 */
val isTablet: Boolean =
    ContextHelper.getApplication()!!.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

fun Application.restart() = ProcessPhoenix.triggerRebirth(this)