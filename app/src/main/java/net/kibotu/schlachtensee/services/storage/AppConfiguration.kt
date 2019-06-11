package net.kibotu.schlachtensee.services.storage

import com.exozet.android.core.extensions.resBoolean
import com.exozet.android.core.storage.sharedPreference
import net.kibotu.schlachtensee.R

class AppConfiguration {

    var enableLogging by sharedPreference("enableLogging", R.bool.enable_logging.resBoolean)
}