package com.ichota.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.databinding.AdapterPaymentMethodsBinding
import com.ichota.model.PaymentMethod
import com.ichota.model.PaymentMethodsResModel
import com.ichota.utils.Global

class PaymentMethodAdapter(private val context: Context) :
    RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder>() {


    private var mCallback: IPaymentMethods? = null
    private val paymentMethods = ArrayList<PaymentMethod>()


    inner class PaymentMethodViewHolder(val binding: AdapterPaymentMethodsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindVew(paymentMethod: PaymentMethod) {
            Glide.with(binding.root.context)
                .load(Global.getImageUrl(paymentMethod.productCoverImage))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.ivPaymentMethod)
            binding.tvPaymentMethod.text = paymentMethod.title

            Glide.with(context)
                .load(if (paymentMethod.status == "0") R.drawable.ic_check_normal else R.drawable.ic_check_enable)
                .into(binding.ivCheck)

            binding.root.setOnClickListener {
                mCallback?.onPaymentMethodClick(paymentMethod)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = AdapterPaymentMethodsBinding.inflate(inflater, parent, false)
        return PaymentMethodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.bindVew(paymentMethods[position])
    }

    override fun getItemCount(): Int {
        return paymentMethods.size
    }

    fun setData(data:List<PaymentMethod>){
        if(paymentMethods.isNotEmpty()){
            paymentMethods.clear()
        }
        for (paymentMethod in data){
            paymentMethods.add(paymentMethod)
        }
        notifyDataSetChanged()
    }

     fun setOnPaymentMethodClickListener(callback: IPaymentMethods) {
        this.mCallback = callback
    }

    interface IPaymentMethods {
        fun onPaymentMethodClick(paymentMethod: PaymentMethod)
    }


}