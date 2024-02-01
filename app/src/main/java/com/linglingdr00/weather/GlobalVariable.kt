package com.linglingdr00.weather

import android.app.Application

class GlobalVariable: Application() {
    companion object {

        //判斷是否已有權限
        var havePermission = false
        //判斷是否已得到位置
        var haveLocation = false

    }
}