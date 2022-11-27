package net.kibotu.schlachtensee.extensions

import android.app.Application
import net.kibotu.logger.LogcatLogger
import net.kibotu.logger.Logger
import net.kibotu.logger.WebLogger
import net.kibotu.schlachtensee.BuildConfig


fun Application.initLogger() {

    if (BuildConfig.DEBUG) {
        Logger.addLogger(LogcatLogger())
        Logger.addLogger(WebLogger())
    }
}

