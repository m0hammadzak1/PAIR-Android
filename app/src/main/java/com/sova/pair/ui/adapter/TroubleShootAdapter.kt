package com.sova.pair.ui.adapter

import com.sova.pair.R
import com.sova.pair.model.Step
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceStep

/**
 * Created by Zaki on 09-01-2024.
 */
class TroubleShootAdapter(private val steps: List<Step>) : SequenceAdapter<Step>() {
    override fun bindView(sequenceStep: SequenceStep, item: Step) {
        with(sequenceStep) {
            setActive(item.isActive)
            setTitle(item.step)
            setTitleTextAppearance(R.style.FontMedium)
            setAnchorMinWidth(0)
            setAnchorMinWidth(0)
        }
    }

    override fun getCount(): Int {
        return steps.size
    }

    override fun getItem(position: Int): Step {
        return steps[position]
    }

    fun updateStep(position: Int) {
        steps[position].isActive = true
    }

}