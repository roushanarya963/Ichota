package com.ichota.adapter

import android.content.Context
import android.provider.SyncStateContract
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterFavouriteBinding
import com.ichota.model.FavouriteItem
import com.ichota.utils.Constants

class FavouritesAdapter : RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {
    private val favouritePosts = ArrayList<FavouriteItem>()
    private lateinit var mContext: Context
    private var mCallback: FavItemClickListener? = null


    inner class FavouritesViewHolder(private val binding: AdapterFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(favouritePost: FavouriteItem) {
            Glide.with(mContext)
                .load(favouritePost.productCoverImg)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(binding.ivFavourite)

            binding.fabFav.setOnClickListener {

                if(favouritePost.postType==Constants.CATEGORY_SALE){
                    mCallback?.onFavClick(
                        adapterPosition,
                        favouritePost.productId
                    )
                }else{
                    mCallback?.onFavServiceClick(
                        adapterPosition,
                        favouritePost.productId
                    )
                }

               /* mCallback?.onFavClick(
                    adapterPosition,
                    favouritePost.productId
                )*/

            }

            binding.root.setOnClickListener {
                mCallback?.onFavoriteSalePostClick(
                    adapterPosition,
                    favouritePost.postType,
                    favouritePost.productId,
                    favouritePost.id
                )
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterFavouriteBinding.inflate(inflater, parent, false)
        return FavouritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        holder.bindView(favouritePosts[position])
    }

    override fun getItemCount(): Int {
        return favouritePosts.size
    }


    fun setData(data: ArrayList<FavouriteItem>) {
        favouritePosts.clear()
        for (item in data) {
            favouritePosts.add(item)
        }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        Log.d("TAG", "removeItem: $position")
        if (position >= 0 && position < favouritePosts.size) {
            favouritePosts.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun onFavItemClickListener(callback: FavItemClickListener) {
        this.mCallback = callback
    }

    interface FavItemClickListener {
        fun onFavClick(position: Int, postId: String)
        fun onFavServiceClick(position: Int, postId: String)
        fun onFavoriteSalePostClick(position: Int, postType: String, id: String, userId: String)
    }


}