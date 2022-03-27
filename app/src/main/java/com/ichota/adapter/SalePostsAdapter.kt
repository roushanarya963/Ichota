package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterSalePostsBinding
import com.ichota.model.SalePost
import com.ichota.utils.Global

class SalePostsAdapter : RecyclerView.Adapter<SalePostsAdapter.SalePostsViewHolder>() {
    private val salePosts = ArrayList<SalePost>()
    private lateinit var mContext: Context
    private var mCallback: SaleItemClickInterface? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalePostsViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterSalePostsBinding.inflate(inflater, parent, false)
        return SalePostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalePostsViewHolder, position: Int) {
        holder.bindView(salePosts[position])
    }

    override fun getItemCount(): Int {
        return salePosts.size
    }

    fun setData(data: List<SalePost>) {
        if(salePosts.isNotEmpty()){
            salePosts.clear()
        }
        for (post in data) {
            salePosts.add(post)
        }
        notifyDataSetChanged()
    }

    inner class SalePostsViewHolder(private val binding: AdapterSalePostsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(post: SalePost) {

            Glide.with(mContext).load(Global.getImageUrl(post.productCoverImg)).placeholder(R.drawable.app_logo).into(binding.ivPost)
            binding.ivBid.visibility = if (post.buyingOptions.equals("Bid",true)) View.VISIBLE else View.GONE
            binding.root.setOnClickListener {
                mCallback?.onSalePostClick(absoluteAdapterPosition)
            }
           // mCallback?.onSalePostSize(getItemAt(absoluteAdapterPosition))
        }
    }


    fun getItemAt(position:Int):SalePost?{
      return  try {
            salePosts[position]
        }catch (e:IndexOutOfBoundsException){
            null
        }
    }

    fun setOnSaleItemClickListener(callback: SaleItemClickInterface) {
        mCallback = callback
    }

    interface SaleItemClickInterface {
        fun onSalePostClick(position:Int)
        fun onSalePostSize(size:Int)

    }
}