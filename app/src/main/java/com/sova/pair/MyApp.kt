package com.sova.pair

import android.app.Application
import com.sova.pair.service.Porcupine
import com.sova.pair.service.SpeechRecognizerClass
import com.sova.pair.utils.Common

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {

        Common.initTTS(applicationContext)
        Porcupine.initPorcupine(applicationContext)
        SpeechRecognizerClass.initSpeechRecognizer(applicationContext)
        //UtilityFunctions.readCSV(applicationContext)

    }

}