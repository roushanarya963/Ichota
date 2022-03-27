package com.ichota.repositories

import com.jago.app.network.Output
import retrofit2.Response
import java.io.IOException

open class BaseRepository {


    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, error: String): T? {

        val result = apiOutput(call, error)

        var output: T? = null

        when (result) {
            is Output.Success ->
                output = result.output


            is Output.Error -> result.exception

            /* Log.e("Error", "The $error and The ${result.exception}: ")*/

        }
        return output


    }


    private suspend fun <T : Any> apiOutput(
        call: suspend () -> Response<T>,
        error: String
    ): Output<T> {
        val response = call.invoke()
        return if (response.isSuccessful)
            Output.Success(response.body()!!)
        else
            Output.Error(IOException("OOps Something went wrong due to $error"))
        // Output.Error(response.body())

    }
}