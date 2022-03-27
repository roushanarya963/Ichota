package com.ichota.network

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object APIFactory {

    private const val API_TIMEOUT: Long = 30
    private const val BASE_URL = "https://ichotaa.appdeft.biz/Api/"
    const val BASE_URL_IMAGE = "https://ichotaa.appdeft.biz/assets/upload/"
    const val URL_PRIVACY_POLICY="https://ichotaa.appdeft.biz/privacy_policy.php"
    const val URL_TERM_AND_CONDITIONS="https://ichotaa.appdeft.biz/terms.php"

    fun makeServiceAPi(): RetrofitService {
        return makeRetrofit(
            makeOkHttpClient(makeLoggingInterceptor()),
            makeGson()
        ).create(RetrofitService::class.java)
    }

    private fun makeOkHttpClient(
            httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                //.addInterceptor(headerInterceptor())
                .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
    }

   private fun headerInterceptor(): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request = chain.request().newBuilder().addHeader("Connection", "close").build()
                return chain.proceed(request)
            }

        }
    }


    private fun makeRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
    }

    private fun makeGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()
    }

    private fun makeLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor(httpLogger)
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    private val httpLogger: HttpLoggingInterceptor.Logger by lazy {
        object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.println(
                    Log.VERBOSE,
                    "WEB SERVICE",
                    "RESPONSE VALUE $message"
                )
            }
        }
    }

}