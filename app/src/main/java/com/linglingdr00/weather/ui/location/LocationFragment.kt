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
import com.linglingdr00.weather.ui.forecast.ForecastViewModel
import com.linglingdr00.weather.ui.now.NowViewModel


class LocationFragment : Fragment() {

    private val TAG = "LocationFragment"
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val forecastViewModel: ForecastViewModel by activityViewModels()
    private val nowViewModel: NowViewModel by activityViewModels()

    private var binding: FragmentLocationBinding? = null
    private lateinit var currentLocationListener: CurrentLocationListener

    private var currentAddress: List<Address>? = null
    private var currentLocatin: Location? = null

    private var nowDataStatus: Boolean? = false
    private var forecastDataStatus: Boolean? = false

    private var locationText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        //新增 location listener
        currentLocationListener = CurrentLocationListener(requireContext()) { location, address ->
            //收到 location callback
            Log.d(TAG, "location: $location, address: $address")
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
        binding?.locationNowRecyclerView?.adapter = LocationNowAdapter()
        // 設定 ItemDecoration 調整 item 邊距
        binding?.locationNowRecyclerView?.addItemDecoration(ItemDecoration())

        // 設 adapter 為 LocationForecastAdapter
        binding?.locationForecastRecyclerView?.adapter = LocationForecastAdapter()
        // 設定 ItemDecoration 調整 item 邊距
        binding?.locationForecastRecyclerView?.addItemDecoration(ItemDecoration())
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
                        //currentLocationListener.startGetLocation()
                        currentLocationListener.startToLocationUpdates()
                    }
                }
                false -> {
                    // 設定 location status error
                    locationViewModel.setLocationState(LocationViewModel.LocationStatus.LOCATION_ERROR)
                    // 設定 error message
                    locationViewModel.setErrorMessage(R.string.error_message_1_no_permission)
                    Log.d(TAG, "errorMessage: 因沒有位置權限，無法定位您的所在位置")
                }
            }
        }

        locationViewModel.location.observe(viewLifecycleOwner) { location ->
            // 將得到的 locatin 存起來
            currentLocatin = location
            Log.d(TAG, "locatin: $currentLocatin")
        }

        locationViewModel.address.observe(viewLifecycleOwner) { address ->
            // 將得到的 address 存起來
            currentAddress = address
            Log.d(TAG, "address: $address")
        }

        locationViewModel.locationStatus.observe(viewLifecycleOwner) {
            when (it) {
                LocationViewModel.LocationStatus.LOCATION_LOADING -> {
                    Log.d(TAG, "LocationStatus: LOCATION_LOADING")
                    binding?.errorMessageTextView?.visibility = View.GONE
                }
                LocationViewModel.LocationStatus.LOCATION_DONE -> {
                    Log.d(TAG, "LocationStatus: LOCATION_DONE")
                    binding?.errorMessageTextView?.visibility = View.GONE
                    // 當得到 location 資料後，開始抓取 item data
                    locationViewModel.setDataStatus(LocationViewModel.DataStatus.DATA_LOADING)

                    nowViewModel.status.observe(viewLifecycleOwner) {
                        val city = currentAddress?.get(0)?.adminArea
                        Log.d(TAG, "locatin: $currentLocatin, city: $city")

                        if (it == NowViewModel.NowWeatherApiStatus.DONE) {
                            // 當 now item 的資料抓完後，設 now data status 為 true
                            nowDataStatus = true
                            val nowItem = nowViewModel.getMyTownData(currentLocatin!!)
                            locationViewModel.receiveNowItem(nowItem)
                        } else {
                            nowDataStatus = false
                        }
                    }

                    forecastViewModel.status.observe(viewLifecycleOwner) {
                        val city = currentAddress?.get(0)?.adminArea
                        Log.d(TAG, "locatin: $currentLocatin, city: $city")

                        if (it==ForecastViewModel.ForecastWeatherApiStatus.DONE) {
                            // 當 forecast item 的資料抓完後，設 forecast data status 為 true
                            forecastDataStatus = true
                            val forecastItem = forecastViewModel.getMyCityData(city!!)
                            locationViewModel.receiveForecastItem(forecastItem)
                        } else {
                            forecastDataStatus = false
                        }
                    }

                    if (nowDataStatus == true && forecastDataStatus == true) {
                        locationViewModel.setDataStatus(LocationViewModel.DataStatus.DATA_DONE)
                    }
                }
                LocationViewModel.LocationStatus.LOCATION_ERROR -> {
                    Log.d(TAG, "LocationStatus: LOCATION_ERROR")

                    locationViewModel.errorMessage.observe(viewLifecycleOwner) {
                        binding?.errorMessageTextView?.setText(it)
                        binding?.errorMessageTextView?.visibility = View.VISIBLE
                    }

                }

            }
        }

        locationViewModel.dataStatus.observe(viewLifecycleOwner) {
            when (it) {
                LocationViewModel.DataStatus.DATA_DONE -> {
                    // 顯示 liner layout
                    binding?.linerLayout?.visibility = View.VISIBLE
                    // 設定 menu
                    setHasOptionsMenu(true)
                } else -> {
                    // 不顯示 liner layout
                    binding?.linerLayout?.visibility = View.GONE
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

        return networkInfo != null && networkInfo.isConnected()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        binding = null
    }

}