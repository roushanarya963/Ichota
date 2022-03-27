package com.ichota.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterFollowersBinding
import com.ichota.model.User

class FollowersAdapter : RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder>() {
    private var mContext: Context? = null
    private val followers = ArrayList<User>()
    private var mCallback: FollowerClickInterface? = null

    class FollowersViewHolder(val binding: AdapterFollowersBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowersViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterFollowersBinding.inflate(inflater, parent, false)
        return FollowersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowersViewHolder, position: Int) {
        val follower = followers[position]

        Glide.with(mContext ?: return)
            .load(follower.userImage)
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.app_logo)
            .into(holder.binding.ivUser)

        holder.binding.tvName.text = follower.name

        holder.binding.root.setOnClickListener {
            mCallback?.onItemClick(follower.userId)
        }

        holder.binding.btRemove.setOnClickListener {
            mCallback?.onItemRemoveClick(
                position,
                follower
            )
        }
    }

    override fun getItemCount(): Int {
        return followers.size
    }

    fun setData(data: ArrayList<User>) {
        followers.clear()
        for (follower in data) {
            followers.add(follower)
            notifyItemInserted(followers.size - 1)
        }
    }

    fun removeItem(position: Int) {

        try {
            followers.removeAt(position)
            notifyItemRemoved(position)

        }catch (e: IndexOutOfBoundsException){

            Log.e("TAG", "NOT A VALID INDEX TO REMOVE ITEM: $e")

        }
    }


    fun onFollowerItemClickListener(callback: FollowerClickInterface) {
        this.mCallback = callback
    }

    interface FollowerClickInterface {
        fun onItemRemoveClick(position: Int, chat: User)
        fun onItemClick(userId:String)
    }


}