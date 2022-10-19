package com.example.sunnyweather.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.RealtimeResponse
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.Dispatcher

object Repository {

    fun searchPlaces(query:String)= liveData(Dispatchers.IO){
        val result=try{
            val placeResponse=SunnyWeatherNetwork.searchPlaces(query)
            Log.d("Repository",placeResponse.toString())
            if(placeResponse.status=="ok"){
                val places=placeResponse.places
                Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }catch (e:Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }
//    fun refreshWeather(lng:String,lat:String)=liveData(Dispatchers.IO) {
//        val result=try{
//            Log.d("Repository","refreshWeather")
//            Log.d("Repository","get RealtimeWeatherResponse")
//            val RealtimeWeatherResponse=SunnyWeatherNetwork.getRealtimeWeather(lng,lat)
//            Log.d("Repository",RealtimeWeatherResponse.toString())
//            Log.d("Repository","get DailyWeatherResponse")
//            val DailyWeatherResponse=SunnyWeatherNetwork.getDailyWeather(lng,lat)
//            Log.d("Repository",DailyWeatherResponse.toString())
//
//            if(RealtimeWeatherResponse.status=="ok"&&DailyWeatherResponse.status=="ok"){
//                Log.d("Repository","both are ok")
//                val weather=Weather(RealtimeWeatherResponse.result.realtime,DailyWeatherResponse.result.daily)
//                Result.success(weather)
//            }else{
//                Result.failure(
//                    RuntimeException(
//                        "realtime response status is ${RealtimeWeatherResponse.status}+" +
//                                "daily response status is ${DailyWeatherResponse.status}")
//                )
//            }
//        }catch (e:Exception){
//            Result.failure<Weather>(e)
//        }
//        emit(result)
//    }

    fun refreshWeather(lng:String,lat:String)= liveData(Dispatchers.IO) {
        val result=try{
            coroutineScope {
                Log.d("Repository","调用refreshWeather")
                val deferredRealtime=async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng,lat)
                }
                val deferredDaily=async {
                    SunnyWeatherNetwork.getDailyWeather(lng,lat)
                }
                Log.d("Repository","调用完SunnyWeatherNetwork.getRealtimeWeather+" +
                        "SunnyWeatherNetwork.getDailyWeather")
                Log.d("Repository", deferredRealtime.toString())
                val realtimeResponse=deferredRealtime.await()
                val dailyResponse=deferredDaily.await()

                Log.d("Repository",realtimeResponse.toString())
                Log.d("Repository",dailyResponse.toString())
                if(realtimeResponse.status=="ok"&&dailyResponse.status=="ok"){
                    Log.d("Repository","both are ok")
                    val weather=Weather(realtimeResponse.result.realtime,dailyResponse.result.daily)
                    Result.success(weather)
                }else{
                    Result.failure(
                        RuntimeException(
                        "realtime response status is ${realtimeResponse.status}+" +
                                "daily response status is ${dailyResponse.status}")
                    )
                }
            }
        }catch (e:Exception){
            Result.failure<Weather>(e)
        }
        emit(result)
    }

    fun savePlace(place:Place)=PlaceDao.savePlace(place)

    fun getSavedPlace()=PlaceDao.getSavedPlace()

    fun isPlaceSaved()=PlaceDao.isPlaceSaved()


}




















