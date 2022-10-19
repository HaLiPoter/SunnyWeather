package com.example.sunnyweather.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProviders.of(this).get(WeatherViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView=window.decorView
        decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor=Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
        val swipeRefresh:SwipeRefreshLayout=findViewById(R.id.swipeRefresh)
        val s1=intent.getStringExtra("location_lng")?:""
        val s2=intent.getStringExtra("location_lat")?:""
        val s3=intent.getStringExtra("place_name")?:""
        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng=s1
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat=s2
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName=s3
        }
        Log.d("WeatherActivity",s1)
        Log.d("WeatherActivity",s2)
        Log.d("WeatherActivity",s3)
        viewModel.weatherLiveData.observe(this, Observer {
            result->
            val weather=result.getOrNull()
            Log.d("WeatherActivity","observe")
            Log.d("WeatherActivity",weather.toString())
            if(weather!=null){
                showWeatherInfo(weather)
            }else{
                Log.d("WeatherActivity","body is null")
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing=false
        })
        swipeRefresh.setColorSchemeResources(com.google.android.material.R.color.cardview_shadow_start_color)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        val drawerLayout:DrawerLayout=findViewById(R.id.drawerLayout)
        val navBtn:Button=findViewById(R.id.navBtn)
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object :DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val manager=getSystemService(Context.INPUT_METHOD_SERVICE)as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
    }
    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        val swipeRefresh:SwipeRefreshLayout=findViewById(R.id.swipeRefresh)
        swipeRefresh.isRefreshing=true
    }
    private fun showWeatherInfo(weather:Weather){
        Log.d("WeatherActivity","showWeatherInfo")
        val placeName:TextView=findViewById(R.id.placeName)
        val currentTemp:TextView=findViewById(R.id.currentTemp)
        val currentSky:TextView=findViewById(R.id.currentSky)
        val currentAQI:TextView=findViewById(R.id.currentAQI)
        placeName.text=viewModel.placeName
        val realtime=weather.realtime
        val daily=weather.daily
        Log.d("WeatherActivity","布局初始化完毕1")
        val currentTempText="${realtime.temperature.toInt()}°C"
        currentTemp.text=currentTempText
        //getSky(realtime.skycon)
        Log.d("WeatherActivity",realtime.skycon)
        Log.d("WeatherActivity","布局初始化完毕2")
        currentSky.text= getSky(realtime.skycon).info
        Log.d("WeatherActivity","布局初始化完毕3")
        val currentPM25Text= "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text=currentPM25Text
        val nowLayout: RelativeLayout =findViewById(R.id.nowLayout)
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        val forecastLayout:LinearLayout=findViewById(R.id.forecastLayout)
        forecastLayout.removeAllViews()
        val days=daily.skycon.size
        Log.d("WeatherActivity","布局初始化完毕")
        for(i in 0 until days){
            val skycon=daily.skycon[i]
            val temperature=daily.temperature[i]
            val view=LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
            val dateInfo=view.findViewById(R.id.dataInfo) as TextView
            val skyIcon=view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo=view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo=view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat=SimpleDateFormat("yyy-MM-dd",Locale.getDefault())
            dateInfo.text=simpleDateFormat.format(skycon.date)
            val sky= getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text=sky.info
            val tempText="${temperature.min.toInt()}~${temperature.max.toInt()}°C"
            temperatureInfo.text=tempText
            forecastLayout.addView(view)
        }
        val lifeIndex=daily.lifeIndex
        val coldRiskText:TextView=findViewById(R.id.coldRiskText)
        val dressingText:TextView=findViewById(R.id.dressingText)
        val ultravioletText:TextView=findViewById(R.id.ultravioletText)
        val carWashingText:TextView=findViewById(R.id.carWashingText)
        coldRiskText.text=lifeIndex.coldRisk[0].desc
        dressingText.text=lifeIndex.dressing[0].desc
        ultravioletText.text=lifeIndex.ultraviolet[0].desc
        carWashingText.text=lifeIndex.carWashing[0].desc
        val weatherLayout:ScrollView=findViewById(R.id.weatherLayout)
        weatherLayout.visibility=View.VISIBLE
    }
}



























