package com.roshan.jha.attendance.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    private var retrofit: Retrofit? = null

    private val BaseUrl = "https://x8ki-letl-twmt.n7.xano.io/api:BS3FIkjh/"

    companion object {
        private var instance: RetrofitClient? = null

        fun getInstance(): RetrofitClient? {
            if (instance == null) {
                instance = RetrofitClient()
            }
            return instance
        }
    }

    @Synchronized
    fun getRetrofitInstance(): Retrofit? {
        if (retrofit == null) {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setLenient()
            val gson: Gson = gsonBuilder.create()
            retrofit = Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit
    }
}