package com.littlehouse_design.jsonparsing;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Item;
import com.littlehouse_design.jsonparsing.Utils.QueryUtils;

import java.util.Locale;

public class ItemActivity extends AppCompatActivity {
    public static final String LOG_TAG = ItemActivity.class.getSimpleName();
    private String storeNumber;
    private String myUrl = "http://store" + storeNumber + ".collegestoreonline.com/ePOS?form=shared3/json/merchandise/item.json&store=898&item=";
    private String itemNumb;
    private String itemJSON;
    private Item item;
    private TextView itemDescription;
    private TextView itemPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            itemNumb = intent.getStringExtra("ITEM_NUMB");
        } else {
            Log.d(LOG_TAG, "My intent was empty?");
        }
        new LongOp().execute();

        itemDescription = (TextView) findViewById(R.id.tv_item_name);
        itemPrice = (TextView) findViewById(R.id.tv_price);
    }
    private class LongOp extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... json) {
                Log.d(LOG_TAG, myUrl + itemNumb);
                itemJSON = QueryUtils.getJSONFromUrl(myUrl + itemNumb);
                item = QueryUtils.getSingleItem(itemJSON);
            return "Execute";
        }

        @Override
        protected void onPostExecute(String result) {
            itemDescription.setText(item.getItemDescriptor());
            String stringPrice = "$ " + String.format(Locale.US,"%.2f",item.getPrice());
            itemPrice.setText(stringPrice);



        }
    }
}
