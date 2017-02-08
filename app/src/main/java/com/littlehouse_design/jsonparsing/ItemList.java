package com.littlehouse_design.jsonparsing;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.littlehouse_design.jsonparsing.Utils.Cart.Cart;
import com.littlehouse_design.jsonparsing.Utils.Cart.OrderItem;
import com.littlehouse_design.jsonparsing.Utils.DataBase.AddItemService;
import com.littlehouse_design.jsonparsing.Utils.DataBase.DatabaseContract;
import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Item;
import com.littlehouse_design.jsonparsing.Utils.LoaderManagers.ItemLoader;
import com.littlehouse_design.jsonparsing.Utils.Adapters.ItemRecyclerAdapter;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Locale;

public class ItemList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Item>> {
    public static final String LOG_TAG = ItemList.class.getSimpleName();

    private String storeNumber = "898";
    private String myUrl = "http://store" + storeNumber + ".collegestoreonline.com/ePOS?form=shared3/json/merchandise/merchlist.json&store=" + storeNumber + "&qty=100&listKey=";
    private String schedCode;
    private String catCode;
    private String subCatCode;
    private static final String SUBCATALOG_NAME = "subCatName";
    private String subCatName;
    private ArrayList<Item> itemArrayList;
    private Cursor orderCursor;
    private Cursor itemCursor;
    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_list);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            schedCode = intent.getStringExtra("SCHED");
            catCode = intent.getStringExtra("CAT");
            subCatCode = intent.getStringExtra("SUBCAT");
            subCatName = intent.getStringExtra(SUBCATALOG_NAME);
            Log.d(LOG_TAG, "subCatCode: " + subCatCode +
                    " catCode: " + catCode + " schedCode: " + schedCode);
        } else {
            Log.d(LOG_TAG, "My intent was empty?");
        }
        final ActionBar aBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar,null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textView = (TextView) viewActionBar.findViewById(R.id.tv_action_bar);
        textView.setText(subCatName);
        aBar.setCustomView(viewActionBar,params);
        aBar.setDisplayShowCustomEnabled(true);
        aBar.setDisplayShowTitleEnabled(false);
        aBar.setDisplayHomeAsUpEnabled(true);

        new CartBuilder().execute();
        getLoaderManager().initLoader(3,null,this).forceLoad();
    }

    @Override
    public Loader<ArrayList<Item>> onCreateLoader(int id, Bundle params) {
        Log.d(LOG_TAG, "onCreateLoader was called");
        Log.d(LOG_TAG, myUrl + subCatCode);
        return new ItemLoader(this,myUrl + subCatCode);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Item>> loader, ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
        updateUI();
    }
    @Override
    public void onLoaderReset(Loader<ArrayList<Item>> loader) {
        itemArrayList = new ArrayList<>();
        updateUI();
    }

    public void updateUI() {
        final RecyclerView cardRecyclerView = (RecyclerView) findViewById(R.id.rv_catalog_list);

        ItemRecyclerAdapter itemRecyclerAdapter = new ItemRecyclerAdapter() {
            @Override
            public Item getItem(int position) {
                return new Item(itemArrayList.get(position).getItemNumber(),itemArrayList.get(position).getItemDescriptor(),itemArrayList.get(position).getPrice());
            }
            @Override
            public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_item, parent, false);
                return new ItemHolder(view);
            }
            @Override
            public void onBindViewHolder(final ItemRecyclerAdapter.ItemHolder holder, final int position) {
                final Item item = itemArrayList.get(position);
                if (item.getItemDescriptor() != null) {
                    Log.d(LOG_TAG, "Got a item name! " + item.getItemDescriptor());
                } else {
                    Log.e(LOG_TAG, "No item name");
                }
                final String stringPrice = "$" + String.format(Locale.US,"%.2f",item.getPrice());
                if(stringPrice.length() > 6) {
                    holder.itemPrice.setTextSize(12);
                }
                holder.itemWrap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            MyDialogFragment frag = new MyDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("name",item.getItemDescriptor());

                            args.putString("price",stringPrice);

                            args.putString("descriptor",item.getItemNumber());

                            frag.setArguments(args);
                            frag.show(ft,LOG_TAG);

                        }
                    });
                Picasso.with(getApplicationContext()).load("http://store898.collegestoreonline.com/webitemimages/898/" + item.getItemNumber() + ".jpg").placeholder(R.drawable.no_image).into(holder.itemImage);
                Log.d(LOG_TAG, stringPrice);
                holder.itemPrice.setText(stringPrice);
                holder.itemTitle.setText(item.getItemDescriptor());
                holder.itemAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DatabaseContract.TableOrderItems.COL_ORDER_ID,cart.getOrder().getId());
                            Log.d(LOG_TAG,"orderId " + cart.getOrder().getId());
                            contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_NAME,item.getItemDescriptor());
                            contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_NUMBER,item.getItemNumber());
                            contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_PRICE, ((int) (item.getPrice() * 100)));
                            Log.d(LOG_TAG,"Attempting to add this item to the cart: " + item.getItemNumber());
                            AddItemService.insertNewItem(getApplicationContext(),contentValues);
                            cart.getOrderItems().add(new OrderItem(item));
                            cart.getOrderItems().get(cart.getOrderItems().size() - 1).setItemPrice((int) (item.getPrice() * 100));
                            showCart();
                        }
                    });
            }
            @Override
            public int getItemCount() {
                    return itemArrayList.size();
                }
            };
            cardRecyclerView.setAdapter(itemRecyclerAdapter);
            cardRecyclerView.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
            cardRecyclerView.setLayoutManager(gridLayoutManager);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
        }



    public static class MyDialogFragment extends DialogFragment {

        private String itemName;
        private String itemDescriptor;
        private String stringPrice;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            itemName = getArguments().getString("name");
            itemDescriptor = getArguments().getString("descriptor");
            stringPrice = getArguments().getString("price");
            setStyle(DialogFragment.STYLE_NORMAL, 0);
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog d = getDialog();
            if (d != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                d.getWindow().setLayout(width, height);
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.item_detail_frag, container, false);
            TextView itemTitle = (TextView) root.findViewById(R.id.tv_item_detail_title);
            itemTitle.setText(itemName);
            TextView itemDescription = (TextView) root.findViewById(R.id.tv_item_detail_description);
            itemDescription.setText(itemDescriptor);
            TextView itemPrice = (TextView) root.findViewById(R.id.tv_item_detail_price);
            itemPrice.setText(stringPrice);
            ImageView closeButton = (ImageView) root.findViewById(R.id.iv_item_detail_close);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

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
                            Log.d(LOG_TAG, "Failed to get order items");
                        }
                    }
                    catch (SQLException e) {
                        Log.e(LOG_TAG,"Error building the items part of my order: " + e.toString());
                    }
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseContract.TableOrders.COL_STATUS,0);
                    getContentResolver().insert(DatabaseContract.ORDER_URI,contentValues);
                    Log.d(LOG_TAG, "tried inserting an order row");

                    orderCursor = getContentResolver().query(DatabaseContract.ORDER_URI,
                            new String[]{DatabaseContract.TableOrders.COL_ID,
                                    DatabaseContract.TableOrders.COL_SUB_TOTAL},
                            "CAST (" + DatabaseContract.TableOrders.COL_STATUS + " AS TEXT) = ?",
                            new String[]{"0"},
                            null
                    );
                    if(orderCursor != null && orderCursor.moveToFirst()) {
                        cart = new Cart(orderCursor);

                        orderCursor.close();

                        Log.d(LOG_TAG,"Got a NEW order: " + cart.getOrder().getId());

                    }
                    else {
                        Log.d(LOG_TAG,"Order cursor failed some more..");
                    }


                }

            }
            catch (SQLException e) {
                Log.e(LOG_TAG, "Error building the orders for my cart: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voidOb) {
            super.onPostExecute(voidOb);
            showCart();
        }

    }
    private void showCart() {
        if(cart != null && cart.getOrderItems() != null) {
            ArrayList<OrderItem> orderItems = cart.getOrderItems();

            if (orderItems.size() > 0) {

                Log.d(LOG_TAG, "The order had items!");

                int newSub = 0;
                for (int i = 0; i < orderItems.size(); i++) {
                    int sub = orderItems.get(i).getItemPrice();
                    Log.d(LOG_TAG, "Item: " + orderItems.get(i).getItemNumb() + " Price: " + Integer.toString(orderItems.get(i).getItemPrice()));
                    newSub = sub + newSub;
                    Log.d(LOG_TAG, Integer.toString(newSub));
                }


                // Create the Snackbar
                if (getCurrentFocus() != null) {
                    Snackbar snackbar = Snackbar.make(getCurrentFocus(), "", Snackbar.LENGTH_INDEFINITE);
                    // Get the Snackbar's layout view
                    Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                    // Hide the text
                    TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setVisibility(View.INVISIBLE);

                    // Inflate our custom view
                    View snackView = snackbar.getView().inflate(getApplicationContext(), R.layout.snackbar_cart, null);
                    TextView textViewTop = (TextView) snackView.findViewById(R.id.tv_sub_total);
                    float subDisplay = (float) newSub / 100;

                    final String stringTotal = "$" + String.format(Locale.US, "%.2f", subDisplay);
                    if (stringTotal.length() > 6) {
                        textViewTop.setTextSize(24);
                    }
                    textViewTop.setText(stringTotal);
                    TextView cartCount = (TextView) snackView.findViewById(R.id.tv_cart_count);
                    if (orderItems.size() > 0) {
                        cartCount.setText(Integer.toString(orderItems.size()));
                    }
                    textViewTop.setTextColor(Color.WHITE);

                    RelativeLayout cartWrapper = (RelativeLayout) snackView.findViewById(R.id.rl_cart_wraper);
                    cartWrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), CartScreen.class);
                            startActivity(intent);
                        }
                    });

                    // Add the view to the Snackbar's layout
                    layout.addView(snackView, 0);
                    RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.activity_catalog_list);
                    myLayout.setPadding(0, 0, 0, 160);
                    // Show the Snackbar
                    snackbar.show();
                }
            }
            else {

                Log.d(LOG_TAG,"My current focus was null..");
                //showCart();
            }
        }
        else {
            Log.d(LOG_TAG, "There were no items added to the order yet...");
        }

    }
}
