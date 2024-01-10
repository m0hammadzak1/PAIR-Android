package com.sova.pair.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.sova.pair.MainActivity
import com.sova.pair.R
import com.sova.pair.service.SpeechRecognizerClass
import com.sova.pair.ui.FeatureActivity
import com.sova.pair.util.DataParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class Common {
    companion object {
        private val TAG: String = "Common"
        var tts: TextToSpeech? = null
        var inSession = false
        var activity: Activity? = null

        fun initTTS(context: Context) {

            showToast(context, "TTS process ")
            tts = TextToSpeech(context,
                TextToSpeech.OnInitListener {
                    if (it != TextToSpeech.ERROR) {
                        tts?.language = Locale.US
                        showToast(context, "TTS successful ")
                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(p0: String?) {
                                Log.i(TAG, "TTS onStart")
                            }

                            override fun onDone(p0: String?) {
                                Log.i(TAG, "TTS onDone")
                                if (inSession) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        //SpeechRecognizerClass.initSpeechRecognizer(context!!)
                                        SpeechRecognizerClass.startSpeechRecognizer(true)
                                    }

                                }
                            }

                            override fun onError(p0: String?) {
                                Log.i(TAG, "TTS onError")

                            }

                        })

                    } else {
                        //Log.d("ReminderService", "Error: $it")
                        showToast(context, "TTS error ")

                    }
                })
        }

        fun startTts(message: String) {
            Log.i(TAG, "startTts")
            Log.i(TAG, "startTts")
            tts?.let {
                it.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
            }
        }

        fun showToast(context: Context, s: String) {
            Toast.makeText(context, s, Toast.LENGTH_LONG).show()
        }

        fun showHideMicIcon(context: Context, flag: Boolean) {
            if(activity is MainActivity){
                (activity as MainActivity).showHideMic(flag)
            }
            /*if (activity?.findViewById<LottieAnimationView>(R.id.gif) != null) {
                val image = activity?.findViewById<LottieAnimationView>(R.id.gif)
                if (flag && image != null) {
                    image.visibility = View.VISIBLE
                   //Glide.with(context).load(R.drawable.mic_listen).into(image)
                } else {
                    image?.visibility = View.GONE
                }
            }*/
        }

        fun openFeature(context: Context, text: String) {
            val features = DataParser.getFeatures(context.assets)
            var found = false
            features.forEach {
                if (it.matchString.equals(text, true)) {
                    found = true
                    val intent = Intent(context, FeatureActivity::class.java)
                    intent.putExtra("feature", it.feature)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    if (!found) {
                        startTts("Sorry Command not found, please try again")
                    }
                }
                Log.i("FEATURES", "openFeature: ${it.feature}")
            }
        }

    }
}