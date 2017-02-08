package com.littlehouse_design.jsonparsing.Utils.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by johnkonderla on 1/9/17.
 */

public class OrdersProvider extends ContentProvider {

    private static final String LOG_TAG = OrdersProvider.class.getSimpleName();


    public static final int ORDER_TABLE = 100;
    public static final int ORDER_ITEMS_TABLE = 101;
    public static final int TOP_TEN_ORDERS = 102;
    public static final int ORDER_WITH_ID = 103;
    public static final int PREFERENCES_TABLE = 104;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_ORDERS,
                ORDER_TABLE);
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_ORDER_ITEMS, ORDER_ITEMS_TABLE);
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_ORDERS,
                TOP_TEN_ORDERS);
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_ORDERS + "/#",
                ORDER_WITH_ID);
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_PREFERENCES, PREFERENCES_TABLE);

    }

    private OrdersDBHelper ordersDBHelper;
    private SQLiteDatabase db;


    @Override
    public boolean onCreate() {
        ordersDBHelper = new OrdersDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {return null;}

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder liteQueryBuilder = new SQLiteQueryBuilder();



        switch (uriMatcher.match(uri)) {
            case TOP_TEN_ORDERS:
                liteQueryBuilder.setTables(DatabaseContract.TABLE_ORDERS);
                break;
            case ORDER_WITH_ID:
                liteQueryBuilder.setTables(DatabaseContract.TABLE_ORDERS);
                liteQueryBuilder.appendWhere(
                        DatabaseContract.TableOrders.COL_ID + "=" +
                                uri.getLastPathSegment());
                break;
            case ORDER_ITEMS_TABLE:
                liteQueryBuilder.setTables(DatabaseContract.TABLE_ORDER_ITEMS);
                Log.d(LOG_TAG,"Selecting from orderItems");
                break;
            case PREFERENCES_TABLE:
                liteQueryBuilder.setTables(DatabaseContract.TABLE_PREFERENCES);
                Log.d(LOG_TAG,"Selecting from preferences");
                break;

                default:
                    Log.d(LOG_TAG,"NO 'where' was given");
                    break;
        }

        if(sortOrder == null || sortOrder.equals("")) {
            sortOrder = DatabaseContract.DEFAULT_SORT_ORDERS;
        }

        db = ordersDBHelper.getReadableDatabase();
        Cursor cursor = liteQueryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = ordersDBHelper.getWritableDatabase();
        long id;
        switch (uriMatcher.match(uri)) {
            case ORDER_ITEMS_TABLE:
                id = db.insert(DatabaseContract.TABLE_ORDER_ITEMS,"",values);
                db.close();
                Log.d(LOG_TAG,"I inserted an item??");
                return ContentUris.withAppendedId(DatabaseContract.ORDER_ITEM_URI,id);
            case PREFERENCES_TABLE:
                id = db.insert(DatabaseContract.TABLE_PREFERENCES,"",values);
                db.close();
                Log.d(LOG_TAG,"I inserted a pref!");
                return ContentUris.withAppendedId(DatabaseContract.PREFERENCES_URI,id);
            default:
                id = db.insert(DatabaseContract.TABLE_ORDERS,"",values);
                db.close();
                Log.d(LOG_TAG,"I inserted an order!");
                return ContentUris.withAppendedId(DatabaseContract.ORDER_URI,id);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Haven't deved this featured yet");
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db = ordersDBHelper.getWritableDatabase();

        int id;
        switch (uriMatcher.match(uri)) {
            case ORDER_ITEMS_TABLE:
                id = db.update(DatabaseContract.TABLE_ORDER_ITEMS,values,selection,selectionArgs);
                Log.d(LOG_TAG,"Updated Order Items Table");
                break;
            case PREFERENCES_TABLE:
                id= db.update(DatabaseContract.TABLE_PREFERENCES,values,selection,selectionArgs);
                Log.d(LOG_TAG,"Updated Preferences Table");
                break;
            default:
                id = db.update(DatabaseContract.TABLE_ORDERS,values,selection,selectionArgs);
                Log.d(LOG_TAG,"Updated Orders Table");
                break;
        }
        return id;
    }


}
