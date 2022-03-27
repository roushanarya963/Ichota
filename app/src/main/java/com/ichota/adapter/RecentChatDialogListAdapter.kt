package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterRecentChatDialogBinding
import com.ichota.model.ChatDialog
import com.ichota.model.MessageType
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecentChatDialogListAdapter(context:Context) : RecyclerView.Adapter<RecentChatDialogListAdapter.RecentChatDialogListViewHolder>() {

    var mContext: Context? = context
    private var helper: PreferenceHelper? = null
    private val chats = ArrayList<ChatDialog>()
    private var mCallback: IChatClick? = null

    init {
        helper = PreferenceHelper.getPreferences(context)
    }

    inner class RecentChatDialogListViewHolder(private val binding: AdapterRecentChatDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(chatDialog: ChatDialog) {
            binding.tvName.text =
                if (chatDialog.receiverId == helper?.getCurrentUser()?.userId) chatDialog.userName else chatDialog.userName
            binding.root.setBackgroundColor(
                if (chatDialog.readStatus == "1")

                    (mContext ?: return).getColor(R.color.colorWhite)
                else
                    (mContext ?: return).getColor(R.color.colorGrey200)
            )

           /* if (chat.message == "0") {
                binding.tvUnreadMessageCount.visibility = View.GONE
            } else {
                binding.tvUnreadMessageCount.text = chat.message
                binding.tvUnreadMessageCount.visibility = View.VISIBLE
            }*/

            val userImage = if (chatDialog.receiverId == helper?.getCurrentUser()?.userId)
                Global.getImageUrl(chatDialog.userImage1) else Global.getImageUrl(chatDialog.userImage1)

            Glide.with(mContext ?: return)
                .load(Global.getImageUrl(userImage))
                .placeholder(R.drawable.img_user_placeholder)
                .error(R.drawable.img_user_placeholder)
                .into(binding.ivUser)

            Glide.with(mContext ?: return)
                .load(Global.getImageUrl(chatDialog.productCoverImage))
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(binding.ivProduct)

            when (chatDialog.msgType) {
                MessageType.TYPE_IMAGE -> binding.tvMessage.text = MessageType.TYPE_IMAGE
                MessageType.TYPE_LOCATION -> binding.tvMessage.text = MessageType.TYPE_LOCATION
                MessageType.TYPE_OFFER-> binding.tvMessage.text=MessageType.TYPE_OFFER
                else -> binding.tvMessage.text = chatDialog.message
            }

            binding.tvTime.text = Global.getTimeDifference(
                chatDialog.createdDtm,
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            )

            binding.root.setOnClickListener {
                mCallback?.onOpenChat(absoluteAdapterPosition,chatDialog.postType.toString())
            }
        }

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentChatDialogListAdapter.RecentChatDialogListViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterRecentChatDialogBinding.inflate(inflater, parent, false)
        return RecentChatDialogListViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return chats.size

    }

    override fun onBindViewHolder(holderDialog: RecentChatDialogListViewHolder, position: Int) {
        holderDialog.bindView(chats[position])
    }


    fun setData(data: List<ChatDialog>) {
        if (chats.isNotEmpty()) {
            chats.clear()
        }

        for (value in data) {
            chats.add(value)
        }
        notifyDataSetChanged()
    }


    fun getItemAt(position:Int) : ChatDialog?{
      return  try {
            chats[position]
        }catch (e:IndexOutOfBoundsException){
            null
        }
    }


    fun setOnChatClickListener(callback: IChatClick) {
        mCallback = callback
    }

    interface IChatClick {
        fun onOpenChat(
            position:Int,
            postType:String
        )
    }

}