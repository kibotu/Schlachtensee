package net.kibotu.schlachtensee.services.storage

import com.exozet.android.core.storage.sharedPreference
import java.util.*

class LocalUser {

    var isTrackingAllowed by sharedPreference("isTrackingAllowed", true)

    var analyticsId by sharedPreference("analyticsId", UUID.randomUUID().toString())

    var fcmToken by sharedPreference("fcmToken", "")

    override fun toString(): String = """
    {
        "fcmToken" : "$fcmToken",
        "analyticsId" : "$analyticsId"
    }
    """.trimIndent()

    fun sharedPreferencesToString(): String = """
    {
        "fcmToken" : "$fcmToken",
        "analyticsId" : "$analyticsId"
    }
    """.trimIndent()

}