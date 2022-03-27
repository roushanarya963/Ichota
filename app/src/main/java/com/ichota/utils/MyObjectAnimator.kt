package com.ichota.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View

object MyObjectAnimator {

    fun setFadeOut(viewToFadeOut: View) {

        val fadeOut = ObjectAnimator.ofFloat(viewToFadeOut, "alpha", 1f, 0f)
        fadeOut.duration = 500
        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                viewToFadeOut.visibility = View.GONE
            }
        })
        fadeOut.start()
    }


    fun setFadeIn(viewToFadeIn : View,duration : Long = 500){

       val  fadeIn = ObjectAnimator.ofFloat(viewToFadeIn,"alpha",0f,1f)
       fadeIn.duration = duration
       fadeIn.addListener(object :AnimatorListenerAdapter(){
           override fun onAnimationEnd(animation: Animator?) {
               viewToFadeIn.visibility = View.VISIBLE
           }
       })
        fadeIn.start()
    }



}