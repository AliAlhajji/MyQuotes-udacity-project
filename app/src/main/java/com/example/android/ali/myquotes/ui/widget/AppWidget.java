package com.example.android.ali.myquotes.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.android.ali.myquotes.R;
import com.example.android.ali.myquotes.utils.AppConstants;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    public static String EXTRA_ITEM_POSITION = "extra_item_position";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.PREF_WIDGET, context.MODE_PRIVATE);
        String quote = sharedPreferences.getString(AppConstants.PREF_QUOTE, "");
        String book = sharedPreferences.getString(AppConstants.PREF_BOOK, context.getString(R.string.widget_unknown_book));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        rv.setTextViewText(R.id.appwidget_text, quote);
        rv.setTextViewText(R.id.appwidget_book, book);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_text);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void updateWidgets(Context context){
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, AppWidget.class));

        for(int id : appWidgetIds){
            AppWidget.updateAppWidget(context, manager, id);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

