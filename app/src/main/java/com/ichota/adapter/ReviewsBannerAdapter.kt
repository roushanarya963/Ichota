package com.ichota.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ichota.databinding.AdapterReviewBannerBinding

class ReviewsBannerAdapter : RecyclerView.Adapter<ReviewsBannerAdapter.ReviewBannerViewHolder>() {

    class ReviewBannerViewHolder(val binding : AdapterReviewBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewBannerViewHolder {
      val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterReviewBannerBinding.inflate(inflater,parent,false)
        return ReviewBannerViewHolder((binding))
    }

    override fun onBindViewHolder(holder: ReviewBannerViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 8
    }
}