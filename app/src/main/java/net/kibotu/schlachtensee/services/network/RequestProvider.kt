package net.kibotu.schlachtensee.services.network

import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


@OptIn(KoinApiExtension::class)
class RequestProvider : KoinComponent {

    val schlachtenseeApi by inject<SchlachtenseeApi>()
}