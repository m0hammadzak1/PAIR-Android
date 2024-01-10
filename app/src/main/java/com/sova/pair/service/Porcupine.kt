package com.sova.pair.service

import ai.picovoice.porcupine.PorcupineException
import ai.picovoice.porcupine.PorcupineManager
import android.content.Context
import android.util.Log
import java.lang.Exception

class Porcupine {

    companion object {
        private var porcupineManager: PorcupineManager? = null
        const val TAG = "Porcupine"
        var context: Context? = null
        fun initPorcupine(context: Context?) {
            this.context = context
            Log.d("startVoiceAgain  initPorcupine", "called")

            try {
                porcupineManager = PorcupineManager.Builder()
                    .setAccessKey("Oyrw3UxdjNeSE8Mdfpf4iXU4yWtwmC5KD69MdzUIfsYagNrFj4SlQg==")
                    .setKeywordPaths(arrayOf("hey_pair_en.ppn"))
                    .setSensitivity(0.7f)
                    .build(
                        context
                    ) { keywordIndex ->
                        Log.i(TAG, "keyword invoke $keywordIndex")
                        Log.i(TAG, "current thread ${Thread.currentThread().name}")
                        porcupineManager?.stop()

                        porcupineManager = null

                        SpeechRecognizerClass.startSpeechRecognizer(false)

                    }


            } catch (e: PorcupineException) {
                Log.e(TAG, "exception  ${e.message}")
            }
            startWakeWordEngine()


        }


        fun startWakeWordEngine() {
            try {
                //stopSpeechClass()
                if (porcupineManager != null) {
                    Log.d(TAG, "startWakeWordEngine: not null")
                    porcupineManager?.start()
                } else {
                    initPorcupine(context)
                    Log.d(TAG, "startWakeWordEngine: null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun stopWakeWordEngine() {
            try {
                if (porcupineManager != null) {
                    Log.d(TAG, "stopWakeWordEngine: not null")
                    porcupineManager?.stop()
                    porcupineManager = null
                } else {
                    Log.d(TAG, "stopWakeWordEngine: null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


}

