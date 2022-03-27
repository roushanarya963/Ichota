package com.ichota.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ichota.databinding.AdapterConditionsBinding
import com.ichota.model.Condition
import com.ichota.utils.Constants


class ConditionsAdapter(callback: ConditionSelectInterface) :
    RecyclerView.Adapter<ConditionsAdapter.ConditionsViewHolder>() {

    private var mCallback: ConditionSelectInterface = callback

    val conditions = arrayOf(
        Condition(Constants.CONDITION_NEW, true),
        Condition(Constants.CONDITION_LIKE_NEW),
        Condition(Constants.CONDITION_GOOD),
        Condition(Constants.CONDITION_FAIR),
        Condition(Constants.CONDITION_BAD)
    )

    class ConditionsViewHolder(val binding: AdapterConditionsBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConditionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterConditionsBinding.inflate(inflater, parent, false)
        return ConditionsViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ConditionsViewHolder, position: Int) {
        val condition = conditions[position]
        holder.binding.rbCondition.text = condition.name
        holder.binding.rbCondition.isChecked = condition.isChecked
        holder.binding.rbCondition.setOnClickListener {
            setChecked(position)
            mCallback.onConditionSelect(condition.name)
        }


    }

    override fun getItemCount(): Int {
        return conditions.size
    }

    private fun setChecked(position: Int) {
        for ((index, condition) in conditions.withIndex()) {
            condition.isChecked = position == index
        }
        notifyDataSetChanged()

    }


    interface ConditionSelectInterface {
        fun onConditionSelect(condition: String)
    }

}