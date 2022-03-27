package com.ichota.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ichota.adapter.ImgSlideShowAdapter
import com.ichota.databinding.ActivityFullImageBinding


private const val TAG = "FullImageActivity"

class FullImageActivity : AppCompatActivity(), ImgSlideShowAdapter.ImageClickInterface {
    private lateinit var binding: ActivityFullImageBinding
    private var imgSlideShowAdapter: ImgSlideShowAdapter? = null
    private var mSelectedPosition : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
        setupListener()
    }



    private fun initView() {



        val images = try {
            intent.getStringArrayListExtra("images")
        }catch (e:Exception){
            ArrayList()
        }
       mSelectedPosition =  intent.getIntExtra("position",0)

        imgSlideShowAdapter = ImgSlideShowAdapter(images as ArrayList<String>,this,true)
        binding.pagerImgSlideShow.adapter = imgSlideShowAdapter
        binding.pagerImgSlideShow.currentItem = mSelectedPosition

    }

    private fun setupListener() {
        binding.fabBack.setOnClickListener {
            finish()
        }

    }

    override fun onImageClick(position: Int) {

    }


}