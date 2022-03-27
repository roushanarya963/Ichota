package com.ichota.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterServicePostBinding
import com.ichota.model.ServicePost
import com.ichota.utils.Constants
import com.ichota.utils.Global

private const val TAG = "ServicePostAdapter"

class ServicePostsAdapter : RecyclerView.Adapter<ServicePostsAdapter.ServicePostsViewHolder>() {

    private val servicePosts = ArrayList<ServicePost>()
    private lateinit var mContext: Context
    private var mCallback: ServiceItemClickInterface? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicePostsViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterServicePostBinding.inflate(inflater, parent, false)
        return ServicePostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicePostsViewHolder, position: Int) {
        holder.bindView(servicePosts[position])
    }

    override fun getItemCount(): Int {
        return servicePosts.size
    }

    fun setData(data: List<ServicePost>) {
        if(servicePosts.isNotEmpty()){
            servicePosts.clear()
        }
        for (post in data) {
            servicePosts.add(post)
        }
        notifyDataSetChanged()
    }

    inner class ServicePostsViewHolder(private val binding: AdapterServicePostBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bindView(post : ServicePost){
                Glide.with(mContext)
                    .load(Global.getImageUrl(post.serviceCoverImg))
                    .placeholder(R.drawable.app_logo)
                    .into(binding.ivPost)

                binding.root.setOnClickListener {
                    mCallback?.onServiceItemClick(post)

                }

            }

        }



    fun setOnServiceItemClickListener(callback: ServiceItemClickInterface) {
        mCallback = callback

    }

    interface ServiceItemClickInterface {
        fun onServiceItemClick(post: ServicePost)
    }


}