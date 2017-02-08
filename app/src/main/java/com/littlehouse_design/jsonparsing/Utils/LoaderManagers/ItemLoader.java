package com.littlehouse_design.jsonparsing.Utils.LoaderManagers;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Item;
import com.littlehouse_design.jsonparsing.Utils.QueryUtils;

import java.util.ArrayList;

/**
 * Created by johnkonderla on 1/7/17.
 */

public class ItemLoader extends AsyncTaskLoader<ArrayList<Item>> {
private String url;
public static final String LOG_TAG = ItemLoader.class.getSimpleName();
public ItemLoader(Context context, String url) {
        super(context);
        this.url = url;
        }
    @Override
    public ArrayList<Item> loadInBackground() {
            Log.d(LOG_TAG, "loadInBackground...items");
            String response = QueryUtils.getJSONFromUrl(url);
            return QueryUtils.getItemsFromJSON(response);
    }
}
