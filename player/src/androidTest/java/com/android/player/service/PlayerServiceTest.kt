package com.android.player.service

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ServiceTestRule
import com.android.player.service.PlayerService.LocalBinder
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

@MediumTest
@RunWith(AndroidJUnit4::class)
class PlayerServiceTest {

    @get:Rule
    val mServiceRule: ServiceTestRule = ServiceTestRule()

    private lateinit var context: Context


    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    @Throws(TimeoutException::class)
    fun testIsServiceBound() {

        // Create the service Intent.
        val intent = Intent(
            getApplicationContext<Context>(),
            PlayerService::class.java
        ).apply {
            // Data can be passed to the service via the Intent.
            putExtra(PlayerService.CMD_NAME, PlayerService.CMD_PAUSE)
        }

        // Bind the service and grab a reference to the binder.
        val binder = mServiceRule.bindService(intent)

        // Get the reference to the service, or you can call public methods on the binder directly.
        val service: PlayerService = (binder as LocalBinder).service

        // Verify that the service is working correctly.
        assertThat(service.command, `is`(PlayerService.CMD_PAUSE))
    }

    @Test
    @Throws(Exception::class)
    fun testOnCreate() {
        val intent = Intent(getApplicationContext<Context>(), PlayerService::class.java)
        val binder = mServiceRule.bindService(intent)
        val service: PlayerService = (binder as LocalBinder).service
        service.onCreate()
        assert(service.mCallback != null) // Verify that the service is created.
    }


    @Test
    @Throws(Exception::class)
    fun testOnDestroy() {
        val intent = Intent(getApplicationContext<Context>(), PlayerService::class.java)
        val binder = mServiceRule.bindService(intent)
        val service: PlayerService = (binder as LocalBinder).service
        service.onDestroy()
        assert(service.mCallback == null) // Verify that the service is destroyed.
    }
}