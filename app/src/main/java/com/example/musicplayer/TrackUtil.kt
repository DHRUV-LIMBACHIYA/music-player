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
class TrackUtil(val context: Context) {

    private lateinit var mediaPlayer: MediaPlayer
    private var currentTrack: Int = 0
    private var tracks: List<Track>

    init {
        currentTrack = 0
        tracks = getMediaFromRaw(context)
    }

    /**
     * Create a media player using raw resource id.
     */
    fun onMediaPlayerCreate() {
        mediaPlayer = MediaPlayer.create(context, tracks[currentTrack].trackId)
        Log.i(TAG, "onMediaPlayerCreate: Track Id $tracks[currentTrack].trackId")
    }

    /**
     * Logic for play and pause.
     */
    fun playorPauseTrack() {
        if (!mediaPlayer.isPlaying && !tracks[currentTrack].isPlaying) {
            mediaPlayer.start()
            tracks[currentTrack].isPlaying = true // set playing status to true on media player play state.
        } else {
            mediaPlayer.pause()
            tracks[currentTrack].isPlaying = false // set playing status to false on media player pause state.
        }
    }

    /**
     * Logic for playing next track.
     */
    fun nextTrack() {
        tracks[currentTrack].isPlaying = false // Resetting the playing status
        mediaPlayer.reset() // Reset the media player. To play different playback media player needs to initialize again.
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
        mediaPlayer.reset() // Reset the media player. To play different playback media player needs to initialize again.
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
        val currentDuration = mediaPlayer.currentPosition
        tracks[currentTrack].totalDuration = totalDuration
        tracks[currentTrack].currentDuration = currentDuration
        Log.i(
            TAG,
            "currentTrackDetails: Total Duration : $totalDuration and Current Duration : $currentDuration"
        )
        return tracks.get(currentTrack)
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
}

/**
 * Method for getting media resource from "raw" folder.
 * @return tracks - list of tracks.
 */
fun getMediaFromRaw(context: Context): List<Track> {
    val mp3Files = mutableListOf<Track>()

    val fields = R.raw::class.java.declaredFields // Get all the fields present in "raw" folder

    for (index in fields.indices) {
        val resourceId = fields[index].getInt(fields[index]) // resource id
        val resourceName = fields[index].name // resource name
        mp3Files.add(Track(resourceId, resourceName)) // create track using resource id & resource name and add it to "mp3Files" list.
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
)
