package com.sova.pair.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sova.pair.R
import com.sova.pair.databinding.ActivityFeatureBinding
import com.sova.pair.model.Feature
import com.sova.pair.model.SavedRouter

class FeatureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeatureBinding
    private var savedRouter = SavedRouter()
    private var feature: Feature? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        feature = intent.getParcelableExtra("feature")

        updateInitialRouterData()
        setRouterStatus()

        binding.pbLoad.visibility = View.VISIBLE
        binding.ivDone.visibility = View.GONE
        binding.tvFeatureDes.text = feature?.des ?: ""
        handler.postDelayed({
            binding.tvFeatureDes.text = feature?.response ?: ""
            binding.pbLoad.visibility = View.GONE
            binding.ivDone.visibility = View.VISIBLE
            updateRouterData()
            setRouterStatus()
        }, 10000)

        //setStepAdapter()
    }

    private fun updateInitialRouterData() {
        feature?.let { fea ->
            savedRouter.apply {
                isOnline = fea.initialOnlineValue
                internet = fea.initialInternetValue
            }
        }
    }

    private fun updateRouterData() {
        feature?.let { fea ->
            savedRouter.apply {
                isOnline = fea.isOnline
                internet = fea.internet
            }
        }
    }

    private fun setRouterStatus() {
        if (savedRouter.isOnline) {
            binding.tvOnline.text = "Turned On"
            binding.tvOnline.setBackgroundColor(getColor(R.color.green))
        } else {
            binding.tvOnline.text = "Turned Off"
            binding.tvOnline.setBackgroundColor(getColor(R.color.red))
        }

        if (savedRouter.internet) {
            binding.tvInternet.text = "Internet On"
            binding.tvInternet.setBackgroundColor(getColor(R.color.green))
        } else {
            binding.tvInternet.text = "Internet Off"
            binding.tvInternet.setBackgroundColor(getColor(R.color.red))
        }
    }

    private fun setStepAdapter() {
        val steps = feature!!.steps.map {
        }

    }

}