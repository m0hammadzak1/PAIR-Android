package com.sova.pair.service

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.google.protobuf.Value
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

class DialogIntentHandler {
    companion object {

        val map: HashMap<String, String> = object : HashMap<String, String>() {
            init {
                /*put("youtube", ConstantValues.YOUTUBE_PACKAGE)
                put("screen settings", ConstantValues.SETTING_ACTIVITY_PACKAGE)
                put("settings", ConstantValues.SETTING_ACTIVITY_PACKAGE)
                // put("tutor me", "com.ahdynamics.aido.Activities.GamesWebView")
                put("general settings", ConstantValues.SETTING_ACTIVITY_PACKAGE)
                put("health", ConstantValues.HEALTH_PACKAGE)
                put("spotify", ConstantValues.SPOTIFY_PACKAGE)
                put("call history", ConstantValues.CONTACT_PACKAGE)
                // put("zoom", "com.ahdynamics.aido.zoomSDK.ZoomMeeting")
                put("alert editor", ConstantValues.ALERT_ENGINE_PACKAGE)
                put("home appliances", ConstantValues.HOME_PACKAGE)
                put("system variables", ConstantValues.SYSTEM_VAR_PACKAGE)
                put("face training", ConstantValues.FACE_TRINING_PACKAGE)
                put("alert list", ConstantValues.ALERT_ENGINE_PACKAGE)
                put("robot list", ConstantValues.CONTACT_PACKAGE)
                // put("movies", "com.ahdynamics.aido.Activities.GamesWebView")
                put("kitchen", ConstantValues.KITCHEN_PACKAGE)
                put("reminder", ConstantValues.REMINDER_PACKAGE)
                put(
                    "personality editor",
                    ConstantValues.PERSONALITY_EDITOR_PACKAGE
                )
                put("contacts", ConstantValues.CONTACT_PACKAGE)
                put("notifications", ConstantValues.NOTIFICATION_PACKAGE)
                put(
                    "behavior list",
                    ConstantValues.BEHAVIOR_LIST_PACKAGE
                )
                put("media", ConstantValues.ACTIVITY_FEED_PACKAGE)*/
            }
        }


        var context: Context? = null
        var TAG = "DialogFlowIntentHandler"
        var audioManager: AudioManager? = null


        var INSTANCE: DialogIntentHandler? = null


        fun getInstnace(): DialogIntentHandler? {
            if (INSTANCE != null) {
                return INSTANCE!!
            } else {
                INSTANCE = DialogIntentHandler()
                return INSTANCE!!
            }
        }

        fun triggerAction(funName: String, slot: MutableMap<String, Value>, context: Context) {
            Log.i(TAG, "triggered Action $funName and slots are $slot ")

            this.context = context
            var method: Method? = null
            try {

                method = DialogIntentHandler::class.java.getDeclaredMethod(
                    funName,
                    MutableMap::class.java,
                    Context::class.java
                )
                method.invoke(getInstnace(), slot, context)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
        }

    }
}