package com.android.player.exo

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ExoPlayerManagerTest {

    private var exoPlayerManager = mock(ExoPlayerManager::class.java)

    @Test
    fun testGetCurrentStreamPosition_forFirstCall() {
        val currentStreamPosition = exoPlayerManager.getCurrentStreamPosition()
        assertTrue(currentStreamPosition == 0L)
    }
}