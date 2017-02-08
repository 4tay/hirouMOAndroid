package com.littlehouse_design.jsonparsing.Utils.DataBase;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by johnkonderla on 2/5/17.
 */




public class AddPreferenceService extends IntentService {
    private static final String TAG = AddPreferenceService.class.getSimpleName();

    public static final String ACTION_INSERT = TAG + ".INSERT";

    public static final String EXTRA_VALUES = TAG + ".ContentValues";

    public static void insertNewPref(Context context, ContentValues values) {
        Intent intent = new Intent(context, AddPreferenceService.class);
        intent.setAction(ACTION_INSERT);
        intent.putExtra(EXTRA_VALUES, values);
        Log.d(TAG,"Launched!~");

        context.startService(intent);
    }

    public AddPreferenceService() {
        super(TAG);
        Log.d(TAG,"I started the AddPreferenceService");
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
        if (getContentResolver().insert(DatabaseContract.PREFERENCES_URI, values) != null) {
            Log.d(TAG, "Inserted new pref");
        } else {
            Log.d(TAG, "Error inserting new pref");
        }
    }
}