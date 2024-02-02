package com.linglingdr00.weather.ui.location

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

    private var city: String? = null
    private var town: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        //新增 location listener
        myLocationListener = MyLocationListener(requireContext()) { city, town ->
            //收到 location callback
            Log.d(TAG, "city: $city, town: $town")
            //將 location 傳給 view model
            locationViewModel.receiveLocation(city, town)
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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
        //設定 toolbar title
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_location)

        //如果已有權限
        if (GlobalVariable.havePermission) {
            //如果還沒得到 location
            if (!(GlobalVariable.haveLocation)) {
                //設定 LocationState LOADING
                locationViewModel.setLocationState(1)
                myLocationListener.startGetLocation()
            }
        } else {
            //設定 LocationState NO_PERMISSION
            locationViewModel.setLocationState(0)
        }

        locationViewModel.location.observe(viewLifecycleOwner) {
            //將得到的 location 資訊存起來
            city = it[0]
            town = it[1]
        }

        locationViewModel.status.observe(viewLifecycleOwner) {
            if (it == LocationViewModel.LocationStatus.DONE) {

                val nowItem = nowViewModel.getMyTownData(town!!)
                locationViewModel.receiveNowItem(nowItem)

                val forecastItem = forecastViewModel.getMyCityData(city!!)
                locationViewModel.receiveForecastItem(forecastItem)

                //顯示 liner layout
                binding?.linerLayout?.visibility = View.VISIBLE
                //設定 menu
                setHasOptionsMenu(true)
            }
        }


    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        Log.d(TAG, "onCreateOptionsMenu()")
        menu.clear()
        inflater.inflate(R.menu.toolbar_menu, menu)

        val myLocationTextView = menu.findItem(R.id.location_textView)
        val locationTextView = myLocationTextView?.actionView as TextView

        val locationText = "$city $town"
        locationTextView.text = locationText

        locationTextView.setBackgroundResource(R.drawable.item_bg)
        locationTextView.setTextAppearance(androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Menu)

        myLocationTextView.isVisible = true
    }

}