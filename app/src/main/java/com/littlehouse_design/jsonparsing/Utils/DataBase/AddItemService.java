package com.littlehouse_design.jsonparsing.Utils.DataBase;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by johnkonderla on 1/12/17.
 */

public class AddItemService extends IntentService {
    private static final String LOG_TAG = AddItemService.class.getSimpleName();

    public static final String ACTION_INSERT = LOG_TAG + ".INSERT";
    public static final String EXTRA_VALUES = LOG_TAG + ".ContentValues";

    public static void insertNewItem(Context context, ContentValues values) {
        Intent intent = new Intent(context, AddItemService.class);
        intent.setAction(ACTION_INSERT);
        intent.putExtra(EXTRA_VALUES,values);
        Log.d(LOG_TAG,"This was my call on the AddItemService");
        context.startService(intent);
    }

    public AddItemService() {super(LOG_TAG); }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ACTION_INSERT.equals(intent.getAction())) {
            ContentValues values = intent.getParcelableExtra(EXTRA_VALUES);
            performItemInsert(values);
        }
    }

    private void performItemInsert(ContentValues values) {

        if(getContentResolver().insert(DatabaseContract.ORDER_ITEM_URI,values) != null) {
            Log.d(LOG_TAG,"Inserted an item to the cart!");
        }
        else {
            Log.d(LOG_TAG,"Error adding item to the cart");
        }
    }
}
