package com.ichota.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.ichota.R
import com.ichota.databinding.ActivityOnBoarding2Binding

class OnBoarding2 : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoarding2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoarding2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        binding.ivOn2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_slide_in))
        binding.fabContinue.setOnClickListener { moveToOnBoarding3() }
        binding.btSkip.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    private fun moveToOnBoarding3() {
        val pairs = arrayOf(
            Pair<View, String>(binding.ivOn2, "iv_onBoarding"),
            Pair<View, String>(binding.view1, "view_1"),
            Pair<View, String>(binding.view2, "view_2"),
            Pair<View, String>(binding.view3, "view_3"),
            Pair<View, String>(binding.tvOnBoardingDesp, "txt_desp"),
            Pair<View, String>(binding.fabContinue, "bt_continue")


        )

        val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            *pairs

        )
        Intent(this, OnBoarding3::class.java).also {
            startActivity(it, optionsCompat.toBundle())


        }
    }
}