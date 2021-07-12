package com.example.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.example.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private val mViewModel: MainViewModel by lazy {
       MainViewModel(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        mBinding.viewModel = mViewModel

        mViewModel.initMediaPlayer()

        // Observe the live track details
        mViewModel.currentTrackDetail.observe(this,{ t ->
            t?.let { track ->
                mBinding.track = track
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mViewModel.pauseMediaPlayer() // Pause the media player.
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.releaseMediaPlayer() // release media player resources.
    }
}