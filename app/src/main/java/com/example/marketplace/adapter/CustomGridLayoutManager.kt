package com.example.marketplace.adapter

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.tool.Tool
import java.lang.IndexOutOfBoundsException

class CustomGridLayoutManager(val context: Context, column_count: Int = 1)
    : GridLayoutManager(context, column_count, VERTICAL, false) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Tool.debugMessage("Inconsistency detected")
        }
    }
}