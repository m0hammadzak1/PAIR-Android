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
import com.sova.pair.model.Step
import com.sova.pair.ui.adapter.TroubleShootAdapter
import com.sova.pair.utils.Common

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
            if (feature!!.talkback) {
                Common.startTts(feature?.des ?: "Working On It")
            }
        }, 2000)

        handler.postDelayed({
            binding.tvFeatureDes.text = feature?.response ?: ""
            binding.pbLoad.visibility = View.GONE
            binding.ivDone.visibility = View.VISIBLE
            updateRouterData()
            setRouterStatus()
            if (!feature!!.internet || !feature!!.isOnline) {
                binding.ivDone.setImageDrawable(getDrawable(R.drawable.ic_close))
            } else binding.ivDone.setImageDrawable(getDrawable(R.drawable.ic_done))
            if (feature!!.talkback) {
                Common.startTts(feature?.response ?: "Done")
            }
            if (feature!!.talkbackSteps) {
                val stepss = feature!!.problem.joinToString { "$it\n\n\n\n\n\n\n" }
                Common.startTts(stepss)
            }
        }, 10000)

        if (feature!!.steps.isNotEmpty()) {
            binding.slSteps.visibility = View.VISIBLE
            setStepAdapter()
        } else {
            binding.slSteps.visibility = View.GONE
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
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
            Step(it, false)
        }
        val adapter = TroubleShootAdapter(steps)
        binding.slSteps.setAdapter(adapter)

        handler.postDelayed({
            steps[steps.size - 1].isActive = true
            binding.slSteps.setAdapter(adapter)
        }, 5000)

    }

}