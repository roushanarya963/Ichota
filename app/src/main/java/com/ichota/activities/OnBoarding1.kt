package com.ichota.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.ichota.databinding.ActivityOnBoarding1Binding

class OnBoarding1 : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoarding1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoarding1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }



    private fun init() {

        binding.fabContinue.setOnClickListener { moveToOnBoarding2() }
        binding.btSkip.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }



    private fun moveToOnBoarding2() {
        Intent(this, OnBoarding2::class.java).also {

            val pairs = arrayOf(
                Pair<View, String>(binding.ivOn1, "iv_onBoarding"),
                Pair<View, String>(binding.view1, "view_1"),
                Pair<View, String>(binding.view2, "view_2"),
                Pair<View, String>(binding.view3, "view_3"),
                Pair<View, String>(binding.tvOnBoardingDesp, "txt_desp"),



            )
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                *pairs
            )
            startActivity(it, optionsCompat.toBundle())


        }
    }
}