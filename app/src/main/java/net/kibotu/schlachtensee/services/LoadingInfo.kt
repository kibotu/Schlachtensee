package net.kibotu.schlachtensee.services

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoadingInfo(
    val name: String = "",
    val isLoading: Boolean = true,
    val time: Long = System.currentTimeMillis()
) : Parcelable