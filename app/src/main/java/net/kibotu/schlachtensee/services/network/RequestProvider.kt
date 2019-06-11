package net.kibotu.schlachtensee.services.network

import com.exozet.android.core.extensions.inject

class RequestProvider {

    val schlachtenseeApi by inject<SchlachtenseeApi>()
}