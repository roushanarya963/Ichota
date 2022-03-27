package com.ichota.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ichota.databinding.DialogAdapterCategoryBinding
import com.ichota.model.SaleCategory
import com.ichota.model.ServiceCategory

class CategoryDialogAdapter : RecyclerView.Adapter<CategoryDialogAdapter.CategoryViewHolder>() {
    private val categories = ArrayList<Any>()
    private var mCallback: CategoryClickInterface? = null

    class CategoryViewHolder(val binding: DialogAdapterCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DialogAdapterCategoryBinding.inflate(inflater, parent, false)
        return CategoryViewHolder((binding))

    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        when(val category = categories[position]){
            is SaleCategory ->{
                holder.binding.tvCategory.text = category.name
                holder.binding.tvCategory.setOnClickListener { mCallback?.onCategoryClick(category) }
            }
            is ServiceCategory ->{
                holder.binding.tvCategory.text = category.name
                holder.binding.tvCategory.setOnClickListener { mCallback?.onCategoryClick(category) }
            }
        }


    }

    override fun getItemCount(): Int {
        Log.d("TAG", "getItemCount:${categories.size} ")
        return categories.size
    }

    fun setData(data: ArrayList<Any>) {
        categories.clear()
        for (category in data) {
            categories.add(category)
            notifyItemInserted(categories.size - 1)
        }
    }

    fun onCategoryClickListener(callback: CategoryClickInterface) {
        this.mCallback = callback
    }

    interface CategoryClickInterface {
        fun onCategoryClick(catId: Any)
    }
}