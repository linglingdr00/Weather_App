package com.linglingdr00.weathernotification.ui.now

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "現在天氣"
    }
    val text: LiveData<String> = _text
}