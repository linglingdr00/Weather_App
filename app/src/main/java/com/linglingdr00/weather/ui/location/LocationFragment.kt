package com.linglingdr00.weather.ui.location

import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.linglingdr00.weather.CurrentLocationListener
import com.linglingdr00.weather.ItemDecoration
import com.linglingdr00.weather.R
import com.linglingdr00.weather.databinding.FragmentLocationBinding
import com.linglingdr00.weather.ui.forecast.ForecastItem
import com.linglingdr00.weather.ui.forecast.ForecastViewModel
import com.linglingdr00.weather.ui.now.NowItem
import com.linglingdr00.weather.ui.now.NowViewModel


class LocationFragment : Fragment() {

    private val TAG = "LocationFragment"
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val forecastViewModel: ForecastViewModel by activityViewModels()
    private val nowViewModel: NowViewModel by activityViewModels()

    private var binding: FragmentLocationBinding? = null
    private lateinit var currentLocationListener: CurrentLocationListener

    private var currentAddress: List<Address>? = null
    private var currentLocation: Location? = null

    private var nowItem: NowItem? = null
    private var forecastItem: ForecastItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        //新增 location listener
        currentLocationListener = CurrentLocationListener(requireContext()) { location, address ->

            //收到 location callback
            Log.d(TAG, "get location callback: location: ${location?.latitude} ${location?.longitude}, address: ${address?.get(0)?.getAddressLine(0)}")
            //將 location 傳給 view model
            locationViewModel.receiveLocation(location, address)
        }

        //新增 gms location listener
        currentLocationListener.createLocationRequest()

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
        binding?.locationRecyclerView?.adapter = LocationAdapter()
        // 設定 ItemDecoration 調整 item 邊距
        binding?.locationRecyclerView?.addItemDecoration(ItemDecoration())
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
        // 設定 toolbar title
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_location)

        locationViewModel.permissionStatus.observe(viewLifecycleOwner) { permission ->
            when (permission) {
                true -> {
                    // 檢查 GPS provider 和 network provider
                    if (checkGPSAndNetworkStatus()) {
                        // 設定 location status LOCATION_LOADING
                        locationViewModel.setLocationState(LocationViewModel.LocationStatus.LOCATION_LOADING)
                        currentLocationListener.startGetLocation()
                        //currentLocationListener.getLastLocation()
                    }
                }
                false -> {
                    // 設定 location status LOCATION_ERROR
                    locationViewModel.setLocationState(LocationViewModel.LocationStatus.LOCATION_ERROR)
                    // 設定 error message
                    locationViewModel.setErrorMessage(R.string.error_message_1_no_permission)
                    Log.d(TAG, "errorMessage: 因沒有位置權限，無法定位您的所在位置")
                }
            }
        }

        locationViewModel.location.observe(viewLifecycleOwner) { location ->
            // 將得到的 locatin 存起來
            currentLocation = location
            Log.d(TAG, "locatin: ${currentLocation?.latitude} ${currentLocation?.longitude}")
        }

        locationViewModel.address.observe(viewLifecycleOwner) { address ->
            // 將得到的 address 存起來
            currentAddress = address
            Log.d(TAG, "address: ${address?.get(0)?.getAddressLine(0)}")
        }

        locationViewModel.locationStatus.observe(viewLifecycleOwner) {
            when (it) {
                LocationViewModel.LocationStatus.LOCATION_LOADING -> {
                    Log.d(TAG, "LocationStatus 1: LOCATION_LOADING")
                    // 不顯示 error message
                    binding?.errorMessageTextView?.visibility = View.GONE
                }
                LocationViewModel.LocationStatus.LOCATION_DONE -> {
                    Log.d(TAG, "LocationStatus 2: LOCATION_DONE")
                    // 不顯示 error message
                    binding?.errorMessageTextView?.visibility = View.GONE
                    // 設定 menu
                    setHasOptionsMenu(true)
                    // 當得到 location 資料後，開始抓取 item data
                    locationViewModel.setLocationState(LocationViewModel.LocationStatus.DATA_LOADING)
                }
                LocationViewModel.LocationStatus.LOCATION_ERROR -> {
                    Log.d(TAG, "LocationStatus 3: LOCATION_ERROR")
                    // 顯示 error message
                    binding?.errorMessageTextView?.visibility = View.VISIBLE
                }
                LocationViewModel.LocationStatus.DATA_LOADING -> {
                    Log.d(TAG, "LocationStatus 4: DATA_LOADING")
                    // 不顯示 error message
                    binding?.errorMessageTextView?.visibility = View.GONE

                    nowViewModel.status.observe(viewLifecycleOwner) {

                        if (it == NowViewModel.NowWeatherApiStatus.DONE) {
                            try {
                                val nowItemList = nowViewModel.getMyTownData(currentLocation!!)
                                nowItem = nowItemList[0]
                                locationViewModel.setNowItemStatus(true)
                                // 得到 now item 後，設 LocationStatus 為 DATA_DONE
                                //locationViewModel.setLocationState(LocationViewModel.LocationStatus.DATA_DONE)
                            } catch (e: Exception) {
                                Log.d(TAG, "now item error: ${e.message}")
                                // 當 now item 發生錯誤時，設 LocationStatus 為 DATA_ERROR
                                locationViewModel.setLocationState(LocationViewModel.LocationStatus.DATA_ERROR)
                                //設定 error message
                                locationViewModel.setErrorMessage(R.string.error_message_5_data)
                            }
                        }
                    }

                    forecastViewModel.status.observe(viewLifecycleOwner) {
                        val city = currentAddress?.get(0)?.adminArea
                        //Log.d(TAG, "city: $city")

                        if (it == ForecastViewModel.ForecastWeatherApiStatus.DONE) {
                            try {
                                val forecastItemList = forecastViewModel.getMyCityData(city!!)
                                forecastItem = forecastItemList[0]
                                locationViewModel.setForecastItemStatus(true)
                                // 得到 forecast item 後，設 LocationStatus 為 DATA_DONE
                                //locationViewModel.setLocationState(LocationViewModel.LocationStatus.DATA_DONE)

                            } catch (e: Exception) {
                                Log.d(TAG, "forecast item error: ${e.message}")
                                // 當 forecast item 發生錯誤時，設 LocationStatus 為 DATA_ERROR
                                locationViewModel.setLocationState(LocationViewModel.LocationStatus.DATA_ERROR)
                                //設定 error message
                                locationViewModel.setErrorMessage(R.string.error_message_5_data)
                            }
                        }
                    }

                    // 接收到 nowItem 和 forecastItem 後，轉成 locationItem
                    locationViewModel.forecastItemStatus.observe(viewLifecycleOwner) { forecastItemStatus ->
                        locationViewModel.nowItemStatus.observe(viewLifecycleOwner) { nowItemStatus ->
                            if (forecastItemStatus && nowItemStatus) {
                                locationViewModel.tranToLocationItem(nowItem!!, forecastItem!!)
                                // 設 LocationStatus 為 DATA_DONE
                                locationViewModel.setLocationState(LocationViewModel.LocationStatus.DATA_DONE)
                            }
                        }
                    }

                }
                LocationViewModel.LocationStatus.DATA_DONE -> {
                    Log.d(TAG, "LocationStatus 5: DATA_DONE")
                    // 不顯示 error message
                    binding?.errorMessageTextView?.visibility = View.GONE
                }
                LocationViewModel.LocationStatus.DATA_ERROR -> {
                    Log.d(TAG, "LocationStatus 6: DATA_ERROR")
                    // 顯示 error message
                    binding?.errorMessageTextView?.visibility = View.VISIBLE
                }

            }
        }
    }

    // 設定 menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        Log.d(TAG, "onCreateOptionsMenu()")
        menu.clear()
        inflater.inflate(R.menu.toolbar_menu, menu)

        // 設定 toolbar 的 text view
        val myLocationTextView = menu.findItem(R.id.location_textView)
        val locationTextView = myLocationTextView?.actionView as TextView

        val city = currentAddress?.get(0)?.adminArea
        val town = currentAddress?.get(0)?.subAdminArea
        val locationText = "${city} ${town}"

        locationTextView.text = locationText

        locationTextView.setBackgroundResource(R.drawable.item_bg)
        locationTextView.setTextAppearance(androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Menu)

        myLocationTextView.isVisible = true
    }

    private fun checkGPSAndNetworkStatus(): Boolean {
        // 建立 location manager instance
        val locationManager: LocationManager = context?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        // 確認 GPS provider 狀態，並傳給 view model
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        // 確認 Network provider 狀態，並傳給 view model
        val isNetworklEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        // 如果 2 種 provider 都有，就 return true
        if (isGPSEnabled && isNetworklEnabled && isConnected()) {
            return true
        } else {
            // 設定 location status 為 error
            locationViewModel.setLocationState(LocationViewModel.LocationStatus.LOCATION_ERROR)
            // 設定 error message
            locationViewModel.setErrorMessage(R.string.error_message_4_gps_and_network)
            Log.d(TAG, "errorMessage: 您的手機未開啟 GPS 定位或網路，請至設定開啟")
            return  false
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager: ConnectivityManager? = context?.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val networkInfo = connectivityManager!!.activeNetworkInfo
        // 如果 networkInfo != null 會 return true，反之 return false
        return networkInfo != null
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        binding = null
    }

}