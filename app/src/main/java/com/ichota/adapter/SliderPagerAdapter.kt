package com.ichota.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.ichota.R
import com.ichota.utils.Global

class SliderPagerAdapter : PagerAdapter() {

    private val productImages = ArrayList<String>()


    override fun getCount(): Int {
        return productImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageLayout = LayoutInflater.from(container.context)
            .inflate(R.layout.adapter_slider_img, container, false)
        val ivSlider = imageLayout.findViewById<ImageView>(R.id.iv_product)
        Glide.with(container.context)
            .load(Global.getImageUrl(productImages[position]))
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.app_logo)
            .into(ivSlider)
        container.addView(imageLayout, 0)
        return imageLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


    fun setData(data: ArrayList<String>?) {
        if (data == null) return
        productImages.clear()
        for (image in data) {
            productImages.add(image)
        }
        notifyDataSetChanged()
    }
}