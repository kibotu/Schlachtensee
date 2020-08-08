package net.kibotu.schlachtensee.koin

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.github.florent37.application.provider.ActivityProvider
import net.kibotu.schlachtensee.viewmodels.AppViewModel
import net.kibotu.schlachtensee.viewmodels.SchlachtenseeApiViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory {
        ActivityProvider.currentActivity?.let {
            ViewModelProvider(it as ViewModelStoreOwner).get(
                AppViewModel::class.java
            )
        }
    }
    factory {
        ActivityProvider.currentActivity?.let {
            ViewModelProvider(it as ViewModelStoreOwner).get(
                SchlachtenseeApiViewModel::class.java
            )
        }
    }
}