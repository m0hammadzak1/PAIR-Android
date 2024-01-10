package com.sova.pair.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import com.sova.pair.MainActivity
import com.sova.pair.utils.Common
import java.util.*

class SpeechRecognizerClass {
    companion object : RecognitionListener {

        private const val TAG = "SpeechRecognizerClass"
        private lateinit var speechRecognizer: SpeechRecognizer
        private lateinit var recognizerIntent: Intent
        private lateinit var context: Context
        private var dialogFlowInSession = false

        fun initSpeechRecognizer(context: Context) {
            this.context = context
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

            Log.i(
                TAG,
                "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(context)
            )
            speechRecognizer.setRecognitionListener(this)
            recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )


        }

        fun startSpeechRecognizer(isDialogFlowSession: Boolean) {

            dialogFlowInSession = isDialogFlowSession
            Log.i(TAG, "startSpeechRecognizer called - dialogflowSession is $dialogFlowInSession")
            Porcupine.stopWakeWordEngine()

            speechRecognizer.startListening(recognizerIntent)
        }

        override fun onReadyForSpeech(p0: Bundle?) {
            Log.i(TAG, "onReadyForSpeech called")
            Toast.makeText(context, "start speaking", Toast.LENGTH_LONG).show()
        }

        override fun onBeginningOfSpeech() {
            Log.i(TAG, "onBeginningOfSpeech called")
            Common.showHideMicIcon(context, true)
        }

        override fun onRmsChanged(p0: Float) {
            Log.i(TAG, "onRmsChanged called")
        }

        override fun onBufferReceived(p0: ByteArray?) {
            Log.i(TAG, "onBufferReceived called")
        }

        override fun onEndOfSpeech() {
            Log.i(TAG, "onEndOfSpeech called")
            //stop progress baar here
            Common.showHideMicIcon(context, false)
            Toast.makeText(context, "stop speaking", Toast.LENGTH_LONG).show()
        }

        override fun onError(error: Int) {
            val errorMessage: String = getErrorText(error)
            Log.d(TAG, "FAILED $errorMessage")
            Porcupine.startWakeWordEngine()
            speechRecognizer.cancel()

        }

        override fun onResults(results: Bundle?) {
            speechRecognizer.cancel()
            Log.i(TAG, "onResults called $dialogFlowInSession")
            val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            var text = ""
            if (matches != null) {
                for (result in matches) text = result.trimIndent()
            }
            Log.i(TAG, "onResults $text")

            if (text.equals("configure router", false)) {
                context.startActivity(Intent(context, MainActivity::class.java)
                    .putExtra("key", text)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                Common.startTts("Configuring Router")
            } else{
                Common.openFeature(context, text)
            }
            Porcupine.startWakeWordEngine()
        }


        override fun onPartialResults(p0: Bundle?) {
            Log.i(TAG, "onPartialResults called")
        }

        override fun onEvent(p0: Int, p1: Bundle?) {
            Log.i(TAG, "onEvent called")
        }

        private fun getErrorText(error: Int): String {
            var message = ""
            message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> "error from server"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Didn't understand, please try again."
            }
            return message
        }

    }
}
