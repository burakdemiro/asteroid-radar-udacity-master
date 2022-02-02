package com.udacity.asteroidradar.api

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod?&api_key=${API_KEY}")
    suspend fun loadImageOfTheDay(): NetworkPictureOfDay

    @GET("neo/rest/v1/feed?&api_key=${API_KEY}")
    suspend fun loadAsteroids(@Query("start_date") startDate: String): ResponseBody
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val client =  OkHttpClient.Builder()
    .addNetworkInterceptor(StethoInterceptor()) // For debugging purposes
    .build()

object Network {
    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

    val nasaApiService: NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}