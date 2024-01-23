package com.linglingdr00.weathernotification.ui.forecast

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ForecastItemDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val allCount = parent.adapter?.itemCount ?: 0

        //設定 item 的邊距
        outRect.top = 30
        outRect.left = 30
        outRect.right = 30

        // 設定最後一個 item 的下邊距
        if (position == allCount - 1) {
            outRect.bottom = 30
        }

    }
}