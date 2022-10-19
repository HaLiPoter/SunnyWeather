package com.example.sunnyweather.logic.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {
    private val weatherService=ServiceCreator.create(WeatherService::class.java)
    suspend fun getDailyWeather(lng:String,lat:String)= weatherService.getDailyWeather(lng,lat).await()
    suspend fun getRealtimeWeather(lng:String,lat:String)= weatherService.getRealtimeWeather(lng,lat).await()

    private val placeService=ServiceCreator.create(PlaceService::class.java)
    suspend fun searchPlaces(query:String)= placeService.searchPlaces(query).await()


    private suspend fun <T> Call<T>.await():T{
        Log.d("SunnyWeatherNetwork","Call<T>.await()")
        return suspendCoroutine { continuation ->
            Log.d("SunnyWeatherNetwork","suspendCoroutine")
            enqueue(object :Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    Log.d("SunnyWeatherNetwork","onResponse")
                    val body=response.body()
                    if(body!=null)continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.d("SunnyWeatherNetwork","onFailure")
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}