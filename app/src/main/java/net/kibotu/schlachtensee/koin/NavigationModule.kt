package net.kibotu.schlachtensee.koin

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import net.kibotu.schlachtensee.R
import org.koin.dsl.module


val navigationModule = module {
    factory { get<NavHostFragment>().navController }
    factory { get<AppCompatActivity>().supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment }
}