package com.example.sunnyweather.ui.place

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.ui.weather.WeatherActivity

class PlaceFragment: Fragment() {

    val viewModel by lazy{ViewModelProviders.of(this).get(PlaceViewModel::class.java)}//创建viewmodel的方式
    private lateinit var mview:View//返回的view必须是同一个
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("PlaceFragment","onCreateView")
        mview=inflater.inflate(R.layout.fragment_place,container,false)
        return mview
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (viewModel.isPlaceSaved()){
            val place=viewModel.getSavedPlace()
            val intent=Intent(context,WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        Log.d("PlaceFragment","onActivityCreated1")
        val layoutManger=LinearLayoutManager(activity)
        val recyclerView:RecyclerView=mview.findViewById(R.id.recyclerView)
        recyclerView.layoutManager=layoutManger
        adapter=PlaceAdapter(this,viewModel.placeList)
        recyclerView.adapter=adapter
        val searchPlaceEdit:EditText=mview.findViewById(R.id.searchPlaceEdit)
        Log.d("PlaceFragment","onActivityCreated2")
        searchPlaceEdit.addTextChangedListener { editable->
            val content=editable.toString()
            Log.d("PlaceFragment","addTextChangedListener")
            if(content.isNotEmpty()){
                viewModel.searchPlaces(content)//
            }else{
                recyclerView.visibility=View.GONE
                val bgImageView:ImageView=mview.findViewById(R.id.bgImageView)
                bgImageView.visibility=View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result->
            val places=result.getOrNull()
            if(places!=null){
                recyclerView.visibility=View.VISIBLE
                val bgImageView:ImageView=mview.findViewById(R.id.bgImageView)
                bgImageView.visibility=View.VISIBLE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                Log.d("placeFragment","这里开始")
                for(p in viewModel.placeList){
                    Log.d("placeFragment","地点"+p.name)
                }
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(activity,"未能查询到任何地点",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}




















