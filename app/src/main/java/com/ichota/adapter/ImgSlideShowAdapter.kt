package com.ichota.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterSliderImgBinding
import com.ichota.utils.Global

class ImgSlideShowAdapter(val images: ArrayList<String>, callback: ImageClickInterface,val zoomable: Boolean = false) :
    RecyclerView.Adapter<ImgSlideShowAdapter.ImgSlideShowViewHolder>() {

    private var mCallback: ImageClickInterface? = null

    init {
        mCallback = callback
    }

  inner  class ImgSlideShowViewHolder(private val binding: AdapterSliderImgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(img: String) {

            Glide.with(binding.root.context)
                .load(Global.getImageUrl(img))
                .placeholder(R.drawable.img_placeholder)
                .into(binding.ivProduct)

            if (!zoomable){
                binding.ivProduct.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.ivProduct.setOnClickListener {
                    mCallback?.onImageClick(adapterPosition)
                }
            }
            else{
                binding.ivProduct.scaleType = ImageView.ScaleType.FIT_CENTER
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgSlideShowViewHolder {
        val binding =
            AdapterSliderImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ImgSlideShowViewHolder((binding))

    }

    override fun onBindViewHolder(holder: ImgSlideShowViewHolder, position: Int) {
        holder.bindView(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }


    interface ImageClickInterface {
        fun onImageClick(position: Int)
    }
}