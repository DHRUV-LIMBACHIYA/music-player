package com.example.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
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
    }

    /**
     * Logic for playing next track.
     */
    fun nextTrack() {
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
        val totalDuration = mediaPlayer.duration
        tracks[currentTrack].totalDuration = totalDuration
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
    fun getCurrentDuration() : Observable<Long>{
       return Observable
            .interval(0, 1, TimeUnit.MILLISECONDS) // 15 milliseconds => ~ 1000/60fps
            .map {
                Log.i(TAG, "getCurrentDuration: Interval")
                mediaPlayer.currentPosition.toLong()
            }
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
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

    /**
     * Change media player current playing position with seek position.
     */
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
    var isPlaying: Boolean = false,
)

/**
 * interface for media player callback
 */
interface TrackPlaybackListener {
    fun onComplete(isComplete: Boolean)
}

