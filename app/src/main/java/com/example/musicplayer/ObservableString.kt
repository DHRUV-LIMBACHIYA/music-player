package com.example.musicplayer

import android.text.TextUtils
import androidx.databinding.ObservableField

/**
 * Created by Dhruv Limbachiya on 13-07-2021.
 */
class ObservableString(string: String = "") : ObservableField<String>(string) {


    override fun get(): String {
        return if (super.get() == null) "" else super.get()!!
    }

    /**
     * Method for get trimmed data
     *
     * @return trimmed data
     */
    fun getTrimmed(): String {
        val stringData = get()
        return if (!TextUtils.isEmpty(stringData) && !TextUtils.isEmpty(stringData.trim { it <= ' ' })) stringData.trim { it <= ' ' } else ""
    }

    /**
     * Get String length
     *
     * @return length
     */
    fun getTrimmedLength(): Int {
        val trimmedData = getTrimmed()
        return if (!TextUtils.isEmpty(trimmedData)) trimmedData.length else 0
    }

    /**
     * Check is Empty String
     *
     * @return is Empty
     */
    fun isEmptyData(): Boolean {
        return TextUtils.isEmpty(getTrimmed())
    }
}
