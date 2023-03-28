package com.roman_tsisyk.whitenoisegenerator

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews

class MyWidgetProvider : AppWidgetProvider() {

    private var whiteNoiseGenerator: WhiteNoiseGenerator? = null

    @SuppressLint("RemoteViewLayout")
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Loop through all widgets
        for (appWidgetId in appWidgetIds) {
            // Create a RemoteViews object for the widget's view
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Set up the "play" button
            val playIntent = Intent(context, MyWidgetProvider::class.java)
            playIntent.action = "ACTION_PLAY"
            val playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            views.setOnClickPendingIntent(R.id.play_button, playPendingIntent)

            // Set up the "stop" button
            val stopIntent = Intent(context, MyWidgetProvider::class.java)
            stopIntent.action = "ACTION_STOP"
            val stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            views.setOnClickPendingIntent(R.id.stop_button, stopPendingIntent)

            // Set the widget's view to show the "play" button and hide the "stop" button by default
            views.setViewVisibility(R.id.play_button, View.VISIBLE)
            views.setViewVisibility(R.id.stop_button, View.GONE)

            // Update the widget's view
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    @SuppressLint("RemoteViewLayout")
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            // Handle widget update
            onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, MyWidgetProvider::class.java)))
        } else if (action == "ACTION_PLAY") {
            // Start the white noise playback
            whiteNoiseGenerator = WhiteNoiseGenerator(context, durationSeconds = 10)
            whiteNoiseGenerator?.start()

            // Update the widget's view
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setViewVisibility(R.id.play_button, View.GONE)
            views.setViewVisibility(R.id.stop_button, View.VISIBLE)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, MyWidgetProvider::class.java))
            appWidgetManager.updateAppWidget(appWidgetIds, views)

            // Schedule a task to revert the pause button to the play button when the duration time runs out
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                // Stop the white noise playback
                whiteNoiseGenerator?.stop()
                whiteNoiseGenerator = null

                // Update the widget's view
                val views = RemoteViews(context.packageName, R.layout.widget_layout)
                views.setViewVisibility(R.id.play_button, View.VISIBLE)
                views.setViewVisibility(R.id.stop_button, View.GONE)
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, MyWidgetProvider::class.java))
                appWidgetManager.updateAppWidget(appWidgetIds, views)
            }, 10000)
        } else if (action == "ACTION_PAUSE") {
            // Stop the white noise playback
            whiteNoiseGenerator?.stop()
            whiteNoiseGenerator = null

            // Update the widget's view
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setViewVisibility(R.id.play_button, View.VISIBLE)
            views.setViewVisibility(R.id.stop_button, View.GONE)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, MyWidgetProvider::class.java))
            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }
    }
}
