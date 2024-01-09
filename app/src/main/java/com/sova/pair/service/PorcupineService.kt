package com.sova.pair.service

import ai.picovoice.porcupine.PorcupineActivationException
import ai.picovoice.porcupine.PorcupineActivationLimitException
import ai.picovoice.porcupine.PorcupineActivationRefusedException
import ai.picovoice.porcupine.PorcupineActivationThrottledException
import ai.picovoice.porcupine.PorcupineException
import ai.picovoice.porcupine.PorcupineInvalidArgumentException
import ai.picovoice.porcupine.PorcupineManager
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sova.pair.MainActivity


/**
 * Created by Zaki on 09-01-2024.
 */
class PorcupineService : Service() {
    private val SERVICE_ID = 1234
    private val CHANNEL_ID = "PorcupineServiceChannel"
    private val accessKey = "Oyrw3UxdjNeSE8Mdfpf4iXU4yWtwmC5KD69MdzUIfsYagNrFj4SlQg=="
    private var porcupineManager: PorcupineManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        try {
            porcupineManager = PorcupineManager.Builder()
                .setAccessKey(accessKey)
                .setKeywordPaths(arrayOf("hey_pair_en.ppn"))
                .setSensitivity(0.7f)
                .build(
                    applicationContext
                ) { keywordIndex ->
                    // Wake word detected!
                    Log.d("PORCUPINE_SERVICE", "Wake word detected: $keywordIndex")
                }
            porcupineManager?.start()
        } catch (e: PorcupineInvalidArgumentException) {
            e.message?.let { onPorcupineInitError(it) }
        } catch (e: PorcupineActivationException) {
            onPorcupineInitError("AccessKey activation error")
        } catch (e: PorcupineActivationLimitException) {
            onPorcupineInitError("AccessKey reached its device limit")
        } catch (e: PorcupineActivationRefusedException) {
            onPorcupineInitError("AccessKey refused")
        } catch (e: PorcupineActivationThrottledException) {
            onPorcupineInitError("AccessKey has been throttled")
        } catch (e: PorcupineException) {
            onPorcupineInitError("Failed to initialize Porcupine: ${e.message}")
        }
        val notification = if (porcupineManager == null) getNotification(
            "Porcupine init failed",
            "Service will be shut down"
        ) else getNotification("Wake word service", "Say 'hey Pair'!")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(SERVICE_ID, notification)
        } else {
            startForeground(
                SERVICE_ID, notification!!,
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onPorcupineInitError(message: String) {
        val i = Intent("PorcupineInitError")
        i.putExtra("errorMessage", message)
        sendBroadcast(i)
    }

    private fun getNotification(title: String, message: String): Notification? {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_MUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "PAIR",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        try {
            porcupineManager?.stop()
            porcupineManager?.delete()
        } catch (e: PorcupineException) {
            Log.e("PORCUPINE", e.toString())
        }
        super.onDestroy()
    }
}