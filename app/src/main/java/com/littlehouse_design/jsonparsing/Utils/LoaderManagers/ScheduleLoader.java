package com.littlehouse_design.jsonparsing.Utils.LoaderManagers;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Catalog;
import com.littlehouse_design.jsonparsing.Utils.QueryUtils;

/**
 * Created by johnkonderla on 1/7/17.
 */

public class ScheduleLoader extends AsyncTaskLoader<Catalog> {
    private String url;
    public static final String LOG_TAG = ScheduleLoader.class.getSimpleName();
    public ScheduleLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public Catalog loadInBackground() {
        Log.d(LOG_TAG, "ScheduleLoader: loadInBackground...");
        String response = QueryUtils.getJSONFromUrl(url);
        return QueryUtils.RootWithSchedules(response);
    }

}
