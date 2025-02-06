package com.umc_msmg.frontend.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.fragment.StepperFragment
import com.umc_msmg.frontend.fragment.WorkoutFragment

class CompositeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.composite_widget_provider)

            val stepIntent = Intent(context, StepperFragment::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val stepPending = PendingIntent.getActivity(context, appWidgetId, stepIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_stepper_section, stepPending)

            val workoutIntent = Intent(context, WorkoutFragment::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val workoutPending = PendingIntent.getActivity(context, appWidgetId, workoutIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.widget_workout_section, workoutPending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
