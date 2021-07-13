package com.example.musicplayer

import androidx.appcompat.widget.AppCompatSeekBar
import androidx.databinding.BindingAdapter

/**
 * Created by Dhruv Limbachiya on 12-07-2021.
 */

@BindingAdapter(value = ["setMaxProgress","setProgress"],requireAll = true)
fun AppCompatSeekBar.setProgress(maxProgress:Long,currentProgress:Long){
    this.max = (maxProgress / 1000).toInt()
    this.progress = (currentProgress / 1000).toInt()
}