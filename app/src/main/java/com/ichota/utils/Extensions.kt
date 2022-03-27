package com.ichota.utils

import android.view.View
import androidx.constraintlayout.widget.Group

object Extensions {

    fun Group.addOnClickListener(listener:(view: View) -> Unit){
        referencedIds.forEach { id->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }
}