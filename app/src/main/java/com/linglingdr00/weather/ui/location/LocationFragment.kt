package com.linglingdr00.weather.ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.linglingdr00.weather.GlobalVariable
import com.linglingdr00.weather.ItemDecoration
import com.linglingdr00.weather.MyLocationListener
import com.linglingdr00.weather.R
import com.linglingdr00.weather.databinding.FragmentLocationBinding
import com.linglingdr00.weather.ui.forecast.ForecastViewModel
import com.linglingdr00.weather.ui.now.NowViewModel


class LocationFragment : Fragment() {

    private val TAG = "LocationFragment"
    private val locationViewModel: LocationViewModel by viewModels()
    private val forecastViewModel: ForecastViewModel by activityViewModels()
    private val nowViewModel: NowViewModel by activityViewModels()

    private var binding: FragmentLocationBinding? = null
    private lateinit var myLocationListener: MyLocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        //新增 location listener
        myLocationListener = MyLocationListener(requireContext()) { city, town ->
            Log.d(TAG, "city: $city, town: $town")
            GlobalVariable.haveLocation = true
            locationViewModel.receiveLocation(city, town)
            forecastViewModel.getForecastData(city)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentLocationBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated()")

        binding?.lifecycleOwner = this
        binding?.viewModel = locationViewModel

        // 設 adapter 為 LocationNowAdapter
        binding?.locationNowRecyclerView?.adapter = LocationNowAdapter()
        // 設定 ItemDecoration 調整 item 邊距
        binding?.locationNowRecyclerView?.addItemDecoration(ItemDecoration())

        // 設 adapter 為 LocationForecastAdapter
        binding?.locationForecastRecyclerView?.adapter = LocationForecastAdapter()
        // 設定 ItemDecoration 調整 item 邊距
        binding?.locationForecastRecyclerView?.addItemDecoration(ItemDecoration())
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
        //設定 toolbar title
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_location)
        // 設定 menu
        //setHasOptionsMenu(true)

        //如果已有權限且還沒得到位置
        if (GlobalVariable.havePermission && !(GlobalVariable.haveLocation)) {
            myLocationListener.startGetLocation()
        }

        locationViewModel.location.observe(viewLifecycleOwner) {
            val city = it[0]
            val town = it[1]

            val nowItem = nowViewModel.getMyTownData(town)
            locationViewModel.receiveNowItem(nowItem)

            val forecastItem = forecastViewModel.getMyCityData(city)
            locationViewModel.receiveForecastItem(forecastItem)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}