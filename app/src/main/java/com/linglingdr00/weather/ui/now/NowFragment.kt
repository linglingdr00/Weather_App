package com.linglingdr00.weather.ui.now

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.linglingdr00.weather.ItemDecoration
import com.linglingdr00.weather.R
import com.linglingdr00.weather.databinding.FragmentNowBinding
import kotlinx.coroutines.launch

class NowFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val TAG = "NowFragment"
    private val nowViewModel: NowViewModel by activityViewModels()
    private var binding: FragmentNowBinding? = null
    // 設定第二層 spinner
    private var myCitySpinner: MenuItem? = null
    private var citySpinner: Spinner? = null
    private var cityList = R.array.northern_array
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 每次進入 ForecastFragment 時都 load data
        nowViewModel.viewModelScope.launch {
            try {
                //載入現在天氣資料
                nowViewModel.getNowData()
                Log.d(TAG, "載入現在天氣資料成功")
            } catch (e: Exception) {
                Log.d(TAG, "載入現在天氣資料失敗")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentNowBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 設定 lifecycleOwner
        binding?.lifecycleOwner = this
        // 設定 view model 為 NowViewModel
        binding?.viewModel = nowViewModel
        // 設 adapter 為 NowAdapter
        binding?.nowRecyclerView?.adapter = NowAdapter()
        // 設定 ItemDecoration 調整 item 邊距
        binding?.nowRecyclerView?.addItemDecoration(ItemDecoration())
    }

    //新增 toolbar 的下拉式選單(spinner) menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        Log.d(TAG, "onCreateOptionsMenu()")
        menu.clear()
        inflater.inflate(R.menu.toolbar_menu, menu)

        val myAreaSpinner = menu.findItem(R.id.areaSpinner)
        val areaSpinner = myAreaSpinner?.actionView as Spinner

        myCitySpinner = menu.findItem(R.id.citySpinner)
        citySpinner = myCitySpinner?.actionView as Spinner

        // 設定顯示 citySpinner
        myCitySpinner?.isVisible = true

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.area_array, // 選單中的 item
            android.R.layout.simple_spinner_item,
        ).also { adapter ->
            // 設定 dropdown 樣式
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 設定 spinner 的 adapter
            areaSpinner.adapter = adapter
        }

        areaSpinner.onItemSelectedListener = this@NowFragment

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.id == R.id.areaSpinner) {
            cityList = when (position) {
                0 -> R.array.northern_array
                1 -> R.array.central_array
                2 -> R.array.southern_array
                3 -> R.array.eastern_array
                else -> R.array.outlying_array
            }

            ArrayAdapter.createFromResource(
                requireContext(),
                cityList!!, // 選單中的 item
                android.R.layout.simple_spinner_item,
            ).also { adapter ->
                // 設定 dropdown 樣式
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // 設定 spinner 的 adapter
                citySpinner!!.adapter = adapter
            }

            citySpinner!!.onItemSelectedListener = this@NowFragment

        } else {
            val selectedCityList = resources.getStringArray(cityList).toList() as MutableList<String>
            val city = selectedCityList[position]
            Log.d(TAG, "position: $position, city: $city")

            // 當資料處理完成時
            nowViewModel.status.observe(viewLifecycleOwner) {
                if (it == NowViewModel.NowWeatherApiStatus.DONE) {
                    // 設定顯示 city 資料
                    nowViewModel.setCityData(city)
                    Log.d(TAG, "setCityData()")
                }
            }
        }

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d(TAG, "onNothingSelected")
    }

    override fun onResume() {
        super.onResume()
        //設定 toolbar title
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_now)
        // 設定 menu
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}