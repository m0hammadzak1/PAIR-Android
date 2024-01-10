package com.sova.pair.util

import android.content.res.AssetManager
import com.google.gson.Gson
import com.sova.pair.model.Feature
import com.sova.pair.model.Features
import java.io.IOException

/**
 * Created by Zaki on 09-01-2024.
 */
object DataParser {

    private fun getJsonDataFromAsset(assets: AssetManager): String? {
        val jsonString: String
        try {
            jsonString =
                assets.open("features/features.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }


    fun getFeatures(assets: AssetManager): List<Feature> {
        val featureString = getJsonDataFromAsset(assets)
        val converter = Gson()
        val data = converter.fromJson(featureString, Features::class.java)
        return data.features
    }
}