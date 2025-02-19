package com.umc_msmg.frontend.widget  // 패키지 위치 변경

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.umc_msmg.frontend.R

class CallWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateCallWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateCallWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.call_widget_provider)
        views.setTextViewText(R.id.call_widget_text, "통화")
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
