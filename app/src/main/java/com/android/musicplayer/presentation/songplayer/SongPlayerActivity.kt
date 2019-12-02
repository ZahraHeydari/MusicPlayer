package com.android.musicplayer.presentation.songplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import coil.api.load
import coil.transform.CircleCropTransformation
import com.android.musicplayer.R
import com.android.musicplayer.data.model.Song
import com.android.musicplayer.utils.player.model.ASong
import com.android.musicplayer.utils.player.BaseSongPlayerActivity
import kotlinx.android.synthetic.main.activity_song_player.*
import java.io.File

class SongPlayerActivity : BaseSongPlayerActivity() {


    private val TAG = SongPlayerActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_player)

        if (intent?.extras?.containsKey(SONG_LIST_KEY) == true) {
            val songList = intent?.extras?.get(SONG_LIST_KEY) as ArrayList<ASong>
            Log.i(TAG, "song list: $songList")

            if (intent?.extras?.containsKey(Song::class.java.name) == true) {
                val song = intent?.extras?.get(Song::class.java.name) as Song
                play(songList, song)
                loadInitialData(song.title, song.artist, song.clipArt)
            }
        }

        playerViewModel.songDurationText.observe(this, Observer<String> { t ->
            song_player_total_time_text_view.text = t
        })

        playerViewModel.songDuration.observe(this, Observer {
            song_player_progress_seek_bar.max = it
        })

        playerViewModel.songPositionText.observe(this,
            Observer<String> { t -> song_player_passed_time_text_view.text = t })

        playerViewModel.songPosition.observe(this, Observer {
            song_player_progress_seek_bar.progress = it
        })

        playerViewModel.isPlay.observe(this, Observer {
            song_player_toggle_image_view.setImageResource(if (it) R.drawable.ic_pause_vector else R.drawable.ic_play_vector)
        })

        playerViewModel.playerdata.observe(this, Observer {
            loadInitialData(it?.getName(), it?.getSingerName(), it?.getFeatureAvatar())
        })

        song_player_skip_next_image_view.setOnClickListener {
            playerViewModel.next()
        }

        song_player_skip_back_image_view.setOnClickListener {
            playerViewModel.previous()
        }

        song_player_toggle_image_view.setOnClickListener {
            playerViewModel.play()
        }

        song_player_progress_seek_bar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged: p0: $p0 p1: $p1, p2: $p2")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.i(TAG, "onStartTrackingTouch: p0: $p0")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.i(TAG, "onStopTrackingTouch: p0: $p0")
                playerViewModel.seekTo(song_player_progress_seek_bar.progress.toLong())
            }

        })
    }

    private fun loadInitialData(title: String?, singerName: String?, image: String?) {
        song_player_title_text_view.text = title
        song_player_singer_name_text_view.text = singerName

        if (image.isNullOrEmpty()) {
            song_player_image_view.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SongPlayerActivity,
                    R.drawable.placeholder
                )
            )
        } else {
            song_player_image_view.load(File(image)) {
                crossfade(true)
                placeholder(
                    ContextCompat.getDrawable(
                        this@SongPlayerActivity,
                        R.drawable.placeholder
                    )
                )
                error(ContextCompat.getDrawable(this@SongPlayerActivity, R.drawable.placeholder))
                transformations(CircleCropTransformation())
            }
        }
    }


    companion object {

        const val SONG_LIST_KEY = "SONG_LIST_KEY"

        fun start(context: Context, song: Song, songList: ArrayList<Song>) {
            val intent = Intent(context, SongPlayerActivity::class.java)
            intent.putExtra(Song::class.java.name, song)
            intent.putExtra(SONG_LIST_KEY, songList)
            context.startActivity(intent)
        }
    }
}