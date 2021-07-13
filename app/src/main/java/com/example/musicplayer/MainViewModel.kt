package com.example.musicplayer

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Dhruv Limbachiya on 12-07-2021.
 */
class MainViewModel(private val context: Context) : ViewModel() {

    val playObservable = ObservableBoolean() // Track play and pause button

    var trackUtil: TrackUtil? = null // Hold track util instance

    // Hold the live data of current track.
    private val _currentTrackDetail = MutableLiveData<Track?>()
    val currentTrackDetail: LiveData<Track?> get() = _currentTrackDetail

    /**
     * Initialize the media player with raw resource.
     */
    fun initMediaPlayer() {
        trackUtil?.onMediaPlayerCreate()
        _currentTrackDetail.value =
            trackUtil?.currentTrackDetails() // fetch the current track details data.
    }

    /**
     * invoke on play/pause button click
     */
    fun onPlayOrPause() {
        trackUtil?.playorPauseTrack()  // call [TrackUtil]'s playorPauseTrack() method
        playObservable.set(_currentTrackDetail.value?.isPlaying ?: false) // set it's playing status
    }

    /**
     * Invoke on next button click
     */
    fun onNext() {
        viewModelScope.launch {
            playObservable.set(false) // show play icon.
            delay(500) // delay play to pause icon transition to create a simulation.
            trackUtil?.nextTrack() // call [TrackUtil]'s nextTrack() method
            _currentTrackDetail.value = trackUtil?.currentTrackDetails()
            playObservable.set(_currentTrackDetail.value?.isPlaying ?: false)
        }
    }

    /**
     * Invoke on previous button click
     */
    fun onPrevious() {
        viewModelScope.launch {
            playObservable.set(false) // show play icon.
            delay(500) // delay play to pause icon transistion to create a simulation.
            trackUtil?.previousTrack() // call [TrackUtil]'s previous() method
            _currentTrackDetail.value = trackUtil?.currentTrackDetails()
            playObservable.set(_currentTrackDetail.value?.isPlaying ?: false)
        }
    }

    /**
     * Release media player resources by calling TrackUtils's releaseMediaPlayer() method
     */
    fun releaseMediaPlayer() {
        trackUtil?.releaseMediaPlayer()
    }

    /**
     * Pause the media player by calling TrackUtils's  onPauseMediaPlayer() method.
     */
    fun pauseMediaPlayer() {
        trackUtil?.onPauseMediaPlayer()
    }

    /**
     * Method will show play icon when user come back to activty in onResume lifecycle
     */
    fun showPlayIcon() {
        playObservable.set(false) // display play icon.
    }
}