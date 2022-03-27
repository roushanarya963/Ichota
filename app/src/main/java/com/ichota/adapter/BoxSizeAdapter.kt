package com.ichota.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ichota.R
import com.ichota.databinding.AdapterBoxSizeBinding
import com.ichota.model.BoxSizeModel

class BoxSizeAdapter : RecyclerView.Adapter<BoxSizeAdapter.BoxSizeViewHolder>() {
    private var mContext: Context? = null
    private var mCallback: BoxSizeInterface? = null

    private val boxSizes = arrayOf(
        BoxSizeModel("Box(5lb)", "$3.29", true),
        BoxSizeModel("9\"x6\"x3\"'", "$7.49"),
        BoxSizeModel("12\"x9\"x6\"'", "$10.99"),
        BoxSizeModel("14\"x10\"x6\"'", "$13.99")
    )


    class BoxSizeViewHolder(val binding: AdapterBoxSizeBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxSizeViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterBoxSizeBinding.inflate(inflater, parent, false)
        return BoxSizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoxSizeViewHolder, position: Int) {
        val boxSize = boxSizes[position]

        holder.binding.tvBoxDimensions.text = boxSize.dimension
        holder.binding.tvPrice.text = boxSize.price

        holder.binding.cardBox.cardElevation = if (boxSize.isChecked) 18f else 0f


        holder.binding.tvBoxDimensions.setTextColor(
            if (boxSize.isChecked) ContextCompat.getColor(
                mContext ?: return,
                R.color.colorWhite
            ) else ContextCompat.getColor(
                mContext ?: return,
                R.color.colorTextPrimary
            )
        )

        holder.binding.ivBox.setColorFilter(
            if (boxSize.isChecked) ContextCompat.getColor(
                mContext ?: return,
                R.color.colorWhite
            ) else ContextCompat.getColor(
                mContext ?: return,
                R.color.colorTextPrimary
            ), PorterDuff.Mode.SRC_IN
        )


        holder.binding.cardBox.setCardBackgroundColor(
            if (boxSize.isChecked) ContextCompat.getColor(
                mContext ?: return,
                R.color.colorPrimary
            ) else ContextCompat.getColor(
                mContext ?: return,
                R.color.colorGrey200
            )
        )


        holder.binding.root.setOnClickListener {
            setChecked(position)

        }


    }

    override fun getItemCount(): Int {
        return boxSizes.size
    }

    private fun setChecked(position: Int) {
        for ((index, boxSize) in boxSizes.withIndex()) {
            boxSize.isChecked = position == index
        }
        notifyDataSetChanged()
    }


    fun onBoxSelectListener(callback: BoxSizeInterface) {

        this.mCallback = callback
    }

    interface BoxSizeInterface {
        fun onBoxSizeSelect(dimension: String)
    }
}