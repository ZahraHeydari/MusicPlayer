package com.android.player.notification

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ServiceTestRule
import com.android.player.service.SongPlayerService
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaNotificationManagerTest {

    @get:Rule
    val mServiceRule: ServiceTestRule = ServiceTestRule()
    lateinit var notificationManager: MediaNotificationManager

    @Before
    fun setUp() {
        val intent = Intent(ApplicationProvider.getApplicationContext<Context>(), SongPlayerService::class.java)
        val binder = mServiceRule.bindService(intent)
        val serviceSong: SongPlayerService = (binder as SongPlayerService.LocalBinder).serviceSong
        notificationManager = MediaNotificationManager(serviceSong)
    }

    @Test
    fun testStartNotification() {
        notificationManager.createMediaNotification()
        assertTrue(notificationManager.mStarted)
    }
}