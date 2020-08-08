package net.kibotu.schlachtensee.services.storage

import com.exozet.android.core.storage.sharedPreference
import net.kibotu.resourceextension.resBoolean
import net.kibotu.schlachtensee.R

class AppConfiguration {

    val schlachtensee_base_url = "http://jmnberlin.de/"

    var enableLogging by sharedPreference("enableLogging", R.bool.enable_logging.resBoolean)
}