package com.sova.pair

import android.app.Application
import io.paperdb.Paper

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
    }
}