package com.ichota.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterUserMarketplaceBinding
import com.ichota.model.MarketPlacePost
import com.ichota.utils.Constants

class PublicProfileMarketplaceAdapter(
    posts: ArrayList<MarketPlacePost>,
    callback: MarketplaceItemClickInterface
) :
    RecyclerView.Adapter<PublicProfileMarketplaceAdapter.MarketplaceViewHolder>() {

    private var posts = emptyList<MarketPlacePost>()
    private var mCallback: MarketplaceItemClickInterface? = null
    private lateinit var mContext: Context

    init {
        this.posts = posts
        this.mCallback = callback

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketplaceViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterUserMarketplaceBinding.inflate(inflater, parent, false)
        return MarketplaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketplaceViewHolder, position: Int) {
        holder.bindView(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

   /* fun setData(data: ArrayList<MarketPlacePost>) {
        posts.clear()
        for (post in data) {
            posts.add(post)
            notifyItemInserted(posts.size - 1)
        }
    }*/

    inner class MarketplaceViewHolder(private val binding: AdapterUserMarketplaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(post: MarketPlacePost) {

            Glide.with(mContext)
                .load(post.productCoverImage)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(binding.ivMarketplaceItems)

            binding.ivBid.visibility =
                if (post.buyingOptions.equals("BID", true)) View.VISIBLE else View.GONE


            binding.root.setOnClickListener {

                mCallback?.onPostClick(post)

            }
        }
    }


    interface MarketplaceItemClickInterface {
        fun onPostClick(post: MarketPlacePost)
    }

}