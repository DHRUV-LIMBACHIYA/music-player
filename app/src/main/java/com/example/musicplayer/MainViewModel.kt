package com.example.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Dhruv Limbachiya on 12-07-2021.
 */
class MainViewModel(private val context: Context) : ViewModel() {

    val playObservable = ObservableBoolean() // Track play and pause button

    private lateinit var trackUtil: TrackUtil

    // Hold the live data of current track.
    private val _currentTrackDetail = MutableLiveData<Track?>()
    val currentTrackDetail: LiveData<Track?> get() =  _currentTrackDetail

    /**
     * Initialize the media player with raw resource.
     */
    fun initMediaPlayer(){
        trackUtil = TrackUtil(context)
        trackUtil.onMediaPlayerCreate()
    }

    /**
     * invoke on play/pause button click
     */
    fun onPlayOrPause(){
        trackUtil.playorPauseTrack()  // call [TrackUtil]'s playorPauseTrack() method
        _currentTrackDetail.value = trackUtil.currentTrackDetails() // fetch the current track details data.
        playObservable.set(_currentTrackDetail.value?.isPlaying ?: false) // set it's playing status
    }

    /**
     * Invoke on next button click
     */
    fun onNext(){
        trackUtil.nextTrack() // call [TrackUtil]'s nextTrack() method
        _currentTrackDetail.value = trackUtil.currentTrackDetails()
        playObservable.set(_currentTrackDetail.value?.isPlaying ?: false)
    }

    /**
     * Invoke on previous button click
     */
    fun onPrevious(){
        trackUtil.previousTrack() // call [TrackUtil]'s previous() method
        _currentTrackDetail.value = trackUtil.currentTrackDetails()
        playObservable.set(_currentTrackDetail.value?.isPlaying ?: false)
    }

    /**
     * Release media player resources by calling TrackUtils's releaseMediaPlayer() method
     */
    fun releaseMediaPlayer(){
        trackUtil.releaseMediaPlayer()
    }

    /**
     * Pause the media player by calling TrackUtils's  onPauseMediaPlayer() method.
     */
    fun pauseMediaPlayer() {
        trackUtil.onPauseMediaPlayer()
    }
}