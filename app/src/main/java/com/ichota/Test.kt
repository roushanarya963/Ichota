package com.ichota

import android.util.Log
import java.util.*


fun main() {
    DisplayMessage()

}


class DisplayMessage() {
    private val firstMessage: String = "Ramesh".toUpperCase().also(::println)
    private val secondMessage: String = "Kumar".also(::println)


    init {
        println(firstMessage)

    }

    init {
        println(secondMessage)
    }
}
