package net.kibotu.schlachtensee.koin

import androidx.recyclerview.widget.RecyclerView
import net.kibotu.ContextHelper
import org.koin.dsl.module

val uiModule = module {
    factory { ContextHelper.getAppCompatActivity()!! }
    single { RecyclerView.RecycledViewPool() }
}