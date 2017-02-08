package com.littlehouse_design.jsonparsing.Utils.LoaderManagers;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Catalog;
import com.littlehouse_design.jsonparsing.Utils.QueryUtils;

/**
 * Created by johnkonderla on 1/7/17.
 */

public class SubCatLoader extends AsyncTaskLoader<Catalog> {
    private String url;
    private String parentCode;
    private String catCode;
    public static final String LOG_TAG = ScheduleLoader.class.getSimpleName();
    public SubCatLoader(Context context, String url, String parentCode, String catCode) {
        super(context);
        this.url = url;
        this.parentCode = parentCode;
        this.catCode = catCode;
    }
    @Override
    public Catalog loadInBackground() {
        Log.d(LOG_TAG, "ScheduleLoader: loadInBackground...");
        String response = QueryUtils.getJSONFromUrl(url);
        return QueryUtils.CatWithSubCats(response, parentCode, catCode);
    }

}
