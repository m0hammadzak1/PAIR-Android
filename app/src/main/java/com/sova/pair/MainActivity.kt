package com.sova.pair

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sova.pair.databinding.ActivityMainBinding
import com.sova.pair.service.PorcupineService


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val receiver: ServiceBroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnstartPair.setOnClickListener {
            if (hasRecordPermission()) {
                startService()
            } else {
                requestRecordPermission()
            }
        }
    }

    private fun startService() {
        val serviceIntent = Intent(this, PorcupineService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, PorcupineService::class.java)
        stopService(serviceIntent)
    }

    private fun hasRecordPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            0
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Handle permission denied
        } else {
            startService()
        }
    }

    private fun onPorcupineInitError(errorMessage: String) {
        runOnUiThread {
            binding.errorText.text = errorMessage
            stopService()
        }
    }

    inner class ServiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onPorcupineInitError(intent.getStringExtra("errorMessage") ?: "gg")
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter("PorcupineInitError"))
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

}