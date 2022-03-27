package com.ichota.adapter

import SwipeHelper
import android.content.Context
import android.graphics.*
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterNotificationBinding
import com.ichota.model.Notification
import com.ichota.utils.Global

class NotificationAdapter(private val context: Context) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private val notifications = ArrayList<Notification>()


    private var mCallback: INotification? = null

    inner class NotificationViewHolder(val binding: AdapterNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(notification: Notification) {

            binding.tvNotificationTitle.text = notification.type
            binding.tvNotificationDescription.text = notification.message ?: ""
            val bio = Global.formatItemListingDate(notification.createdAt)
            binding.tvNotificationTime.text = bio

            Glide.with(context)
                .load(notification.productCoverImage)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(binding.ivNotification)

            binding.root.setOnClickListener {
                mCallback?.onNotificationClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = AdapterNotificationBinding.inflate(inflater, parent, false)
        return NotificationViewHolder((binding))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bindView(notifications[position])
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun setData(data: ArrayList<Notification>) {
        notifications.clear()
        for (notification in data) {
            notifications.add(notification)
        }
        notifyDataSetChanged()
    }

    fun setupOnNotificationClickListener(callBack: INotification) {
        mCallback = callBack
    }

    fun getItemAt(position: Int): Notification? {
        return try {
            notifications[position]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    fun deleteItem(i:Int){
        notifications.removeAt(i)
        notifyDataSetChanged()
    }

    interface INotification {
        fun onNotificationClick(position: Int)
    }

}


