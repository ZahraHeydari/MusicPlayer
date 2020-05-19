package com.android.musicplayer.presentation.songplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import coil.api.load
import com.android.musicplayer.R
import com.android.musicplayer.data.model.Song
import com.android.player.BaseSongPlayerActivity
import com.android.player.model.ASong
import com.android.player.utils.OnSwipeTouchListener
import kotlinx.android.synthetic.main.activity_song_player.*
import java.io.File

class SongPlayerActivity : BaseSongPlayerActivity() {


    private var mSong: Song? = null
    private var mSongList: MutableList<ASong>? = null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.apply {
            if (containsKey(SONG_LIST_KEY)) {
                mSongList = getParcelableArrayList(SONG_LIST_KEY)
            }

            if (containsKey(ASong::class.java.name)) {
                mSong = getParcelable<ASong>(ASong::class.java.name) as Song
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_player)

        onNewIntent(intent)

        mSong?.let {
            play(mSongList, it)
            loadInitialData(it.title, it.artist, it.clipArt)
        }

        playerViewModel.songDurationTextData.observe(this, Observer { t ->
            song_player_total_time_text_view.text = t
        })

        playerViewModel.songDurationData.observe(this, Observer {
            song_player_progress_seek_bar.max = it
        })

        playerViewModel.songPositionTextData.observe(this,
            Observer { t -> song_player_passed_time_text_view.text = t })

        playerViewModel.songPositionData.observe(this, Observer {
            song_player_progress_seek_bar.progress = it
        })

        playerViewModel.isRepeatData.observe(this, Observer {
            song_player_repeat_image_view.setImageResource(
                if (it) R.drawable.ic_repeat_one_color_primary_vector
                else R.drawable.ic_repeat_one_black_vector
            )
        })

        playerViewModel.isShuffleData.observe(this, Observer {
            song_player_shuffle_image_view.setImageResource(
                if (it) R.drawable.ic_shuffle_color_primary_vector
                else R.drawable.ic_shuffle_black_vector
            )
        })

        playerViewModel.isPlayData.observe(this, Observer {
            song_player_toggle_image_view.setImageResource(if (it) R.drawable.ic_pause_vector else R.drawable.ic_play_vector)
        })

        playerViewModel.playerData.observe(this, Observer {
            loadInitialData(it?.title, it?.artist, it?.clipArt)
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

        song_player_shuffle_image_view.setOnClickListener {
            playerViewModel.shuffle()
        }

        song_player_repeat_image_view.setOnClickListener {
            playerViewModel.repeat()
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

        song_player_container.setOnTouchListener(object :
            OnSwipeTouchListener(this@SongPlayerActivity) {
            override fun onSwipeRight() {
                if (mSongList?.size ?: 0 > 1) skipToPrevious()

            }

            override fun onSwipeLeft() {
                if (mSongList?.size ?: 0 > 1) skipToNext()
            }
        })
    }

    private fun loadInitialData(title: String?, singerName: String?, image: String?) {
        song_player_title_text_view.text = title
        song_player_singer_name_text_view.text = singerName

        image?.let {
            song_player_image_view.load(File(it)) {
                crossfade(true)
                placeholder(
                    ContextCompat.getDrawable(
                        this@SongPlayerActivity,
                        R.drawable.placeholder
                    )
                )
                error(ContextCompat.getDrawable(this@SongPlayerActivity, R.drawable.placeholder))
                //transformations(CircleCropTransformation())
            }
        }
    }

    companion object {

        private val TAG = SongPlayerActivity::class.java.name


        fun start(context: Context, song: Song, songList: ArrayList<Song>) {
            val intent = Intent(context, SongPlayerActivity::class.java).apply {
                putExtra(ASong::class.java.name, song)
                putExtra(SONG_LIST_KEY, songList)
            }
            context.startActivity(intent)
        }
    }
}