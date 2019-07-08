package com.example.android.ali.myquotes.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.android.ali.myquotes.R;

public class AppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetItemFactory factory = new WidgetItemFactory(getApplicationContext(), intent);
        return factory;
    }

    class WidgetItemFactory implements RemoteViewsFactory{
        private Context mContext;
        private String[] data = {"one", "Two", "Three"};

        WidgetItemFactory(Context context, Intent intent){
            this.mContext = context;
        }

        @Override
        public void onCreate() {
            //Connect to data source

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            rv.setTextViewText(R.id.widget_quote, data[position]);
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
            return true;
        }
    }
}
