package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ichota.R
import com.ichota.databinding.AdapterImgBinding


class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    private val images = ArrayList<String>()
    private var mContext: Context? = null
    private var mCallback: ImageClickInterface? = null

    inner class ImagesViewHolder(private val binding: AdapterImgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(image: String) {

            Glide.with(mContext ?: return)
                .load(image)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivPost)


            binding.ivPost.setOnClickListener { mCallback?.onImageClick(image,adapterPosition) }
            binding.ivDelete.setOnClickListener { mCallback?.onDeleteImage(adapterPosition) }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterImgBinding.inflate(inflater, parent, false)
        return ImagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.bindView(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }



    fun setData(data: ArrayList<String>) {
        images.clear()
        images.addAll(data)
        notifyDataSetChanged()
    }

    fun onImageClickListener(callback: ImageClickInterface) {
        this.mCallback = callback
    }


    interface ImageClickInterface {
        fun onImageClick(imagePath: String,position : Int)
        fun onDeleteImage(position : Int)
    }


}