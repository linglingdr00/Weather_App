package com.linglingdr00.weather.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.linglingdr00.weather.databinding.FragmentForecastBinding


class ForecastFragment : Fragment() {

    private val viewModel: ForecastViewModel by activityViewModels()
    private var binding: FragmentForecastBinding? = null

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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}