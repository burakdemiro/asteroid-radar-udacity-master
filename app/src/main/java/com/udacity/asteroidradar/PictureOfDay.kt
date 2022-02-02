package com.udacity.asteroidradar

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class PictureOfDay(val media_type: String,
                        val title: String,
                        val url: String): Parcelable