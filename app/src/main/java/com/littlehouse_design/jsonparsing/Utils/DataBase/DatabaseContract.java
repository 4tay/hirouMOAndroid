package com.littlehouse_design.jsonparsing.Utils.DataBase;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by johnkonderla on 1/9/17.
 */

public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.littlehouse_design.jsonparsing";

    public static final String TABLE_ORDERS = "orders";
    public static final class TableOrders implements BaseColumns {
        public static final String COL_ID = "_id";
        public static final String COL_NAME = "name";
        public static final String COL_EMAIL = "email";
        public static final String COL_PHONE = "phone";
        public static final String COL_TRANS_DATE = "transactionDate";
        public static final String COL_PICKUP_TIME = "pickupTime";
        public static final String COL_PAYMENT_TYPE = "paymentType";
        public static final String COL_ACCOUNT_NUM = "accountNumb";
        public static final String COL_SUB_TOTAL = "subTotal";
        public static final String COL_TAXABLE_AMOUNT = "taxableAmount";
        public static final String COL_TAX = "tax";
        public static final String COL_TOTAL = "total";
        public static final String COL_STATUS = "statusId";
    }

    public static final String DEFAULT_SORT_ORDERS = TableOrders.COL_ID;
    // Base content Uri for accessing the provider
    public static final Uri ORDER_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_ORDERS)
            .build();

    public static final String TABLE_ORDER_ITEMS = "orderItems";
    public static final class TableOrderItems implements BaseColumns {
        public static final String COL_ID = "_id";
        public static final String COL_ORDER_ID = "orderId";
        public static final String COL_ITEM_NAME = "itemName";
        public static final String COL_ITEM_NUMBER = "itemNumber";
        public static final String COL_ITEM_PRICE = "itemPrice";
        public static final String COL_ITEM_QTY = "itemQty";
    }
    public static final String DEFAULT_SORT_ORDER_ITEMS = TableOrderItems.COL_ID;
    public static final Uri ORDER_ITEM_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_ORDER_ITEMS)
            .build();

    //Preferences
    public static final String TABLE_PREFERENCES = "prefs";
    public static final class TablePreferences implements BaseColumns {
        public static final String COL_ID = "_id";
        public static final String COL_PREF_TYPE = "prefType";
        public static final String COL_PREF_VALUE = "prefValue";
    }
    public static final String DEFAULT_SORT_PREFS = TablePreferences.COL_ID;
    public static final Uri PREFERENCES_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_PREFERENCES)
            .build();
}