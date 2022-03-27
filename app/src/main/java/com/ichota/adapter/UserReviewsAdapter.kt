package com.ichota.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterActiveAdsBinding
import com.ichota.databinding.AdapterUserReviewBinding
import com.ichota.model.Record
import com.ichota.utils.Global
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UserReviewsAdapter : RecyclerView.Adapter<UserReviewsAdapter.UserReviewsViewHolder>(){

    private val posts = ArrayList<Record>()
    private var mContext : Context?=null

    inner class UserReviewsViewHolder( val binding : AdapterUserReviewBinding) : RecyclerView.ViewHolder(binding.root){
                fun bindView() {

                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReviewsViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterUserReviewBinding.inflate(inflater,parent,false)
        return UserReviewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserReviewsViewHolder, position: Int) {
      var activervpost=  posts[position]
       holder.binding.tvName.text=activervpost.userName

        Glide.with(mContext?:return)
            .load(Global.getImageUrl(activervpost.userImage))
            .error(R.drawable.app_logo)
            .placeholder(R.drawable.app_logo)
            .into(holder.binding.ivUser)

        holder.binding.tvUserDescription.text= activervpost.userRating.toString()
        val bio = Global.formatItemListingDate(activervpost.time)
        holder.binding.tvReviewDate.text=bio
        holder.binding.rbUser.rating= activervpost.userRating.toFloat()
    }

    override fun getItemCount(): Int {
        return posts.size
    }


    fun setData(data: List<Record>) {
        posts.clear()
        for (notification in data) {
            posts.add(notification)
        }
        notifyDataSetChanged()
    }

}