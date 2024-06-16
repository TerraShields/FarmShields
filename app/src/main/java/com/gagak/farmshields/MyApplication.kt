package com.gagak.farmshields

import android.app.Application
import com.gagak.farmshields.core.modules.appModule
import com.gagak.farmshields.core.modules.networkModule
import com.gagak.farmshields.core.modules.retrofitModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    appModule,
                    networkModule,
                    retrofitModule
                )
            )
        }
    }
}