package com.android.musicplayer

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.android.musicplayer.di.module.AppModule
import com.android.musicplayer.di.module.DatabaseModule
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        Stetho.initializeWithDefaults(this)

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(DatabaseModule, AppModule))
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}