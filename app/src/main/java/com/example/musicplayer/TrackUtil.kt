package com.example.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

/**
 * Created by Dhruv Limbachiya on 12-07-2021.
 */

private const val TAG = "MusicUtil"

/*
 Utility class for managing media player and its states.
 */
class TrackUtil(private val context: Context) : MediaPlayer.OnCompletionListener {

    private lateinit var mediaPlayer: MediaPlayer
    private var currentTrack: Int = 0
    private var tracks: List<Track>
    private lateinit var mPlaybackListener: TrackPlaybackListener
    lateinit var timerObservable: Observable<Long>

    init {
        currentTrack = 0
        tracks = getMediaFromRaw()
    }

    /**
     * function for setting playbackListener.
     */
    fun setCallback(playbackListener: TrackPlaybackListener) {
        mPlaybackListener = playbackListener
    }

    /**
     * Create a media player using raw resource id.
     */
    fun onMediaPlayerCreate() {
        mediaPlayer = MediaPlayer.create(context, tracks[currentTrack].trackId)
        mediaPlayer.setOnCompletionListener(this)
        getCurrentDuration()
        Log.i(TAG, "onMediaPlayerCreate: Track Id $tracks[currentTrack].trackId")
    }

    /**
     * Logic for play and pause.
     */
    fun playorPauseTrack() {
        if (!mediaPlayer.isPlaying && !tracks[currentTrack].isPlaying) {
            mediaPlayer.start()
            tracks[currentTrack].isPlaying =
                true // set playing status to true on media player play state.
        } else {
            mediaPlayer.pause()
            tracks[currentTrack].isPlaying =
                false // set playing status to false on media player pause state.
        }
        tracks[currentTrack].trackSelect = true
    }

    /**
     * Logic for playing next track.
     */
    fun nextTrack() {
        tracks[currentTrack].trackSelect = false
        tracks[currentTrack].isPlaying = false // Resetting the playing status
        mediaPlayer.stop() // stop the track because reset() will call onCompleteListener() which is not need in our case.
        if (currentTrack < tracks.size && currentTrack != tracks.size - 1) {
            currentTrack++ // Next track
        }
        onMediaPlayerCreate()
        playorPauseTrack()
    }

    /**
     * Logic for previous track.
     */
    fun previousTrack() {
        tracks[currentTrack].isPlaying = false // Resetting the playing status
        mediaPlayer.stop() // stop the track because reset() will call onCompleteListener() which is not need in our case.
        if (currentTrack < tracks.size && currentTrack != 0) {
            currentTrack-- // Previous track
        }
        onMediaPlayerCreate()
        playorPauseTrack()
    }

    /**
     * Set up the track details.
     * @return track - Track object.
     */
    fun currentTrackDetails(): Track {
        if (mediaPlayer.isPlaying) {
            val totalDuration = mediaPlayer.duration
            val currentDuration = mediaPlayer.currentPosition
            tracks[currentTrack].totalDuration = totalDuration
            tracks[currentTrack].currentDuration = currentDuration
            Log.i(
                TAG,
                "currentTrackDetails: Total Duration : $totalDuration and Current Duration : $currentDuration"
            )
        }

        return tracks[currentTrack]
    }

    /**
     * Release media player resources.
     */
    fun releaseMediaPlayer() {
        mediaPlayer.release()
    }

    /**
     * Pause the media player.
     */
    fun onPauseMediaPlayer() {
        mediaPlayer.pause()
    }

    /**
     * Get the elapsed time.
     */
    private fun getCurrentDuration() {
        timerObservable = Observable
            .interval(0, 15, TimeUnit.MILLISECONDS) // 15 milliseconds => ~ 1000/60fps
            .map {
                Log.i(TAG, "getCurrentDuration: Interval")
                mediaPlayer.currentPosition.toLong()
            }
    }


    /**
     * Find out the explicit complete or implicit complete
     */
    override fun onCompletion(mp: MediaPlayer?) {
        if (currentTrack < tracks.size && currentTrack != tracks.size - 1) {
            mPlaybackListener.onComplete(true)
        } else {
            mPlaybackListener.onComplete(false)
        }

    }

    fun mediaPlayerProgress(progress: Int) {
        mediaPlayer.seekTo(progress)
    }

}

/**
 * Method for getting media resource from "raw" folder.
 * @return tracks - list of tracks.
 */
fun getMediaFromRaw(): List<Track> {
    val mp3Files = mutableListOf<Track>()

    val fields = R.raw::class.java.declaredFields // Get all the fields present in "raw" folder

    for (index in fields.indices) {
        val resourceId = fields[index].getInt(fields[index]) // resource id
        val resourceName = fields[index].name // resource name

        mp3Files.add(
            Track(
                resourceId,
                resourceName
            )
        ) // create track using resource id & resource name and add it to "mp3Files" list.
        Log.i(TAG, "getMediaFromRaw: Media Name: $resourceName and Media Id : $resourceId")
    }

    return mp3Files
}


/**
 * Data class for holding Track data.
 */
data class Track(
    val trackId: Int,
    val trackName: String,
    var totalDuration: Int = 0,
    var currentDuration: Int = 0,
    var isPlaying: Boolean = false,
    var trackSelect: Boolean = false
)

/**
 * interface for media player callback
 */
interface TrackPlaybackListener {
    fun onComplete(isComplete: Boolean)
}

