package com.littlehouse_design.jsonparsing;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderComplete extends AppCompatActivity {

    TextView pickupTime;
    TextView pickupCover;
    TextView orderTotal;
    private static final String LOG_TAG = OrderComplete.class.getSimpleName();
    private static final String ORDER_NUMBER = "orderNumber";
    private static final String PICKUP_DATE = "pickupDate";
    private String total;
    private int orderNumber;
    private String pickUpDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        final ActionBar aBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar,null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textView = (TextView) viewActionBar.findViewById(R.id.tv_action_bar);
        textView.setText("Checkout Complete");
        aBar.setCustomView(viewActionBar,params);
        aBar.setDisplayShowCustomEnabled(true);
        aBar.setDisplayShowTitleEnabled(false);
        aBar.setDisplayHomeAsUpEnabled(false);

        TextView goHome = (TextView) findViewById(R.id.tv_complete_order_again);
        goHome.setBackgroundResource(R.drawable.rounded_corners_3);

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),ScheduleScreen.class);
                startActivity(intent);
                finish();
            }
        });

        pickupTime = (TextView) findViewById(R.id.tv_complete_order_pickup);
        pickupCover = (TextView) findViewById(R.id.tv_complete_order_title);
        orderTotal = (TextView) findViewById(R.id.tv_complete_order_total);


        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            orderNumber = intent.getIntExtra(ORDER_NUMBER,0);
            pickUpDate = intent.getStringExtra(PICKUP_DATE);
            total = intent.getStringExtra("total");
            pickupTime.setText(pickUpDate);
            orderTotal.setText(total);

        } else {
            Log.d(LOG_TAG, "My intent was empty?");
            pickupCover.setText("");

        }

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),ScheduleScreen.class);
        startActivity(intent);
        finish();
    }
}
