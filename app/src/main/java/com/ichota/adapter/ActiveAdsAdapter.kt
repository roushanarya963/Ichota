package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterActiveAdsBinding
import com.ichota.model.ActivePost
import com.ichota.utils.Global
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActiveAdsAdapter : RecyclerView.Adapter<ActiveAdsAdapter.ActiveAdsViewHolder>() {

    private val posts = ArrayList<ActivePost>()
    private var mContext: Context? = null
    private var mCallback : MenuClickInterface? =null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveAdsViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterActiveAdsBinding.inflate(inflater, parent, false)
        return ActiveAdsViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ActiveAdsViewHolder, position: Int) {
        holder.bindView(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }


    fun removeItem(position: Int){
        try {
            posts.removeAt(position)
            notifyItemRemoved(position)

        }catch (e: IndexOutOfBoundsException){
            e.printStackTrace()
        }
    }

    fun setData(data: ArrayList<ActivePost>) {
        posts.clear()

        for (post in data) {
            posts.add(post)
            notifyItemInserted(posts.size - 1)
        }
    }

    inner class ActiveAdsViewHolder(val binding: AdapterActiveAdsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(post: ActivePost) {
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

           /* try {
                binding.tvAdPrice.text =String.format("$%,d", post.productPrice.toLong())
            }catch (e:Exception){

            }*/

          //  binding.tvAdPrice.text =String.format("$%,d", post.productPrice.toFloat())

            binding.tvAdPrice.text ="$"+post.productPrice

         //   binding.tvStatus.text = post.activeSoldStatus
            binding.tvStatus.text = ":- ${mContext?.getString(R.string.active)}"
            binding.root.setOnClickListener {
                mCallback?.onItemClick(post)
            }

            binding.tvMenu.setOnClickListener {
                val popupMenu = PopupMenu(mContext,binding.tvMenu)
                popupMenu.inflate(R.menu.menu_active_ads)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_item_mark_as_sold ->{
                            mCallback?.onMarkAsSold(adapterPosition,post.postId)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.menu_item_delete ->{
                        mCallback?.onItemDelete(adapterPosition,post.postId)
                            return@setOnMenuItemClickListener true
                    }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
                popupMenu.show()
            }
        }
    }

    fun onMenuClickListener(callback : MenuClickInterface){
        mCallback = callback
    }

    interface MenuClickInterface{
        fun onMarkAsSold(position:Int,postId : String)
        fun onItemDelete(position:Int,postId : String)
        fun onItemClick(post: ActivePost)
    }

}