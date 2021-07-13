package com.example.musicplayer

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableLong
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Created by Dhruv Limbachiya on 12-07-2021.
 */
class MainViewModel(private val context: Context) : ViewModel() {

    val playObservable = ObservableBoolean() // Track play and pause button
    val currentDurationObservable = ObservableString() // Track current duration of track or timer.

    var trackUtil: TrackUtil? = null // Hold track util instance

    // Hold the live data of current track.
    private val _currentTrackDetail = MutableLiveData<Track?>()
    val currentTrackDetail: LiveData<Track?> get() = _currentTrackDetail

    private val disposable = CompositeDisposable()
    var currentDuration = ObservableInt()

    /**
     * Initialize the media player with raw resource.
     */
    fun initMediaPlayer() {
        trackUtil?.onMediaPlayerCreate()
        _currentTrackDetail.value =
            trackUtil?.currentTrackDetails() // fetch the current track details data.

        disposable
            .add(
                trackUtil?.timerObservable?.subscribe {
                    currentDuration.set((it / 1000).toInt())
                    currentDurationObservable.set(millisToTimer(it))
                }
            )
    }

    /**
     * invoke on play/pause button click
     */
    fun onPlayOrPause() {
        trackUtil?.playorPauseTrack()  // call [TrackUtil]'s playorPauseTrack() method
        _currentTrackDetail.value =
            trackUtil?.currentTrackDetails() // fetch the current track details data.
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

    fun disposeTimerObservable() {
//        if (_currentTrackDetail.value?.totalDuration == this.currentDuration.get().toInt()) {
//            disposable.clear() // clear the disposable. Can add other disposable.
//        }
    }

    /**
     *
     */
    fun seekTo(progress: Int) {
        trackUtil?.mediaPlayerProgress(progress)
    }

    /**
     * Function for converting milliseconds into timer string
     * @return string - elapsed time in string.
     */
    private fun millisToTimer(elapsedTime: Long): String {
        val stringBuffer = StringBuffer()
        val minutes = ((elapsedTime % (1000 * 60 * 60)) / (1000 * 60))
        val seconds = (((elapsedTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000).toInt()
        stringBuffer
            .append(String.format("%02d", minutes))
            .append(":")
            .append(String.format("%02d", seconds))
        return stringBuffer.toString()
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}