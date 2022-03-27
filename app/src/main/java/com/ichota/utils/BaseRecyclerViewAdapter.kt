package com.ichota.utils

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import com.ichota.interfaces.OnItemClickListener

abstract class BaseRecyclerViewAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list: ArrayList<T> = ArrayList()
    protected var itemClickListener: OnItemClickListener? = null

    fun addItems(items: ArrayList<T>) {
        clear()
        this.list.addAll(items)
        reload()
    }

    fun addItem(item: T) {
        this.list.add(item)
        notifyItemInserted(list.size - 1)
    }


    fun clear() {
        this.list.clear()
        reload()
    }

    fun getItem(position: Int): T? {
        return this.list[position]
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int {
        return list.size

    }

    private fun reload() {
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }
}