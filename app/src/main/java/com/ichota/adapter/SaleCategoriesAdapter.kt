package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterSalesBinding
import com.ichota.interfaces.ICategoryClickListener
import com.ichota.model.SaleCategory
import com.ichota.utils.Constants

class SaleCategoriesAdapter : RecyclerView.Adapter<SaleCategoriesAdapter.SaleCategoriesViewHolder>() {
    val saleCategories = ArrayList<SaleCategory>()
    private var mContext: Context? = null
    private var mCallback: ICategoryClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleCategoriesViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterSalesBinding.inflate(inflater, parent, false)
        return SaleCategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleCategoriesViewHolder, position: Int) {
        holder.bindView(saleCategories[position])

    }

    override fun getItemCount(): Int {
        return saleCategories.size
    }

    inner class SaleCategoriesViewHolder(val binding: AdapterSalesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(category: SaleCategory) {
            binding.tvCell.text = category.name
            Glide.with(mContext ?: return)
                .load(category.categoryImg)
                .placeholder(R.drawable.app_logo)
                .into(binding.ivCell)

            binding.root.setOnClickListener {
                mCallback?.onCategoryClick(category.categoryId, Constants.CATEGORY_SALE)
            }

        }
    }

    fun loadData(data: ArrayList<SaleCategory>) {
        saleCategories.clear()
        for (category in data) {
            saleCategories.add(category)
            notifyItemInserted(saleCategories.size - 1)

        }
    }

    fun onCategoryClickListener(callback: ICategoryClickListener) {
        mCallback = callback

    }



}