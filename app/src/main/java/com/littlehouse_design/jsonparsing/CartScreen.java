package com.littlehouse_design.jsonparsing;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.littlehouse_design.jsonparsing.Utils.Cart.Cart;
import com.littlehouse_design.jsonparsing.Utils.DataBase.DatabaseContract;
import com.littlehouse_design.jsonparsing.Utils.Cart.OrderItem;
import com.littlehouse_design.jsonparsing.Utils.Adapters.CartRecyclerAdapter;
import com.littlehouse_design.jsonparsing.Utils.Adapters.DividerScheduleDecoration;
import com.littlehouse_design.jsonparsing.Utils.TCPClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CartScreen extends AppCompatActivity {
    private static final String LOG_TAG = CartScreen.class.getSimpleName();
    private Spinner paymentSpinner;
    private Spinner pickupSpinner;
    //private ProgressBar progressBar;
    private ArrayList<String> paymentMethods;
    private ArrayList<String> pickupTimes;
    private Cursor orderCursor;
    private Cursor itemCursor;
    private Cart cart;
    private ArrayList<OrderItem> itemList;
    private String staticHost = "192.168.1.66"; //AWS box IP: 54.86.78.90
    private static int staticPort = 4000;
    private static int tokenPort = 4001;
    private String submitStat = "lololololol";
    private TCPClient mTcpClient;
    private AutoCompleteTextView nameTV;
    private AutoCompleteTextView emailTV;
    private AutoCompleteTextView phoneNumberTV;
    private TextView subTotalTV;
    private TextView taxTV;
    private TextView totalTV;
    private String textInputName;
    private String textInputEmail;
    private String textInputPhone;
    private ArrayList<String> savedBillingNames;
    private ArrayList<String> savedBillingEmails;
    private ArrayList<String> savedBillingPhones;
    private int intSubTotal;
    private int taxRate = 5000;
    private int intTax;
    private int intTotal;
    private String transTime;
    private String pickupTime;
    private static final String PICKUP_DATE = "pickupDate";
    private String clientToken;
    private int REQUEST_CODE = 10;
    private String nonce;
    private String paymentResponse;
    private String stringTotal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_screen);


        //inflate ActionBar
        final ActionBar aBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar,null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textView = (TextView) viewActionBar.findViewById(R.id.tv_action_bar);
        textView.setText("Checkout");
        aBar.setCustomView(viewActionBar,params);
        aBar.setDisplayShowCustomEnabled(true);
        aBar.setDisplayShowTitleEnabled(false);
        aBar.setDisplayHomeAsUpEnabled(true);

        //Create my lists to store billing info
        savedBillingNames = new ArrayList<>();
        savedBillingEmails = new ArrayList<>();
        savedBillingPhones = new ArrayList<>();

        //Billing info EditText
        nameTV = (AutoCompleteTextView) findViewById(R.id.et_cart_name);
        emailTV = (AutoCompleteTextView) findViewById(R.id.et_cart_email);
        phoneNumberTV = (AutoCompleteTextView) findViewById(R.id.et_cart_phone);

        //Order totals
        subTotalTV = (TextView) findViewById(R.id.tv_cart_sub_total);
        taxTV = (TextView) findViewById(R.id.tv_cart_tax);
        totalTV = (TextView) findViewById(R.id.tv_cart_total);

        //Payment selector
        paymentSpinner = (Spinner) findViewById(R.id.sp_payment_spinner);
        //Pickup time selector
        pickupSpinner = (Spinner) findViewById(R.id.sp_pickup_spinner);

        //Payment method list
        paymentMethods = new ArrayList<>();
        paymentMethods.add("Pay at pickup");
        paymentMethods.add("Credit card");

        Date date = new Date();
        SimpleDateFormat transFormat = new SimpleDateFormat("HH:mm");

        long pickupTime = date.getTime();
        pickupTimes = new ArrayList<>();
        for(int i = 10; i > 0; i--) {
            long truncatedTime =(pickupTime / 10000000);
            long reportedTime = (truncatedTime * 10000000) + 1200000;
            pickupTime = pickupTime + 600000;
            Log.d(LOG_TAG,String.valueOf(reportedTime));
            pickupTimes.add(String.valueOf(transFormat.format(pickupTime)));
        }

        //String nowTime = String.valueOf(transFormat.format(date.getTime()));

        /*//Pickup time list
        pickupTimes = new ArrayList<>();
        pickupTimes.add("12:00 PM");
        pickupTimes.add("12:10 PM");
        pickupTimes.add("12:20 PM");
        pickupTimes.add("12:30 PM");
        pickupTimes.add("12:40 PM");
        pickupTimes.add("12:50 PM");
        pickupTimes.add("1:00 PM");
        pickupTimes.add("1:10 PM");
        pickupTimes.add("1:20 PM");
        pickupTimes.add("1:30 PM");
        pickupTimes.add("1:40 PM");
        pickupTimes.add("1:50 PM");
        pickupTimes.add("2:00 PM");*/

        //ArrayAdapter for my payment method selector
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                paymentMethods);
        paymentSpinner.setAdapter(paymentAdapter);

        //ArrayAdapter for my pickup time selector
        ArrayAdapter<String> pickupAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                pickupTimes);
        pickupSpinner.setAdapter(pickupAdapter);

        //Submit button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_checkout_button);

        //Submit order onClick
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textInputName = nameTV.getText().toString().trim();
                textInputEmail = emailTV.getText().toString().trim();
                textInputPhone = phoneNumberTV.getText().toString().trim();

                //Test name. If it is empty throw error
                if(textInputName.length() ==0 || textInputName.equals("")) {
                    nameTV.setError(getString(R.string.error_empty_billing_name));

                }
                //Test email. If it is empty throw error
                else if(textInputEmail.length() == 0 || textInputEmail.equals("")) {
                    emailTV.setError(getString(R.string.error_empty_email));
                }
                //Test phone. If it is empty throw error
                else if(textInputPhone.length() == 0 || textInputPhone.equals("")) {
                    phoneNumberTV
                            .setError(getString(R.string.error_empty_phone));
                }
                //If the above checks out, start sending.
                else {
                    if(paymentSpinner.getSelectedItem().toString().equals("Credit card")) {
                        onBraintreeSubmit(getCurrentFocus());
                    }
                    else {
                        completeTrans();
                    }

                }
            }
        });


        new CartBuilder().execute();
        Log.d(LOG_TAG,"Getting token...");
        new getToken().execute("");


    }
    private void completeTrans() {
        //Create fragment to hold my loading bar
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoaderFrag loaderFrag = new LoaderFrag();
        //Show my fragment for loading
        loaderFrag.show(ft, LOG_TAG);
        //Make my JSON to send to the server
        submitStat = jsonEvent();

        //Start the AsyncTask to send my JSON to the server and save the order information into
        //the SQLite db for reference.
        new sendOrder().execute("");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                if(result.getPaymentMethodNonce() != null && result.getPaymentMethodNonce().getNonce() != null) {
                    nonce = result.getPaymentMethodNonce().getNonce();
                    Log.d(LOG_TAG, result.getPaymentMethodNonce().getNonce());
                }
                // use the result to update your UI and send the payment method nonce to your server
                new chargeNonce().execute("");
                Log.d(LOG_TAG,"Launched chargeNonce");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.d(LOG_TAG,"USER CANCELED REQUEST");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e(LOG_TAG, "ERROR WITH PAYMENT INFO: " + error.toString());
            }
        }
    }
    public void onBraintreeSubmit(View v) {
        if(clientToken == null || clientToken.equals("token")) {
                    clientToken = ("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiI1ZTQ3MmZiNzE4YmYxZWMwMWE2YzAzMzczMGEwOGE5MGUwZDZiNjg0ODA5MWQyNzhiOWM3YjIxZjk2MjYxMWQ3fGNyZWF0ZWRfYXQ9MjAxNy0wMS0yN1QwMzozMjoyMS44NTY1NjU4NjYrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=");
            Log.d(LOG_TAG,"My token was null or \"token\"");
        } else {
            Log.d(LOG_TAG, "My client token: " + clientToken);
        }
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(clientToken);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }
    public static class LoaderFrag extends DialogFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        }
        @Override
        public void onStart() {
            super.onStart();
            Dialog d = getDialog();
            if (d != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                d.getWindow().setLayout(width,height);
            }
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.loading_screen_frag, container, false);
            root.setBackgroundColor(Color.TRANSPARENT);
            ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.pb_cart_progress_bar);
            //progressBar.setBackgroundColor(Color.TRANSPARENT);
            progressBar.setVisibility(View.VISIBLE);

            return root;
        }
    }
    private class CartBuilder extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void...voidOb) {
            orderCursor = getContentResolver().query(DatabaseContract.ORDER_URI,
                    new String[]{DatabaseContract.TableOrders.COL_ID,
                            DatabaseContract.TableOrders.COL_SUB_TOTAL},
                    "CAST (" + DatabaseContract.TableOrders.COL_STATUS + " AS TEXT) = ?",
                    new String[]{"0"},
                    null
            );

            try{
                if(orderCursor != null && orderCursor.moveToLast()) {

                    Log.d(LOG_TAG,"I got an order!!");
                    itemCursor = getContentResolver().query(DatabaseContract.ORDER_ITEM_URI,
                            null,
                            DatabaseContract.TableOrderItems.COL_ORDER_ID + " = ?",
                            new String[]{Integer.toString(orderCursor.getInt(0))},
                            null
                    );

                    Log.d(LOG_TAG, "Looking for items with orderID: " + Integer.toString(orderCursor.getInt(0)));
                    try {
                        if (itemCursor != null) {

                            cart = new Cart(orderCursor, itemCursor);
                            orderCursor.close();
                            itemCursor.close();
                        } else {
                            Toast.makeText(getApplicationContext(),"Items didn't make it",Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (SQLException e) {
                        Log.e(LOG_TAG,"Error building the items part of my order: " + e.toString());
                    }
                } else {
                    Log.d(LOG_TAG,"Couldn't get an order to checkout with");
                }

            }
            catch (SQLException e) {
                Log.e(LOG_TAG, "Error building the orders for my cart: " + e.toString());
            }

            itemList = cart.getOrderItems();

            for(int i = 0; i < itemList.size(); i++) {
                for(int j = 0; j < itemList.size(); j++) {
                    if(i == j) {
                        Log.d(LOG_TAG, "my variables where the same");
                    }
                    else if(itemList.get(i).getItemNumb().equals(itemList.get(j).getItemNumb())) {
                        itemList.get(i).setQuantity(itemList.get(i).getQuantity() + itemList.get(j).getQuantity());
                        itemList.remove(j);
                        Log.d(LOG_TAG,"added a qty to an item: " +
                                itemList.get(i).getItemNumb() + " Qty: " +
                                Integer.toString(itemList.get(i).getQuantity()));
                        i = 0;
                    }
                }
            }
            for(int i = 0; i < itemList.size(); i++) {
                intSubTotal = intSubTotal + (itemList.get(i).getItemPrice() * itemList.get(i).getQuantity());
            }

            Cursor suggestionCursor = getContentResolver().query(DatabaseContract.ORDER_URI,
                    new String[]{DatabaseContract.TableOrders.COL_NAME,
                            DatabaseContract.TableOrders.COL_EMAIL,
                            DatabaseContract.TableOrders.COL_PHONE},
                    "CAST (" + DatabaseContract.TableOrders.COL_STATUS + " AS TEXT) = ?",
                    new String[]{"1"},
                    null
            );

            if(suggestionCursor != null && suggestionCursor.moveToFirst()) {
                do{
                    boolean addName = true;
                    if(suggestionCursor.getString(0) != null) {
                        if(savedBillingNames.size() > 0) {
                            for (int i = 0; i < savedBillingNames.size(); i++) {
                                if (savedBillingNames.get(i).equals(suggestionCursor.getString(0))) {
                                    if(suggestionCursor.getCount() == (suggestionCursor.getPosition() + 1)) {
                                        savedBillingNames.remove(i);
                                        Log.d(LOG_TAG, "Added the most recent billing name to the END of the list");
                                    }
                                    else {
                                        addName = false;
                                        Log.d(LOG_TAG, "didn't add this name");
                                    }
                                }
                            }
                        }
                    } else {
                        Log.d(LOG_TAG,"My name suggestions looked empty...");
                    }
                    if(addName) {
                        savedBillingNames.add(suggestionCursor.getString(0));
                        Log.d(LOG_TAG,"IT WAS TRUE");
                    }
                    boolean addEmail = true;
                    if(suggestionCursor.getString(1) != null) {
                        if(savedBillingEmails.size() > 0) {
                            for (int i = 0; i < savedBillingEmails.size(); i++) {
                                if (suggestionCursor.getString(1).equals(savedBillingEmails.get(i))) {
                                    if(suggestionCursor.getCount() == (suggestionCursor.getPosition() + 1)) {
                                        savedBillingEmails.remove(i);
                                        Log.d(LOG_TAG, "Added the most recent billing email to the END of the list");
                                    }
                                    else {
                                        addEmail = false;
                                        Log.d(LOG_TAG, "didn't add this email");
                                    }
                                }
                            }
                        }
                    }
                    if(addEmail) {
                        savedBillingEmails.add(suggestionCursor.getString(1));
                        Log.d(LOG_TAG,"Added this email: " + suggestionCursor.getString(1));
                    }
                    boolean addPhone = true;
                    if(suggestionCursor.getString(2) != null) {
                        if (savedBillingPhones.size() > 0) {

                            for (int i = 0; i < savedBillingPhones.size(); i++) {
                                if (suggestionCursor.getString(2).equals(savedBillingPhones.get(i))) {

                                    if(suggestionCursor.getCount() == (suggestionCursor.getPosition() + 1)) {
                                        savedBillingPhones.remove(i);
                                        Log.d(LOG_TAG, "Added the most recent billing email to the END of the list");
                                    }
                                    else {
                                        addPhone = false;
                                        Log.d(LOG_TAG, "didn't add this phone number");
                                    }
                                }
                            }
                        }
                    }
                    if(addPhone) {
                        savedBillingPhones.add(suggestionCursor.getString(2));
                        Log.d(LOG_TAG,"Added this phone number: " + suggestionCursor.getString(2));
                    }
                } while(suggestionCursor.moveToNext());
                suggestionCursor.close();
            }
            else {
                Log.d(LOG_TAG,"No suggestions for billing info");
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void voidOb) {
            //update the recycler view here...
            populateCart();
            showSuggestedBilling();

        }
    }
    private void populateCart() {
        final String stringSubTotal = "$" + String.format(Locale.US,"%.2f",(float) intSubTotal / 100);
        subTotalTV.setText(stringSubTotal);
        float floatTax = (float) intSubTotal / 100 * ((float) taxRate / 100000);
        intTax = (int) (floatTax * 100);
        //intTotal = intSubTotal * (taxRate / 1000);

        intTotal = (int) (100 * ((float) intSubTotal / 100 + floatTax));
        final String stringTax = "$" + String.format(Locale.US,"%.2f",floatTax );
        taxTV.setText(stringTax);
        stringTotal = "$" + String.format(Locale.US,"%.2f",(float) intSubTotal / 100 + floatTax);
        totalTV.setText(stringTotal);


        final RecyclerView cartItemView = (RecyclerView) findViewById(R.id.rv_cart_item_list);
        CartRecyclerAdapter cartRecyclerAdapter = new CartRecyclerAdapter() {
            @Override
            public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false);
                return new ItemHolder(view);
            }

            @Override
            public void onBindViewHolder(ItemHolder holder, int position) {
                holder.itemName.setText(cart.getOrderItems().get(position).getItemName());
                final String stringPrice = "$" + String.format(Locale.US,"%.2f",(float) cart.getOrderItems().get(position).getItemPrice() / 100);
                holder.itemPrice.setText(stringPrice);
                holder.itemQty.setText(Integer.toString(itemList.get(position).getQuantity()));
                Picasso.with(getApplicationContext()).load("http://store898.collegestoreonline.com/webitemimages/898/" + cart.getOrderItems().get(position).getItemNumb() + ".jpg").placeholder(R.drawable.no_image).into(holder.itemImage);

            }

            @Override
            public int getItemCount() {
                if(cart != null && cart.getOrderItemsCount() > 0) {
                    return cart.getOrderItemsCount();
                }
                else {
                    return 0;
                }
            }
        };
        cartItemView.setAdapter(cartRecyclerAdapter);
        cartItemView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        cartItemView.setLayoutManager(layoutManager);
        cartItemView.addItemDecoration(new DividerScheduleDecoration(getApplicationContext()));
    }
    private void showSuggestedBilling() {
        if(savedBillingNames != null) {
            ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, savedBillingNames);
            //Find TextView control
            //Set the number of characters the user must type before the drop down list is shown
            nameTV.setThreshold(1);
            //Set the adapter
            nameTV.setAdapter(nameAdapter);
            if(savedBillingNames.size() > 0) {
                nameTV.setText(savedBillingNames.get(savedBillingNames.size() - 1));
                nameTV.clearFocus();
            }
        }

        if(savedBillingEmails != null) {
            ArrayAdapter<String> emailAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, savedBillingEmails);
            //Find TextView control
            //Set the number of characters the user must type before the drop down list is shown
            emailTV.setThreshold(1);
            //Set the adapter
            emailTV.setAdapter(emailAdapter);

            if(savedBillingEmails.size() > 0) {
                emailTV.setText(savedBillingEmails.get(savedBillingEmails.size() - 1));
            }
        }

        if(savedBillingPhones != null) {
            ArrayAdapter<String> phoneAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, savedBillingPhones);
            //Find TextView control
            //Set the number of characters the user must type before the drop down list is shown
            phoneNumberTV.setThreshold(1);
            //Set the adapter
            phoneNumberTV.setAdapter(phoneAdapter);
            if(savedBillingPhones.size() > 0) {
                phoneNumberTV.setText(savedBillingPhones.get(savedBillingPhones.size() - 1));
            }
        }
    }
    public class sendOrder extends AsyncTask<String,String,TCPClient> {
        @Override
        protected TCPClient doInBackground(String... message) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.TableOrders.COL_STATUS,1);
            contentValues.put(DatabaseContract.TableOrders.COL_NAME,textInputName);
            contentValues.put(DatabaseContract.TableOrders.COL_EMAIL,textInputEmail);
            contentValues.put(DatabaseContract.TableOrders.COL_PHONE,textInputPhone);
            contentValues.put(DatabaseContract.TableOrders.COL_TRANS_DATE,transTime);
            contentValues.put(DatabaseContract.TableOrders.COL_PICKUP_TIME,pickupTime);
            contentValues.put(DatabaseContract.TableOrders.COL_SUB_TOTAL,intSubTotal);
            contentValues.put(DatabaseContract.TableOrders.COL_TAX,(intTax + 1));
            contentValues.put(DatabaseContract.TableOrders.COL_TOTAL,intTotal);
            getContentResolver().update(DatabaseContract.ORDER_URI,
                    contentValues,
                    DatabaseContract.TableOrders.COL_ID+"=?"
                    ,new String[] {String.valueOf(cart.getOrder().getId())});

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(int type, String message) {
                    if(type == 0) {
                        //this method calls the onProgressUpdate
                        publishProgress(message);
                    }
                    else if(type == 1) {
                        Log.d(LOG_TAG,"ERROR TRYING TO SUBMIT ORDER");
                    }
                }

            }, staticHost, staticPort);

            Log.d(LOG_TAG, submitStat);
            mTcpClient.run(submitStat);
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.d("Server says","Server sent something back! " + values[0]);

            mTcpClient.stopClient();
            Intent intent = new Intent();
            intent.putExtra("valueBack",values[0]);
            intent.setClass(getApplicationContext(),OrderComplete.class);


            String pickup = (pickupTime.substring(pickupTime.length() - 5,pickupTime.length()));
            intent.putExtra(PICKUP_DATE,pickup);
            intent.putExtra("total",stringTotal);
            startActivity(intent);
            finishAffinity();


        }
        @Override
        protected void onPostExecute(TCPClient result) {
            super.onPostExecute(result);
            populateCart();
        }
    }
    private String jsonEvent() {
        JSONObject fullOb = new JSONObject();
        Date date = new Date();

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        transTime = String.valueOf(transFormat.format(date.getTime()));

        SimpleDateFormat pickupFormat = new SimpleDateFormat("yyyy-MM-dd");
        pickupTime = String.valueOf(pickupFormat.format(date.getTime()) + " " + pickupSpinner.getSelectedItem().toString());

        try {
            fullOb.put("name",textInputName);
            fullOb.put("transactionDate",transTime);
            fullOb.put("pickupDate",pickupTime);
            fullOb.put("subTotal",intSubTotal);
            fullOb.put("tax",(intTax +1));
            fullOb.put("total",(intTotal + 1));
            fullOb.put("paymentMethod",paymentSpinner.getSelectedItem().toString());
            fullOb.put("phoneNumber",textInputPhone);
            fullOb.put("email",textInputEmail);
            if(paymentSpinner.getSelectedItem().toString().equals("Credit card") && nonce != null && (!nonce.equals(""))) {
               fullOb.put("cardNumb",nonce);
            }
            else {
                fullOb.put("cardNumb", "");
            }

            JSONArray jsonItems = new JSONArray();

            for(int i = 0; i < itemList.size(); i++) {
                JSONObject jsonItem = new JSONObject();
                OrderItem orderItem = itemList.get(i);
                jsonItem.put("itemNumber",orderItem.getItemNumb());
                jsonItem.put("itemName",orderItem.getItemName());
                jsonItem.put("itemPrice",orderItem.getItemPrice());
                jsonItem.put("itemQty",orderItem.getQuantity());
                JSONObject itemKey = new JSONObject();
                itemKey.put("item",jsonItem);
                jsonItems.put(itemKey);
            }
            fullOb.put("items",jsonItems);


        }
        catch (JSONException e) {
            Log.e(LOG_TAG, "Error creating JSON Object: " + e.toString() );
        }
        return fullOb.toString();
    }
    public class getToken extends AsyncTask<String,String,TCPClient> {
        @Override
        protected TCPClient doInBackground(String... message) {

            Cursor prefCursor = getContentResolver().query(DatabaseContract.PREFERENCES_URI,
                    new String[]{DatabaseContract.TablePreferences.COL_PREF_VALUE},
                    "CAST (" + DatabaseContract.TablePreferences.COL_PREF_TYPE + " AS TEXT) = ?",
                    new String[] {"1"},
                    null
            );

            if(prefCursor != null && prefCursor.moveToLast()) {
                staticHost = prefCursor.getString(0);
            }
            Log.d(LOG_TAG,"About to launch a request for the token...");
            TCPClient client = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                public void messageReceived(int type, String message) {

                    clientToken = message;
                    Log.d(LOG_TAG,"token: " + clientToken);

                }
            },staticHost,tokenPort);
            client.requestToken("token");
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.d("Server says","Server sent something back! " + values[0]);

            mTcpClient.stopClient();
            Intent intent = new Intent();
            intent.putExtra("valueBack",values[0]);
            intent.setClass(getApplicationContext(),OrderComplete.class);
            intent.putExtra(PICKUP_DATE,pickupTime);
            startActivity(intent);
            finishAffinity();


        }
        @Override
        protected void onPostExecute(TCPClient result) {
            super.onPostExecute(result);
            //showTokenGot(clientToken);
        }
        }
    public class chargeNonce extends AsyncTask<String,String, TCPClient> {
        @Override
        protected TCPClient doInBackground(String... message) {

            Log.d(LOG_TAG,"Launcing request sending charge nonce");

            JSONObject paymentJSON = new JSONObject();
            try {
                paymentJSON.put("nonce", nonce);
                paymentJSON.put("total",(intTotal + 1));
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, "Error with paymentJSON: " + e.toString());
            }
            String sendPayment = paymentJSON.toString();

            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                public void messageReceived(int type, String message) {
                    paymentResponse = message;
                }
            },staticHost,tokenPort);
            mTcpClient.requestToken(sendPayment);
            return null;
        }
        @Override
        protected void onPostExecute(TCPClient result) {
            super.onPostExecute(result);
            Log.d(LOG_TAG, "payment response: " + paymentResponse);
            completeTrans();

        }
    }
}
