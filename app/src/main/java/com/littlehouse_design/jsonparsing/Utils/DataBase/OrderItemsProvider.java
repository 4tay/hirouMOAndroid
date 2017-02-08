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
 * Created by johnkonderla on 1/12/17.
 */

public class OrderItemsProvider extends ContentProvider {

    private static final String LOG_TAG = OrderItemsProvider.class.getSimpleName();

    private static final int ITEMS_BY_ORDER = 100;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_ORDER_ITEMS,
                ITEMS_BY_ORDER);
    }
    private OrdersDBHelper orderItemsDBHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        orderItemsDBHelper = new OrdersDBHelper(getContext());
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

        liteQueryBuilder.setTables(DatabaseContract.TABLE_ORDER_ITEMS);

        switch (uriMatcher.match(uri)) {
            case ITEMS_BY_ORDER:
                break;
            default:
                Log.d(LOG_TAG,"NO 'where' was given");
                break;
        }

        if(sortOrder == null || sortOrder.equals("")) {
            sortOrder = DatabaseContract.DEFAULT_SORT_ORDER_ITEMS;
        }

        db = orderItemsDBHelper.getReadableDatabase();
        Cursor cursor = liteQueryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = orderItemsDBHelper.getWritableDatabase();
        long id = db.insert(DatabaseContract.TABLE_ORDER_ITEMS,"",values);
        db.close();
        return ContentUris.withAppendedId(DatabaseContract.ORDER_ITEM_URI,id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Haven't deved this featured yet");
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("This provider does not support updates");
    }
}
