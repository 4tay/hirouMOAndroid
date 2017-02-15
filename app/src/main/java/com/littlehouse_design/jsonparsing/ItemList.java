package com.littlehouse_design.jsonparsing;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.VoiceInteractor;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.littlehouse_design.jsonparsing.Utils.Adapters.ModRecyclerAdapter;
import com.littlehouse_design.jsonparsing.Utils.Cart.Cart;
import com.littlehouse_design.jsonparsing.Utils.Cart.OrderItem;
import com.littlehouse_design.jsonparsing.Utils.Cart.OrderMod;
import com.littlehouse_design.jsonparsing.Utils.DataBase.AddItemService;
import com.littlehouse_design.jsonparsing.Utils.DataBase.DatabaseContract;
import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Item;
import com.littlehouse_design.jsonparsing.Utils.DataBase.OrdersProvider;
import com.littlehouse_design.jsonparsing.Utils.LoaderManagers.ItemLoader;
import com.littlehouse_design.jsonparsing.Utils.Adapters.ItemRecyclerAdapter;

import com.littlehouse_design.jsonparsing.Utils.QueryUtils;
import com.littlehouse_design.jsonparsing.Utils.TCPClient;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Locale;

public class ItemList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Item>> {
    public static final String LOG_TAG = ItemList.class.getSimpleName();

    private String storeNumber = "898";
    private String myUrl = "http://store" + storeNumber + ".collegestoreonline.com/ePOS?form=shared3/json/merchandise/merchlist.json&store=" + storeNumber + "&qty=100&listKey=";
    private String schedCode;
    private String catCode;
    private static String subCatCode;
    private static final String SUBCATALOG_NAME = "subCatName";
    private String subCatName;
    private ArrayList<Item> itemArrayList;
    private static Cursor orderCursor;
    private static Cursor itemCursor;
    private static Cart cart;
    private static String staticHost;
    private static ArrayList<OrderMod> modArray;
    private TCPClient tcpClient;

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
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textView = (TextView) viewActionBar.findViewById(R.id.tv_action_bar);
        textView.setText(subCatName);
        aBar.setCustomView(viewActionBar, params);
        aBar.setDisplayShowCustomEnabled(true);
        aBar.setDisplayShowTitleEnabled(false);
        aBar.setDisplayHomeAsUpEnabled(true);


        //Moved CartBuilder to onResume() so that when navigating back the cart is updated to current status.
        //new CartBuilder().execute();
        getLoaderManager().initLoader(3, null, this).forceLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "was onResume called???");

        if(orderCursor != null) {
            if(orderCursor.isClosed()) {
                Log.d(LOG_TAG,"My cursor was not null but closed?");
                orderCursor = null;
                itemCursor = null;
                new CartBuilder().execute();
            } else {
                Log.d(LOG_TAG,"orderCursor was not null");
                new CartBuilder().execute();
            }
        } else {
            new CartBuilder().execute();
        }
    }

    @Override
    public Loader<ArrayList<Item>> onCreateLoader(int id, Bundle params) {
        Log.d(LOG_TAG, "onCreateLoader was called");
        Log.d(LOG_TAG, myUrl + subCatCode);
        return new ItemLoader(this, myUrl + subCatCode);
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
                return new Item(itemArrayList.get(position).getItemNumber(), itemArrayList.get(position).getItemDescriptor(), itemArrayList.get(position).getPrice());
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
                final String stringPrice = "$" + String.format(Locale.US, "%.2f", item.getPrice());
                if (stringPrice.length() > 6) {
                    holder.itemPrice.setTextSize(12);
                }
                holder.fullWrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MyDialogCloseListener closeListener = new MyDialogCloseListener() {
                            @Override
                            public void handleDialogClose(DialogInterface dialog) {
                                //new CartBuilder().execute();
                            }
                        };

                        Bundle args = new Bundle();
                        args.putString("name", item.getItemDescriptor());

                        args.putString("price", stringPrice);

                        args.putFloat("floatPrice",item.getPrice());

                        args.putString("descriptor", item.getItemNumber());

                        new GetSugNMod().execute(args);
                        /*FragmentTransaction ft = getFragmentManager().beginTransaction();
                        MyDialogFragment frag = new MyDialogFragment();
                        frag.setArguments(args);
                        frag.show(ft, LOG_TAG);*/

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
                        contentValues.put(DatabaseContract.TableOrderItems.COL_ORDER_ID, cart.getOrder().getId());
                        Log.d(LOG_TAG, "orderId " + cart.getOrder().getId());
                        contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_NAME, item.getItemDescriptor());
                        contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_NUMBER, item.getItemNumber());
                        contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_PRICE, ((int) (item.getPrice() * 100)));
                        Log.d(LOG_TAG, "Attempting to add this item to the cart: " + item.getItemNumber());
                        AddItemService.insertNewItem(getApplicationContext(), contentValues);
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        cardRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
    }


    public static class MyDialogFragment extends DialogFragment  {

        private String itemName;
        private String itemDescriptor;
        private String stringPrice;
        private float floatPrice;
        private Context activityContext;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            itemName = getArguments().getString("name");
            itemDescriptor = getArguments().getString("descriptor");
            stringPrice = getArguments().getString("price");
            floatPrice = getArguments().getFloat("floatPrice");
            setStyle(DialogFragment.STYLE_NORMAL, 0);

            activityContext = getActivity();
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog d = getDialog();
            if (d != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                d.getWindow().setLayout(width, height);
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.item_detail_frag, container, false);
            root.setForegroundGravity(Gravity.END);
            TextView itemTitle = (TextView) root.findViewById(R.id.tv_item_detail_title);
            itemTitle.setText(itemName);
            TextView itemDescription = (TextView) root.findViewById(R.id.tv_item_detail_description);
            //itemDescription.setText(itemDescriptor);
            TextView itemPrice = (TextView) root.findViewById(R.id.tv_item_detail_price);
            itemPrice.setText(stringPrice);
            ImageView closeButton = (ImageView) root.findViewById(R.id.iv_item_detail_close);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            TextView addToCart = (TextView) root.findViewById(R.id.tv_item_frag_add);
            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item item = new Item(itemDescriptor,itemName,floatPrice);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseContract.TableOrderItems.COL_ORDER_ID, cart.getOrder().getId());
                    Log.d(LOG_TAG, "orderId " + cart.getOrder().getId());
                    contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_NAME, item.getItemDescriptor());
                    contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_NUMBER, item.getItemNumber());
                    contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_PRICE, ((int) (item.getPrice() * 100)));
                    Log.d(LOG_TAG, "Attempting to add this item to the cart: " + item.getItemNumber());

                    //AddItemService.insertNewItem(activityContext, contentValues);

                    //Probably handling this poorly BUT if I insert an item via the service, it won't be available soon
                    //enough for me to query against it to load the cart??

                    getContext().getContentResolver().insert(DatabaseContract.ORDER_ITEM_URI,contentValues);
                    /*cart.getOrderItems().add(new OrderItem(item));
                    cart.getOrderItems().get(cart.getOrderItems().size() - 1).setItemPrice((int) (item.getPrice() * 100));*/



                    Intent intent = new Intent();


                    intent.setClass(activityContext, ItemList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    intent.putExtra("SUBCAT",subCatCode);
                    subCatCode = intent.getStringExtra("SUBCAT");


                    startActivity(intent);

                    //activityContext.onRefresh();

                    dismiss();
                }
            });
            addToCart.setBackgroundResource(R.drawable.rounded_corners_3);
            TextView checkout = (TextView) root.findViewById(R.id.tv_item_frag_checkout);
            checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Item item = new Item(itemDescriptor,itemName,floatPrice);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseContract.TableOrderItems.COL_ORDER_ID, cart.getOrder().getId());
                    Log.d(LOG_TAG, "orderId " + cart.getOrder().getId());
                    contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_NAME, item.getItemDescriptor());
                    contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_NUMBER, item.getItemNumber());
                    contentValues.put(DatabaseContract.TableOrderItems.COL_ITEM_PRICE, ((int) (item.getPrice() * 100)));
                    Log.d(LOG_TAG, "Attempting to add this item to the cart: " + item.getItemNumber());

                    AddItemService.insertNewItem(activityContext, contentValues);
                    cart.getOrderItems().add(new OrderItem(item));
                    cart.getOrderItems().get(cart.getOrderItems().size() - 1).setItemPrice((int) (item.getPrice() * 100));

                    //orderCursor.close();
                    //itemCursor.close();

                    Intent intent = new Intent();
                    intent.setClass(activityContext, CartScreen.class);
                    startActivity(intent);

                }
            });
            checkout.setBackgroundResource(R.drawable.rounded_corners_3);
            final RecyclerView modRecyclerView = (RecyclerView) root.findViewById(R.id.rv_item_frag_mod);

            final ModRecyclerAdapter modRecyclerAdapter = new ModRecyclerAdapter() {
                @Override
                public OrderMod getItem(int position) {

                    return new OrderMod(modArray.get(position).getName(),modArray.get(position).getPrice());
                }

                @Override
                public ModHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modifier_list_item,parent,false);

                    return new ModHolder(view);
                }

                @Override
                public void onBindViewHolder(final ModHolder holder, int position) {

                    holder.modName.setText(modArray.get(position).getName());
                    holder.cardView.setBackgroundResource(R.drawable.rounded_corners);
                    String floatModPrice = String.format("%.2f",modArray.get(position).getPrice());
                    holder.modPrice.setText("$"+floatModPrice);

                    holder.cardView.setOnClickListener(new View.OnClickListener() {
                        int status = 0;
                        @Override
                        public void onClick(View v) {
                            Log.d(LOG_TAG,"Click!");
                            //GradientDrawable modBackground = (GradientDrawable) holder.cardView.getBackground();
                            if(status == 0) {
                                //Log.d(LOG_TAG,Integer.toString(holder.getStatus()));
                                holder.cardView.setBackgroundResource(R.drawable.rounded_corners_2);
                                status++;
                            } else {
                                holder.cardView.setBackgroundResource(R.drawable.rounded_corners);
                                status = 0;
                            }

                        }
                    });

                }

                @Override
                public int getItemCount() {

                    if(modArray != null && modArray.size() > 0) {
                        return modArray.size();
                    } else {
                        return 0;
                    }
                }
            };
            modRecyclerView.setAdapter(modRecyclerAdapter);
            modRecyclerView.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(activityContext,2);
            modRecyclerView.setLayoutManager(gridLayoutManager);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });

            return root;
        }
        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if(closeListener!=null) {
                closeListener.handleDialogClose(null);
            }

        }
    }

    static MyDialogCloseListener closeListener = new MyDialogCloseListener() {
        @Override
        public void handleDialogClose(DialogInterface dialog) {

            //do here whatever you want to do on Dialog dismiss
            Log.d(LOG_TAG,"Closed Frag");
        }
    };




    private class CartBuilder extends AsyncTask<Context, Void, Void> {
        @Override
        protected Void doInBackground(Context... contexts) {


            //if(orderCursor != null && !orderCursor.isClosed()){
                /*orderCursor = getContentResolver().query(DatabaseContract.ORDER_URI,
                        new String[]{DatabaseContract.TableOrders.COL_ID,
                                DatabaseContract.TableOrders.COL_SUB_TOTAL},
                        "CAST (" + DatabaseContract.TableOrders.COL_STATUS + " AS TEXT) = ?",
                        new String[]{"0"},
                        null
                );*/
            //}
            try {
                orderCursor = getContentResolver().query(DatabaseContract.ORDER_URI,
                        new String[]{DatabaseContract.TableOrders.COL_ID,
                                DatabaseContract.TableOrders.COL_SUB_TOTAL},
                        "CAST (" + DatabaseContract.TableOrders.COL_STATUS + " AS TEXT) = ?",
                        new String[]{"0"},
                        null
                );
                if (orderCursor != null && orderCursor.moveToLast()) {

                    Log.d(LOG_TAG, "I got an order!!");
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

                        } else {
                            Log.d(LOG_TAG, "Failed to get order items");
                        }
                    } catch (SQLException e) {
                        Log.e(LOG_TAG, "Error building the items part of my order: " + e.toString());
                    }
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseContract.TableOrders.COL_STATUS, 0);
                    getContentResolver().insert(DatabaseContract.ORDER_URI, contentValues);
                    Log.d(LOG_TAG, "tried inserting an order row");

                    orderCursor = getContentResolver().query(DatabaseContract.ORDER_URI,
                            new String[]{DatabaseContract.TableOrders.COL_ID,
                                    DatabaseContract.TableOrders.COL_SUB_TOTAL},
                            "CAST (" + DatabaseContract.TableOrders.COL_STATUS + " AS TEXT) = ?",
                            new String[]{"0"},
                            null
                    );
                    if (orderCursor != null && orderCursor.moveToFirst()) {
                        cart = new Cart(orderCursor);

                        Log.d(LOG_TAG, "Got a NEW order: " + cart.getOrder().getId());

                    } else {
                        Log.d(LOG_TAG, "Order cursor failed some more..");
                    }


                }

            } catch (SQLException e) {
                Log.e(LOG_TAG, "Error building the orders for my cart: " + e.toString());
            }
            if(orderCursor != null && itemCursor != null) {
                Log.d(LOG_TAG,"Finished my looping!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voidOb) {
            super.onPostExecute(voidOb);
            showCart();
        }

    }

    private class GetSugNMod extends AsyncTask<Bundle,String,Void> {

        Bundle bundled;
        String servResponse;
        FragmentTransaction ft;
        ItemList.LoaderFrag loaderFrag;
        @Override
        protected Void doInBackground(Bundle... args) {

            //Create fragment to hold my loading bar
            ft = getFragmentManager().beginTransaction();
            loaderFrag = new ItemList.LoaderFrag();
            //Show my fragment for loading
            loaderFrag.show(ft, LOG_TAG);

            bundled = args[0];
            Cursor prefCursor = getContentResolver().query(DatabaseContract.PREFERENCES_URI,
                    new String[]{DatabaseContract.TablePreferences.COL_PREF_VALUE},
                    "CAST (" + DatabaseContract.TablePreferences.COL_PREF_TYPE + " AS TEXT) = ?",
                    new String[] {"1"},
                    null
            );

            if(prefCursor != null && prefCursor.moveToLast()) {
                staticHost = prefCursor.getString(0);
            }
            if(prefCursor != null) {
                prefCursor.close();
            }

            String itemNumber = bundled.getString("descriptor");

            Log.d(LOG_TAG, "Asking the server for " + itemNumber);
            //servResponse = "{\"modifiers\":[{\"mod\":{\"name\":\"addVanillaShot\",\"price\":0.50}},{\"mod\":{\"name\":\"addHazelnutShot\",\"price\":0.50}},{\"mod\":{\"name\":\"addCaramelShot\",\"price\":0.50}},{\"mod\":{\"name\":\"addWhippedCream\",\"price\":0.75}}]}";

            if(tcpClient != null) {
                tcpClient.stopClient();
            }

            tcpClient = new TCPClient (new TCPClient.OnMessageReceived() {
                @Override
                public void messageReceived(int type, String message) {
                    Log.d(LOG_TAG,message);
                    servResponse = message;


                }
            },staticHost,4000);

            tcpClient.run(itemNumber);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Void voidOb) {
            super.onPostExecute(voidOb);

            if(servResponse.contains("modifiers")) {
                Log.d(LOG_TAG,"it was Modifiers!!");

                modArray = QueryUtils.GetModifiers(servResponse);


            }
            else if (servResponse.contains("suggested")) {
                Log.d(LOG_TAG,"it was Suggested!!");
            }
            else {
                Log.d(LOG_TAG,"it was something else?? " + servResponse );
            }
            loaderFrag.dismiss();

            //Toast.makeText(getApplicationContext(),servResponse,Toast.LENGTH_LONG).show();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            MyDialogFragment frag = new MyDialogFragment();
            frag.setArguments(bundled);
            frag.show(ft, LOG_TAG);


        }
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



    private void showCart() {
        if (cart != null && cart.getOrderItems() != null) {
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
                            if(itemCursor != null && orderCursor != null) {
                                itemCursor.close();
                                orderCursor.close();
                            }

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
            } else if (orderItems.size() == 0) {
                Log.d(LOG_TAG,"No items for this order");
                if (getCurrentFocus() != null) {
                    Snackbar snackbar = Snackbar.make(getCurrentFocus(), "", Snackbar.LENGTH_SHORT);

                    // Get the Snackbar's layout view
                    Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                    // Hide the text
                    TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setVisibility(View.INVISIBLE);

                    // Inflate our custom view
                    View snackView = snackbar.getView().inflate(getApplicationContext(), R.layout.snackbar_cart, null);
                    TextView textViewTop = (TextView) snackView.findViewById(R.id.tv_sub_total);
                    //float subDisplay = (float) newSub / 100;

                    //final String stringTotal = "$" + String.format(Locale.US, "%.2f", subDisplay);
                    /*if (stringTotal.length() > 6) {
                        textViewTop.setTextSize(24);
                    }
                    textViewTop.setText(stringTotal);*//**//*
                    TextView cartCount = (TextView) snackView.findViewById(R.id.tv_cart_count);
                    if (orderItems.size() > 0) {
                        cartCount.setText(Integer.toString(orderItems.size()));
                    }
                    textViewTop.setTextColor(Color.WHITE);*/

                    RelativeLayout cartWrapper = (RelativeLayout) snackView.findViewById(R.id.rl_cart_wraper);
                    cartWrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), CartScreen.class);
                            if(itemCursor != null && orderCursor != null) {
                                itemCursor.close();
                                orderCursor.close();
                            }
                            startActivity(intent);
                        }
                    });

                    // Add the view to the Snackbar's layout
                    layout.addView(snackView, 0);
                    RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.activity_catalog_list);
                    myLayout.setPadding(0, 0, 0, 0);
                    // Show the Snackbar
                    snackbar.show();
                    snackbar.getView().setVisibility(View.INVISIBLE);
                } else {

                    Log.d(LOG_TAG, "My current focus was null..");
                    //showCart();
                }

            } else {
                Log.d(LOG_TAG, "There were no items added to the order yet...");
            }

        }
    }
}
