package com.android.player.helper

import android.content.Context
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.player.R
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceHelperTest {

    lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun getThemeColorTest() {
        val themeColor = ResourceHelper.getThemeColor(context, R.attr.colorPrimary, Color.DKGRAY)
        assertEquals(-12303292, themeColor)
    }
}