package net.kibotu.schlachtensee.koin

import com.exozet.android.core.extensions.viewModel
import net.kibotu.schlachtensee.viewmodels.AppViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { AppViewModel::class.java.viewModel() }
}