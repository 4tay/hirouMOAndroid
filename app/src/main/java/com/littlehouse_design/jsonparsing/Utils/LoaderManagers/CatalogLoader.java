package com.littlehouse_design.jsonparsing.Utils.LoaderManagers;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Catalog;
import com.littlehouse_design.jsonparsing.Utils.QueryUtils;

/**
 * Created by johnkonderla on 1/8/17.
 */

public class CatalogLoader extends AsyncTaskLoader<Catalog> {
    private String url;
    private String parentCode;
    private String catCode;
    private int type;
    public static final String LOG_TAG = CatalogLoader.class.getSimpleName();

    public CatalogLoader(Context context, String url, int type) {
        super(context);
        this.url = url;
        this.type = type;
    }
    public CatalogLoader(Context context, String url, String parentCode, int type) {
        super(context);
        this.url = url;
        this.parentCode = parentCode;
        this.type = type;
    }
    public CatalogLoader(Context context, String url, String parentCode, String catCode, int type) {
        super(context);
        this.url = url;
        this.parentCode = parentCode;
        this.catCode = catCode;
        this.type = type;
    }

    @Override
    public Catalog loadInBackground() {
        String response;
        switch (type) {
            case 0:
                Log.d(LOG_TAG, "ScheduleLoader: loadInBackground...");
                response = QueryUtils.getJSONFromUrl(url);
                return QueryUtils.RootWithSchedules(response);
            case 1:
                response = QueryUtils.getJSONFromUrl(url);
                return QueryUtils.SchedulesWithCats(response,parentCode);
            case 2:
                Log.d(LOG_TAG, "SubCatLoader: loadInBackground...");
                response = QueryUtils.getJSONFromUrl(url);
                return QueryUtils.CatWithSubCats(response, parentCode, catCode);
            default:
                return new Catalog();

        }

    }
}
