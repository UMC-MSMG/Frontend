// CompositeWidgetProvider.kt
package com.umc_msmg.frontend.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.umc_msmg.frontend.R

class CompositeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.composite_widget_provider)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
