package com.udacity.asteroidradar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PictureOfDay(val media_type: String,
                        val title: String,
                        val url: String): Parcelable