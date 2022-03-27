package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterSalePostsBinding
import com.ichota.model.SearchPost
import com.ichota.utils.Global

class SearchPostAdapter(
    searchPost: List<SearchPost>,
    context: Context,
    callback: ISearchItemClick
) : RecyclerView.Adapter<SearchPostAdapter.SearchPostViewHolder>() {
    private var mContext: Context = context
    private var mCallback: ISearchItemClick? = null
    private var mSearchPosts = emptyList<SearchPost>()

    init {
        mSearchPosts = searchPost
        mCallback = callback
    }


    inner class SearchPostViewHolder(private val binding: AdapterSalePostsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(post : SearchPost) {

            Glide.with(mContext)
                .load(Global.getImageUrl(post.coverImage))
                .placeholder(R.drawable.app_logo)
                .into(binding.ivPost)

            binding.ivBid.visibility =
                if (post.buyingOptions.equals("Bid",true)) View.VISIBLE else View.GONE


            binding.root.setOnClickListener {
                mCallback?.onItemClick(post)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterSalePostsBinding.inflate(inflater, parent, false)
        return SearchPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchPostViewHolder, position: Int) {
       holder.bindView(mSearchPosts[position])
    }

    override fun getItemCount(): Int {
        return mSearchPosts.size
    }
}


interface ISearchItemClick {
    fun onItemClick(searchPost: SearchPost)
}