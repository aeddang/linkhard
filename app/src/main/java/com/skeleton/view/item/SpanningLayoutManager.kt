package com.skeleton.view.item

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.lib.view.layoutmanager.SpanningLinearLayoutManager

class HorizontalLinearLayoutManager(context: Context): SpanningLinearLayoutManager(context,  LinearLayoutManager.HORIZONTAL, false)
class VerticalLinearLayoutManager(context: Context): SpanningLinearLayoutManager(context,  LinearLayoutManager.VERTICAL, false)