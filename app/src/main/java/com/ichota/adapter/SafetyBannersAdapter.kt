package com.ichota.adapter

import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.databinding.AdapterSafetyBannersBinding
import com.ichota.model.SafetyBanner
import com.ichota.utils.Global

class SafetyBannersAdapter : RecyclerView.Adapter<SafetyBannersAdapter.SafetyBannersViewHolder>() {
    private val safetyBanners = ArrayList<SafetyBanner>()

    inner class SafetyBannersViewHolder(private val binding : AdapterSafetyBannersBinding) :
            RecyclerView.ViewHolder(binding.root){
                fun bindView(safetyBanners: SafetyBanner){
                    Glide.with(binding.root.context)
                        .load(Global.getImageUrl(safetyBanners.image))
                        .into(binding.ivBanner)
                    binding.tvTitle.text = safetyBanners.title
                    binding.tvDescription.text = safetyBanners.description
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SafetyBannersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterSafetyBannersBinding.inflate(inflater,parent,false)
        return  SafetyBannersViewHolder(binding)

    }

    override fun onBindViewHolder(holder: SafetyBannersViewHolder, position: Int) {
     holder.bindView(safetyBanners[position])
    }

    override fun getItemCount(): Int {
        return  safetyBanners.size
    }

    fun setData(data:List<SafetyBanner>){
        if(safetyBanners.isNotEmpty()){
            safetyBanners.clear()
        }
        for(banner in data){
            safetyBanners.add(banner)
        }
        notifyDataSetChanged()
    }
}