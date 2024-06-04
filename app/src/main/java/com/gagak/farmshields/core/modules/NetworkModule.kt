package com.gagak.farmshields.core.modules

import com.gagak.farmshields.core.data.local.preferences.AuthPreferences
import org.koin.dsl.module

val networkModule = module {
    single { AuthPreferences(get()) }
}