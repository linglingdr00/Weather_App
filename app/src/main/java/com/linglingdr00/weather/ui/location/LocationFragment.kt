package com.linglingdr00.weather.ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.linglingdr00.weather.R
import com.linglingdr00.weather.databinding.FragmentLocationBinding


class LocationFragment : Fragment() {

    private val TAG = "LocationFragment"
    private val locationViewModel: LocationViewModel by viewModels()
    private var binding: FragmentLocationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}