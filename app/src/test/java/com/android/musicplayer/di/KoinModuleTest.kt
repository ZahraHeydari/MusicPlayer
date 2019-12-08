package com.android.musicplayer.di

import com.android.musicplayer.di.module.AppModule
import com.android.musicplayer.di.module.DatabaseModule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.test.AutoCloseKoinTest
import org.koin.test.check.checkModules

class KoinModuleTest : AutoCloseKoinTest() {

    @Test
    fun testCoreModule() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(listOf(DatabaseModule,AppModule))
        }.checkModules()
    }
}