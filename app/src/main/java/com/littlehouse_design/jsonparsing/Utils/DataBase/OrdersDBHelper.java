package com.littlehouse_design.jsonparsing.Utils.DataBase;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by johnkonderla on 1/11/17.
 */

public class OrdersDBHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = OrdersDBHelper.class.getSimpleName();

    public static final String DB_NAME = "orders.db";
    public static final int DB_VERSION = 4;

    private static final String SQL_CREATE_TABLE_ORDERS = "CREATE TABLE " +
            DatabaseContract.TABLE_ORDERS + " (" +
            DatabaseContract.TableOrders.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabaseContract.TableOrders.COL_NAME + " TEXT," +
            DatabaseContract.TableOrders.COL_EMAIL + " TEXT," +
            DatabaseContract.TableOrders.COL_PHONE + " TEXT," +
            DatabaseContract.TableOrders.COL_TRANS_DATE + " TEXT," +
            DatabaseContract.TableOrders.COL_PICKUP_TIME + " TEXT," +
            DatabaseContract.TableOrders.COL_PAYMENT_TYPE + " TEXT," +
            DatabaseContract.TableOrders.COL_ACCOUNT_NUM + " TEXT," +
            DatabaseContract.TableOrders.COL_SUB_TOTAL + " INTEGER," +
            DatabaseContract.TableOrders.COL_TAXABLE_AMOUNT + " INTEGER," +
            DatabaseContract.TableOrders.COL_TAX + " INTEGER," +
            DatabaseContract.TableOrders.COL_TOTAL + " INTEGER, " +
            DatabaseContract.TableOrders.COL_STATUS + " INTEGER )";

    private static final String SQL_CREATE_TABLE_ORDER_ITEMS = "CREATE TABLE " +
            DatabaseContract.TABLE_ORDER_ITEMS + " (" +
            DatabaseContract.TableOrderItems.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabaseContract.TableOrderItems.COL_ORDER_ID + " INTEGER," +
            DatabaseContract.TableOrderItems.COL_ITEM_NAME + " TEXT," +
            DatabaseContract.TableOrderItems.COL_ITEM_NUMBER + " TEXT," +
            DatabaseContract.TableOrderItems.COL_ITEM_PRICE + " INTEGER," +
            DatabaseContract.TableOrderItems.COL_ITEM_QTY + " INTEGER )";
    //Preferences table
    private static final String SQL_CREATE_TABLE_PREFERENCES = "CREATE TABLE " +
            DatabaseContract.TABLE_PREFERENCES + " (" +
            DatabaseContract.TablePreferences.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabaseContract.TablePreferences.COL_PREF_TYPE + " INTEGER," +
            DatabaseContract.TablePreferences.COL_PREF_VALUE + " TEXT" + " )";


    private Resources resources;

    public OrdersDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        resources = context.getResources();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ORDERS);
        db.execSQL(SQL_CREATE_TABLE_ORDER_ITEMS);
        db.execSQL(SQL_CREATE_TABLE_PREFERENCES);

        Log.d(LOG_TAG,"The onCreate method for the DB was called");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_PREFERENCES);

        Log.d(LOG_TAG,"On upgrade..");
        onCreate(db);

    }
}
