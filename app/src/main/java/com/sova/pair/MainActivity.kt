package com.sova.pair

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sova.pair.databinding.ActivityMainBinding
import com.sova.pair.service.PorcupineService
import com.sova.pair.ui.FeatureActivity
import com.sova.pair.util.DataParser
import com.sova.pair.utils.Common

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val receiver: ServiceBroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkServiceRunning()

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

        //openFeature()
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
        checkServiceRunning()
    }

    private fun checkServiceRunning() {
        if (isServiceRunning(PorcupineService::class.java)) {
            binding.tvStatus.setBackgroundColor(getColor(R.color.green))
            binding.tvStatus.text = "Status : PAIR is running!"
        } else {
            binding.tvStatus.setBackgroundColor(getColor(R.color.red))
            binding.tvStatus.text = "Status : Disconnected!"
        }
    }

    private fun stopService() {
        val serviceIntent = Intent(this, PorcupineService::class.java)
        stopService(serviceIntent)
        checkServiceRunning()
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
        if (receiver == null) registerReceiver(receiver, IntentFilter("PorcupineInitError"))
        Common.activity = this
        registerReceiver(receiver, IntentFilter("PorcupineInitError"))
    }

    override fun onDestroy() {
        if (receiver != null) unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Int.MAX_VALUE)

        for (service in services) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }

        return false
    }

    private fun openFeature() {
        val features = DataParser.getFeatures(assets)
        features.forEach {
            Log.i("FEATURES", "openFeature: ${it.feature}")
        }
        val intent = Intent(this, FeatureActivity::class.java)
        intent.putExtra("feature", features[3])
        startActivity(intent)
    }

}