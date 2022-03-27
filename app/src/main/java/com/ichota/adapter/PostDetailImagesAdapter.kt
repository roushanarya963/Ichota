package com.ichota.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterPostDetailImageBinding
import com.ichota.model.PostDetailImageModel
import com.ichota.utils.Global

class PostDetailImagesAdapter(val images: ArrayList<PostDetailImageModel>) :
    RecyclerView.Adapter<PostDetailImagesAdapter.PostDetailImagesViewHolder>() {
    private var mContext: Context? = null
    private var mCallback: IPostImageClick? = null


    inner class PostDetailImagesViewHolder(private val binding: AdapterPostDetailImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(detailImg: PostDetailImageModel) {

            Glide.with(mContext ?: return)
                .load(Global.getImageUrl(detailImg.image))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)

                .into(binding.ivProduct)

            Log.d("TAG", "bindView:  is Selected")

            if (detailImg.isSelected) {
                binding.viewOverlay.visibility = View.GONE
            } else {
                binding.viewOverlay.visibility = View.VISIBLE
            }

            binding.root.setOnClickListener {
                setSelected(adapterPosition)
            }

        }

    }


    fun setSelected(position : Int){
        Log.d("TAG", "bindView: $position ")
        mCallback?.onImageClicked(position)
        for ((index,value) in images.withIndex()){
            Log.d("TAG", "bindView: $index")
            images[index].isSelected = index == position
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailImagesViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterPostDetailImageBinding.inflate(inflater, parent, false)
        return PostDetailImagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostDetailImagesViewHolder, position: Int) {
        holder.bindView(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setOnImageClickListener(callback: IPostImageClick) {

        mCallback = callback
    }

    interface IPostImageClick {
        fun onImageClicked(position : Int)

    }
}