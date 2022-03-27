package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterBidsBinding
import com.ichota.model.BidDetail
import com.ichota.utils.Global
import java.text.SimpleDateFormat
import java.util.*

class BidsAdapter(val bids: ArrayList<BidDetail>) :
    RecyclerView.Adapter<BidsAdapter.BidsViewHolder>() {
    private var mContext: Context? = null


    class BidsViewHolder(val binding: AdapterBidsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidsViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterBidsBinding.inflate(inflater, parent, false)
        return BidsViewHolder(binding)

    }

    override fun onBindViewHolder(holder: BidsViewHolder, position: Int) {
        val bid = bids[position]
        try {
            holder.binding.tvBidPrice.text = String.format("$%,d", bid.bidAmount.toLong())
        }catch (e:Exception){

        }

        holder.binding.tvUserName.text = bid.name
        Glide.with(mContext ?: return)
            .load(bid.userImage)
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.app_logo)
            .into(holder.binding.ivUser)
        holder.binding.tvBidTime.text = Global.getTimeDifference(
            bid.createdOn,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
    }

    override fun getItemCount(): Int {
        return bids.size
    }


}