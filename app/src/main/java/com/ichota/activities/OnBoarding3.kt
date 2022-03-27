package com.ichota.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.ichota.R
import com.ichota.auth.LoginActivity
import com.ichota.databinding.ActivityOnBoarding3Binding

class OnBoarding3 : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoarding3Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoarding3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.ivOn3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_slide_in))
        binding.btLogin.setOnClickListener { moveToLogin() }

    }

    private fun moveToLogin() {
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }
}