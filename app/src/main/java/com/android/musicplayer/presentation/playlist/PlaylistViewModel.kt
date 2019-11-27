package com.android.musicplayer.presentation.playlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.musicplayer.data.model.Song
import com.android.musicplayer.domain.usecase.DeleteSongUseCase
import com.android.musicplayer.domain.usecase.GetSongsUseCase
import com.android.musicplayer.domain.usecase.SaveSongDataUseCase

class PlaylistViewModel(
    private val saveSongDataUseCase: SaveSongDataUseCase,
    private val getSongsUseCase: GetSongsUseCase,
    private val deleteSongUseCase: DeleteSongUseCase
) : ViewModel() {


    val TAG = PlaylistViewModel::class.java.name
    val playlistData = MutableLiveData<List<Song>>()

    fun saveSongData(song: Song) {
        Log.i(TAG, "Song : $song")
        saveSongDataUseCase.save(song)

    }

    fun getSongsFromDb() {
        playlistData.value = getSongsUseCase.getSongs()
    }

    fun removeItemFromList(song: Song) {
        deleteSongUseCase.deleteItem(song)
        val list = playlistData.value as ArrayList<Song>
        list.remove(song)
        playlistData.value = list
    }
}