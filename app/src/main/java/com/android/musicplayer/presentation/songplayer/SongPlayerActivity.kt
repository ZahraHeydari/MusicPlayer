package com.android.musicplayer.presentation.songplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.Observer
import coil.load
import coil.request.CachePolicy
import com.android.musicplayer.R
import com.android.musicplayer.data.model.Song
import com.android.musicplayer.data.model.Song.Companion.createMediaItem
import com.android.player.BaseSongPlayerActivity
import com.android.player.util.OnSwipeTouchListener
import com.google.android.exoplayer2.MediaItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_song_player.*
import java.io.File

class SongPlayerActivity : BaseSongPlayerActivity() {

    private var mSong: Song? = null
    private var mSongList: MutableList<Song>? = null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.apply {
            if (containsKey(PLAY_LIST_KEY)) {
                mSongList = getParcelableArrayList(PLAY_LIST_KEY)
            }

            if (containsKey(Song::class.java.name)) {
                mSong = getParcelable<Song>(Song::class.java.name) as Song
                setData(mSong)
            }

            if (containsKey(MEDIA_ITEM_KEY)) {
                mSong = Gson().fromJson (getString(MEDIA_ITEM_KEY), Song::class.java)
               setData(mSong)
            }
        }
    }

    fun setData(song : Song?){
        song?.let {song->
            val mediaItems = ArrayList<MediaItem>()
            mSongList?.forEach {
                mediaItems.add(it.createMediaItem())
            }
            play(mediaItems, song.createMediaItem())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_player)

        onNewIntent(intent)

        with(songPlayerViewModel) {
            songDurationData.observe(this@SongPlayerActivity, Observer {
                song_player_progress_seek_bar.max = it
            })

            songDurationTextData.observe(this@SongPlayerActivity,{
                song_player_total_time_text_view.text = it
            })

            songPositionTextData.observe(this@SongPlayerActivity,
                { t -> song_player_passed_time_text_view.text = t })

            songPositionData.observe(this@SongPlayerActivity, {
                song_player_progress_seek_bar.progress = it.toInt()
            })

            isRepeatData.observe(this@SongPlayerActivity, {
                song_player_repeat_image_view.setImageResource(
                    if (it) R.drawable.ic_repeat_one_color_primary_vector
                    else R.drawable.ic_repeat_one_black_vector
                )
            })

            isShuffleData.observe(this@SongPlayerActivity, {
                song_player_shuffle_image_view.setImageResource(
                    if (it) R.drawable.ic_shuffle_color_primary_vector
                    else R.drawable.ic_shuffle_black_vector
                )
            })

            isPlayingData.observe(this@SongPlayerActivity, {
                song_player_toggle_image_view.setImageResource(if (it) R.drawable.ic_pause_vector else R.drawable.ic_play_vector)
            })

            mediaItemData.observe(this@SongPlayerActivity, {
                loadInitialData(Song(it))
            })
        }

        song_player_container.setOnTouchListener(object :
            OnSwipeTouchListener(this@SongPlayerActivity) {
            override fun onSwipeRight() {
                if (mSongList?.size ?: 0 > 1) previous()
            }

            override fun onSwipeLeft() {
                if (mSongList?.size ?: 0 > 1) next()
            }
        })

        song_player_progress_seek_bar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) seekTo(p1.toLong())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //Nothing to do here
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //Nothing to do here
            }

        })

        song_player_skip_next_image_view.setOnClickListener {
            next()
        }

        song_player_skip_back_image_view.setOnClickListener {
            previous()
        }

        song_player_toggle_image_view.setOnClickListener {
            toggle()
        }

        song_player_shuffle_image_view.setOnClickListener {
            shuffle()
        }

        song_player_repeat_image_view.setOnClickListener {
            repeat()
        }
    }

    private fun loadInitialData(aSong: Song) {
        song_player_title_text_view.text = aSong.title
        song_player_singer_name_text_view.text = aSong.artist

        if (aSong.albumArt.toString().isEmpty())
            song_player_image_view.setImageResource(R.drawable.placeholder)
        aSong.albumArt?.let {
            song_player_image_view.load(File(it)) {
                placeholder(R.drawable.placeholder)
                CachePolicy.ENABLED
                error(R.drawable.placeholder)
            }
        }
    }

    companion object {
        fun start(context: Context, song: Song, songList: ArrayList<Song>) {
            val intent = Intent(context, SongPlayerActivity::class.java).apply {
                putExtra(Song::class.java.name, song)
                putExtra(PLAY_LIST_KEY, songList)
            }
            context.startActivity(intent)
        }
    }
}