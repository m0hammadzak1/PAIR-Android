package com.sova.pair

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sova.pair.databinding.ActivityMainBinding
import com.sova.pair.service.PorcupineService
import com.sova.pair.utils.Common

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val receiver: ServiceBroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swEnablePair.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (hasRecordPermission()) {
                    startService()
                } else {
                    requestRecordPermission()
                }
            } else {
                stopService()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.hasExtra("key") == true){
            binding.tvText.text = intent.extras?.getString("key")
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
            binding.tvStatus.visibility = View.VISIBLE
            binding.tvStatus.setBackgroundColor(getColor(R.color.red))
            binding.tvStatus.text = "Status : $errorMessage"
            binding.swEnablePair.isChecked = false
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
        Common.activity = this
        registerReceiver(receiver, IntentFilter("PorcupineInitError"))
    }

    override fun onDestroy() {
        //unregisterReceiver(receiver)
        super.onDestroy()
    }

}