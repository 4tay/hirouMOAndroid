package com.littlehouse_design.jsonparsing.Utils.DataBase;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* Process DB insert on a background thread */
public class AddOrderService extends IntentService {
    private static final String TAG = AddOrderService.class.getSimpleName();

    public static final String ACTION_INSERT = TAG + ".INSERT";

    public static final String EXTRA_VALUES = TAG + ".ContentValues";

    public static void insertNewOrder(Context context, ContentValues values) {
        Intent intent = new Intent(context, AddOrderService.class);
        intent.setAction(ACTION_INSERT);
        intent.putExtra(EXTRA_VALUES, values);
        Log.d(TAG,"Launched!~");

        context.startService(intent);
    }

    public AddOrderService() {
        super(TAG);
        Log.d(TAG,"I started the AddOrderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"DID this launch??");
        if (ACTION_INSERT.equals(intent.getAction())) {
            ContentValues values = intent.getParcelableExtra(EXTRA_VALUES);
            performInsert(values);
        }
    }

    private void performInsert(ContentValues values) {

        Log.d(TAG,"perform insert??");
        if (getContentResolver().insert(DatabaseContract.ORDER_URI, values) != null) {
            Log.d(TAG, "Inserted new order");
        } else {
            Log.d(TAG, "Error inserting new order");
        }
    }
}
