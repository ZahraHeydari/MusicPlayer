package com.android.musicplayer.data.model


import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.musicplayer.data.source.local.AppDatabase
import com.android.musicplayer.data.source.local.dao.SongDao
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class SongReadWriteTest {


    private lateinit var songDao: SongDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        songDao = db.songDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeSongAndReadInList() {
        val song = Song(3).apply {
            title = "Boom"
        }
        songDao.insert(song)
        val byName = songDao.loadOneBySongTitle("Boom")
        assertThat(byName, equalTo(song))
    }
}