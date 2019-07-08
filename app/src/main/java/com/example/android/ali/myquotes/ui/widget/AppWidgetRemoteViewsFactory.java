package com.example.android.ali.myquotes.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.ali.myquotes.R;
import com.example.android.ali.myquotes.model.Quote;

import java.util.ArrayList;
import java.util.List;

public class AppWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Quote> mQuotes = new ArrayList<>();
    private int mWidgetID;

    public AppWidgetRemoteViewsFactory(Context context, List<Quote> quotes, Intent intent){
        this.mContext = context;
        this.mQuotes = quotes;
        mWidgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        mQuotes.clear();
    }

    @Override
    public int getCount() {
        if(mQuotes != null){
            return mQuotes.size();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.widget_quote, mQuotes.get(position).getText());

        Bundle extras = new Bundle();
        extras.putInt(AppWidget.EXTRA_ITEM_POSITION, position);
        Intent intent = new Intent();
        intent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_quote, intent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
