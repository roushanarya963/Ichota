package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.R
import com.ichota.databinding.AdapterServicesBinding
import com.ichota.interfaces.ICategoryClickListener
import com.ichota.model.ServiceCategory
import com.ichota.utils.Constants
import com.ichota.utils.Global

class ServiceCategoriesAdapter :
    RecyclerView.Adapter<ServiceCategoriesAdapter.ServiceCategoriesViewHolder>() {


    private val serviceCategories = ArrayList<ServiceCategory>()
    private lateinit var mContext: Context
    private var mCallback  :ICategoryClickListener? =null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceCategoriesViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val binding = AdapterServicesBinding.inflate(inflater, parent, false)
        return ServiceCategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceCategoriesViewHolder, position: Int) {
        holder.bindView(serviceCategories[position])
    }

    override fun getItemCount(): Int {
        return serviceCategories.size
    }


    fun setData(data: ArrayList<ServiceCategory>) {
        serviceCategories.clear()
        for (service in data) {
            serviceCategories.add(service)
            notifyItemInserted(serviceCategories.size - 1)
        }

    }

  inner  class ServiceCategoriesViewHolder(val binding: AdapterServicesBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bindView(category : ServiceCategory){
                binding.tvCell.text =if (category.membershipStatus != "1") category.name else ""
                Glide.with(mContext)
                    .load(
                        if (category.membershipStatus =="1")R.drawable.img_hidden_category
                            else
                                Global.getImageUrl(category.image)
                    )
                    .placeholder(R.drawable.app_logo)
                    .into(binding.ivCell)
                binding.root.setOnClickListener {
                    if (category.membershipStatus=="1"){
                        showMemberShipOnlyAlert()
                    }else{
                        mCallback?.onCategoryClick(category.id, Constants.CATEGORY_SERVICE)
                    }

                }
            }
        }

    fun onCategoryClickListener(callback : ICategoryClickListener){
        mCallback = callback

    }

    fun showMemberShipOnlyAlert(){
        MaterialAlertDialogBuilder(mContext)
            .setTitle(mContext.getString(R.string.alert))
            .setMessage(mContext.getString(R.string.messagePrimeOnly))
            .setPositiveButton(mContext.getString(R.string.ok)){dialog,which->
                dialog.dismiss()
            }
            .show()
    }



}