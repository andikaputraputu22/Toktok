package com.moonlightsplitter.toktok.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoModel(
    val url: String
) : Parcelable
