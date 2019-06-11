package net.kibotu.schlachtensee.koin

import net.kibotu.schlachtensee.services.storage.AppConfiguration
import net.kibotu.schlachtensee.services.storage.LocalUser
import org.koin.dsl.module

val configuration = module {
    single { AppConfiguration() }
    single { LocalUser() }
}