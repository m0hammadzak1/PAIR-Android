package com.sova.pair.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Zaki on 09-01-2024.
 */
@Parcelize
data class Feature(
    val matchString: String,
    val feature: String,
    val des: String,
    val response: String,
    var initialOnlineValue: Boolean = true,
    var initialInternetValue: Boolean = true,
    var isOnline: Boolean = true,
    var internet: Boolean = true,
    var steps: List<String> = emptyList()
) : Parcelable {
    fun getStepsList(): List<Step> {
        return steps.map {
            Step(it, false)
        }
    }
}

data class Features(
    val features: List<Feature>
)

data class Step(
    var step: String,
    var isActive: Boolean
)
