package com.linglingdr00.weather.ui.forecast

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.linglingdr00.weather.R
import com.linglingdr00.weather.databinding.FragmentForecastBinding


class ForecastFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val TAG = "ForecastFragment"
    private val viewModel: ForecastViewModel by activityViewModels()
    private var binding: FragmentForecastBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentForecastBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 設定 lifecycleOwner
        binding?.lifecycleOwner = this
        // 設定 view model 為 ForecastViewModel
        binding?.viewModel = viewModel
        // 設 adapter 為 ForecastAdapter
        binding?.forecastRecyclerView?.adapter = ForecastAdapter()
        // 設定 ForecastItemDecoration 調整 item 邊距
        binding?.forecastRecyclerView?.addItemDecoration(ForecastItemDecoration())
    }

    //新增 toolbar 的下拉式選單(spinner) menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        Log.d(TAG, "onCreateOptionsMenu()")
        menu.clear()
        inflater.inflate(R.menu.toolbar_menu, menu)

        val spinnerItem = menu.findItem(R.id.spinner)
        val spinnerView = spinnerItem?.actionView as Spinner

        /*
        val str = listOf("北部", "中部", "南部")
        ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            str).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerView.adapter = adapter
        }
        */

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.forecast_array, // 選單中的 item
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerView.adapter = adapter
        }
        spinnerView.onItemSelectedListener = this@ForecastFragment

    }

    // 當 spinner menu 選擇 item 時的動作
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d(TAG, "onItemSelected: $position, $id")
        val cityList = when (position) {
            0 -> resources.getStringArray(R.array.northern_array).toList() as MutableList<String>
            1 -> resources.getStringArray(R.array.central_array).toList() as MutableList<String>
            2 -> resources.getStringArray(R.array.southern_array).toList() as MutableList<String>
            3 -> resources.getStringArray(R.array.eastern_array).toList() as MutableList<String>
            else -> resources.getStringArray(R.array.outlying_array).toList() as MutableList<String>
        }
        Log.d(TAG, "position: $position, cityList: $cityList")
        if (viewModel.status.value == ForecastViewModel.WeatherApiStatus.DONE) {
            viewModel.setAreaData(cityList)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d(TAG, "onNothingSelected")
    }

    override fun onResume() {
        super.onResume()
        //設定 toolbar title
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_forecast)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}