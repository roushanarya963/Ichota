package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterActiveAdsBinding
import com.ichota.databinding.AdapterMarkAndSoldBinding
import com.ichota.model.ActivePost
import com.ichota.model.UserChatData
import com.ichota.utils.Global

class MarkAndSoldAdapter : RecyclerView.Adapter<MarkAndSoldAdapter.MarkAndSoldViewHolder>() {

    private var mContext: Context? = null
    private val userChatData = ArrayList<UserChatData>()
    private var mCallback : UserChatListClickInterface? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkAndSoldViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterMarkAndSoldBinding.inflate(inflater, parent, false)
        return MarkAndSoldViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarkAndSoldViewHolder, position: Int) {
       holder.bindView(userChatData[position])
    }

    override fun getItemCount(): Int {
       return userChatData.size
    }



    fun setData(data: ArrayList<UserChatData>) {
        userChatData.clear()

        for (post in data) {
            userChatData.add(post)
            notifyItemInserted(userChatData.size - 1)
        }
    }


    inner class MarkAndSoldViewHolder(val binding: AdapterMarkAndSoldBinding): RecyclerView.ViewHolder(binding.root) {
         fun bindView(userChatData: UserChatData){
             Glide.with(mContext?:return).load(Global.getImageUrl(userChatData.userImage))
                 .error(R.drawable.app_logo)
                 .placeholder(R.drawable.app_logo)
                 .into(binding.ivMarkAndSold)


             binding.tvUserName.text=userChatData.userName

             binding.root.setOnClickListener {
                 mCallback?.onItemClick(userChatData)
             }

         }
    }


    fun onItemlistClickListener(callback:UserChatListClickInterface){
        mCallback=callback
    }

    interface UserChatListClickInterface{
        fun onItemClick(userChatData: UserChatData)
    }


}