package com.jago.app.network


sealed class Output<out T : Any> {
    data class Success<out T : Any>(val output: T) : Output<T>()
    data class Error(val exception: Exception) : Output<Nothing>()
   // data class Error<out T : Any>(val noOutput: T?) : Output<T>()
   // data class Error<out T : Any>(val error: String) : Output<T>()
}