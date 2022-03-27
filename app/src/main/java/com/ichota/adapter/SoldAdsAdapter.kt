package com.ichota.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterActiveAdsBinding
import com.ichota.model.SoldPost
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SoldAdsAdapter : RecyclerView.Adapter<SoldAdsAdapter.SoldAdsViewHolder>() {

    private var mContext: Context? = null
    private val posts = ArrayList<SoldPost>()
    private var mCallback :SoldAdsClickInterface? =null
    private var helper:PreferenceHelper?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldAdsViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        helper=PreferenceHelper.getPreferences(mContext!!)
        val binding = AdapterActiveAdsBinding.inflate(inflater, parent, false)
        return SoldAdsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoldAdsViewHolder, position: Int) {
        holder.bindView(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }


    fun setData(data: ArrayList<SoldPost>) {
        posts.clear()
        for (post in data) {
            posts.add(post)
            notifyItemInserted(posts.size - 1)
        }
    }

    fun removeItem(position : Int){
        try {
            posts.removeAt(position)
            notifyItemRemoved(position)
        }catch (e: Exception){
            Log.e("TAG", "removeItem: ${e.printStackTrace()}")

        }


    }

    inner class SoldAdsViewHolder(val binding: AdapterActiveAdsBinding) :
        RecyclerView.ViewHolder(binding.root){

            fun bindView(post : SoldPost){

                Glide.with(mContext?:return)
                    .load(Global.getImageUrl(post.coverImage))
                    .error(R.drawable.app_logo)
                    .placeholder(R.drawable.app_logo)
                    .into(binding.ivAd)
                binding.tvAdTitle.text = post.postName

                binding.tvAdTime.text = mContext?.getString(
                    R.string.postedTime, Global.getTimeDifference(
                        post.createdDtm,
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    )
                )


                helper?.saveStringValue(PrefKeys.KEY_SOLD_PRODUCT_STATS,post.activeSoldStatus)

                if(post.activeSoldStatus=="1"){
                    binding.tvStatus.text=mContext?.getString(R.string.sold)
                }

                binding.tvAdPrice.text ="$"+ post.productPrice.toString()

                binding.root.setOnClickListener {
                    mCallback?.onItemClick(post)
                }

                binding.tvMenu.setOnClickListener {
                    val popupMenu = PopupMenu(mContext,binding.tvMenu)
                    popupMenu.inflate(R.menu.menu_sold_ads)
                    popupMenu.setOnMenuItemClickListener {
                        if (it.itemId == R.id.menu_item_delete){
                            mCallback?.onDeleteMenuClick(post.postId,adapterPosition)
                            return@setOnMenuItemClickListener true

                        }
                        return@setOnMenuItemClickListener false
                    }
                    popupMenu.show()
                }


            }
        }

    fun onSoldItemClickListener(callback : SoldAdsClickInterface){
        mCallback = callback

    }

    interface SoldAdsClickInterface {
        fun onDeleteMenuClick(postId :String,position : Int)
        fun onItemClick(post : SoldPost)

    }
}