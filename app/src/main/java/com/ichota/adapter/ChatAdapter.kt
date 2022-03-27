package com.ichota.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ichota.R
import com.ichota.databinding.LayoutMessageImageBinding
import com.ichota.databinding.LayoutMessageLocationBinding
import com.ichota.databinding.LayoutMessageNormalBinding
import com.ichota.databinding.LayoutMessageOfferBinding
import com.ichota.model.Message
import com.ichota.model.MessageType
import com.ichota.model.PostDetail
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "ChatAdapter"

private const val TYPE_MESSAGE = 0
private const val TYPE_OFFER = 1
private const val TYPE_LOCATION = 2
private const val TYPE_IMAGE = 3

class ChatAdapter(val callback: ChatMessageClickInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messages = ArrayList<Message>()
    private var mContext: Context? = null
    private var mCallback: ChatMessageClickInterface? = null

    init {
        this.mCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)

        return when (viewType) {
            TYPE_OFFER -> {
                val binding = LayoutMessageOfferBinding.inflate(inflater, parent, false)
                OfferViewHolder(binding)
            }

            TYPE_LOCATION -> {
                val binding = LayoutMessageLocationBinding.inflate(inflater, parent, false)
                LocationViewHolder(binding)
            }

            TYPE_IMAGE -> {
                val binding = LayoutMessageImageBinding.inflate(inflater, parent, false)
                ImageViewHolder(binding)
            }
            else -> {
                val binding = LayoutMessageNormalBinding.inflate(inflater, parent, false)
                MessageViewHolder(binding)
            }

        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder) {
            is MessageViewHolder -> holder.onBind(message)

            is OfferViewHolder -> holder.onBind(message)

            is LocationViewHolder -> holder.onBind(message)

            is ImageViewHolder -> holder.onBind(message)
        }

    }

    override fun getItemCount(): Int {
        return messages.size
      //  return postdetail.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].msgType) {
            MessageType.TYPE_OFFER -> TYPE_OFFER
            MessageType.TYPE_LOCATION -> TYPE_LOCATION
            MessageType.TYPE_IMAGE -> TYPE_IMAGE
            else -> TYPE_MESSAGE
        }


    }

    fun setData(data: ArrayList<Message>) {
        messages.clear()
        messages.addAll(data)
        notifyDataSetChanged()
    }

    private fun getFormattedTime(source: String?): String {
        if (source == null) return "No Time_available"
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(source)
            SimpleDateFormat("HH:mm aa", Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            "No Time_available"
        }
    }

    inner class MessageViewHolder(private val binding: LayoutMessageNormalBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(message: Message) {
            binding.tvUserName.text = message.senderName
            Glide.with(mContext ?: return)
                .load(Global.getImageUrl(message.senderImage))
                .placeholder(R.drawable.img_user_placeholder)
                .error(R.drawable.img_user_placeholder)
                .into(binding.ivUser)
            binding.tvTime.text = getFormattedTime(message.msgTime)
            binding.tvMsg.text = message.message


            binding.ivUser.setOnClickListener {
                if (message.senderId != PreferenceHelper.getPreferences(mContext ?: return@setOnClickListener).getCurrentUser()?.userId ?: ""
                ) {
                    message.senderId?.let { it1 -> mCallback?.onProfilePictureClick(it1) }
                }else{
                    message.senderId?.let { it -> mCallback?.onUserProfilePictureClick(it) }
                }
            }

        }

    }

    inner class OfferViewHolder(private val binding: LayoutMessageOfferBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(message: Message) {
            binding.tvUserName.text = message.senderName
            Glide.with(mContext ?: return)
                .load(Global.getImageUrl(message.senderImage))
                .placeholder(R.drawable.img_user_placeholder)
                .error(R.drawable.img_user_placeholder)
                .into(binding.ivUser)
            binding.tvTime.text = getFormattedTime(message.msgTime)

            try {
                val price = message.message?.let { String.format("$%,d", it.toLong()) }
                binding.tvOfferPrice.text = price
            }catch (e:Exception){

            }



            binding.ivUser.setOnClickListener {
                if (message.senderId != PreferenceHelper.getPreferences(
                        mContext ?: return@setOnClickListener
                    ).getCurrentUser()?.userId ?: ""
                ) {
                    message.senderId?.let { it1 -> mCallback?.onProfilePictureClick(it1) }
                }else{
                    message.senderId?.let { it ->mCallback?.onUserProfilePictureClick(it) }
                }
            }

        }
    }

    inner class LocationViewHolder(private val binding: LayoutMessageLocationBinding) : RecyclerView.ViewHolder(binding.root), OnMapReadyCallback {
        private lateinit var mGoogleMap: GoogleMap
        private var mMessage: Message? = null
        private var mPostDetail: PostDetail? = null

        init {
            with(binding.mapView) {
                onCreate(null)
                getMapAsync(this@LocationViewHolder)
            }
        }
        fun onBind(message: Message) {
            mMessage = message
            binding.tvUserName.text = message.senderName
            Glide.with(mContext ?: return)
                .load(Global.getImageUrl(message.senderImage))
                .placeholder(R.drawable.img_user_placeholder)
                .error(R.drawable.img_user_placeholder)
                .into(binding.ivUser)
            binding.tvTime.text = getFormattedTime(message.msgTime)

            binding.ivUser.setOnClickListener {
                if (message.senderId != PreferenceHelper.getPreferences(
                        mContext ?: return@setOnClickListener
                    ).getCurrentUser()?.userId ?: ""
                ) {
                    message.senderId?.let { it1 -> mCallback?.onProfilePictureClick(it1) }
                }else{
                    message.senderId?.let { it -> mCallback?.onUserProfilePictureClick(it) }
                }
            }

            binding.viewMap.setOnClickListener {

                mCallback?.onLocationClick(message)
            }

        }

        override fun onMapReady(googleMap: GoogleMap?) {
            MapsInitializer.initialize(mContext?:return)
            mGoogleMap = googleMap ?: return
            val currentLocation = LatLng(mMessage?.lat?.toDouble() ?: 0.0, mMessage?.lng?.toDouble() ?: 0.0)
            val marker = MarkerOptions()
            val options = GoogleMapOptions()
            options.liteMode(true)
            mGoogleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            marker.position(currentLocation)
            mGoogleMap.addMarker(marker)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f))
        }

    }


    inner class ImageViewHolder(private val binding: LayoutMessageImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(message: Message) {
            binding.tvUserName.text = message.senderName
            binding.tvTime.text = getFormattedTime(message.msgTime)
            Glide.with(mContext ?: return)
                .load(Global.getImageUrl(message.senderImage))
                .placeholder(R.drawable.img_user_placeholder)
                .error(R.drawable.img_user_placeholder)
                .into(binding.ivUser)

            Glide.with(mContext ?: return)
                .load(Global.getImageUrl(message.sentImage))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.ivProduct)

            binding.ivProduct.setOnClickListener {
                message.sentImage?.let { it1 -> mCallback?.onImageClick(it1) }
            }

            binding.ivUser.setOnClickListener {
                if (message.senderId != PreferenceHelper.getPreferences(
                        mContext ?: return@setOnClickListener
                    ).getCurrentUser()?.userId ?: ""
                ) {
                    message.senderId?.let { it1 -> mCallback?.onProfilePictureClick(it1) }
                }else{
                    message.senderId?.let { it -> mCallback?.onUserProfilePictureClick(it) }
                }
            }

        }
    }


    interface ChatMessageClickInterface {
        fun onImageClick(img: String)
        fun onProfilePictureClick(userId: String)
        fun onUserProfilePictureClick(userId: String)
        fun onLocationClick(message:Message)
    }


}

