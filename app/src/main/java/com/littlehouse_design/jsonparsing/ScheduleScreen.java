package com.littlehouse_design.jsonparsing;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.littlehouse_design.jsonparsing.Utils.Adapters.ScheduleRecyclerAdapter;
import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Catalog;
import com.littlehouse_design.jsonparsing.Utils.Adapters.DividerScheduleDecoration;
import com.littlehouse_design.jsonparsing.Utils.QueryUtils;
import com.littlehouse_design.jsonparsing.Utils.LoaderManagers.ScheduleLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ScheduleScreen extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Catalog> {

    public static final String LOG_TAG = ScheduleScreen.class.getSimpleName();
    private ArrayList<String> childNames;
    private ArrayList<String> childCodes;
    private ArrayList<String> childImages;
    private ArrayList<Boolean> skipCategory;
    public Catalog topCat;
    private ListView catList;
    private String storeNumber = "898";
    private String myUrl = "http://store" + storeNumber + ".collegestoreonline.com/ePOS?form=shared3/json/merchandise/catlist.json&store=" + storeNumber + "&cat=Front%20Page&depth=3";
    private String jsonResponse;
    private static final String PARENT_CODE = "Parent";
    private static final String CODE = "Code";
    private static final String JSON = "JSON";
    private static final String CATALOG_NAME = "catalogName";
    private static final String STORE_NUMBER = "STORE NUMBER";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_screen);
        storeNumber = "898";
        final android.support.v7.app.ActionBar aBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar,null);
        android.support.v7.app.ActionBar.LayoutParams params = new android.support.v7.app.ActionBar.LayoutParams(//Center the textview in the ActionBar !
                android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT,
                android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textView = (TextView) viewActionBar.findViewById(R.id.tv_action_bar);
        textView.setText("Kondor Dining");
        aBar.setCustomView(viewActionBar,params);
        aBar.setDisplayShowCustomEnabled(true);
        aBar.setDisplayShowTitleEnabled(false);
        aBar.setDisplayHomeAsUpEnabled(false);

        //new LongOp().execute(myUrl);
        getLoaderManager().initLoader(1, null, this).forceLoad();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            ScheduleScreen.this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Catalog> onCreateLoader(int id, Bundle params) {
        Log.d(LOG_TAG,"onCreateLoader was called");

        return new ScheduleLoader(this,myUrl);
    }
    @Override
    public void onLoadFinished(Loader<Catalog> loader,Catalog catalog) {
        topCat = catalog;
        childNames = topCat.getChildNames();
        childCodes = topCat.getChildCodes();
        childImages = topCat.getChildImages();
        skipCategory = topCat.getSkipCategory();
        updateUI();
    }
    @Override
    public void onLoaderReset(Loader<Catalog> loader) {
        topCat = new Catalog();
        childNames = topCat.getChildNames();
        childCodes = topCat.getChildCodes();
        updateUI();
    }


    public void updateUI() {
        final RecyclerView cardRecyclerView = (RecyclerView) findViewById(R.id.rv_schedule_list);
        ScheduleRecyclerAdapter catRecyclerAdapter = new ScheduleRecyclerAdapter() {
            @Override
            public Catalog getItem(int position) {
                return new Catalog(childCodes.get(position),topCat.getCode(),childNames.get(position));
            }

            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, parent, false);
                return new MyHolder(view);
            }

            @Override
            public void onBindViewHolder(final ScheduleRecyclerAdapter.MyHolder holder, final int position) {
                final Catalog catalog = new Catalog(childCodes.get(position),topCat.getCode(),childNames.get(position));

                //QueryUtils.schedulesWithCats(jsonResponse,position);

                if(catalog.getName() != null) {
                } else {
                    Log.e(LOG_TAG, "No Cat name");
                }

                holder.title.setText(catalog.getName());
                holder.pickupTime.setText("Next available pickup: 12:00 pm");
                //holder.logo.setImageBitmap(QueryUtils.getBitmapFromURL("http://store" + storeNumber + ".collegestoreonline.com/webitemimages/" + storeNumber + "/catalog/" + childCodes.get(0) + "-ct.jpg)"));
                Log.d(LOG_TAG, "http://store" + storeNumber + ".collegestoreonline.com/webitemimages/" + storeNumber + "/catalog/" + childImages.get(position) + "-ct.jpg");
                Picasso.with(getApplicationContext()).load("http://store" + storeNumber + ".collegestoreonline.com/webitemimages/" + storeNumber + "/catalog/" + childImages.get(position) + "-ct.jpg").placeholder(R.drawable.no_image).into(holder.logo);

                holder.wrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent();
                        if(skipCategory.get(position)) {
                            intent.setClass(getApplicationContext(),SubCatList.class);
                            intent.putExtra(PARENT_CODE, childCodes.get(position));
                            intent.putExtra(CODE, childImages.get(position));
                            intent.putExtra(CATALOG_NAME,childNames.get(position));
                        } else {
                            intent.setClass(getApplicationContext(), CatalogList.class);
                            intent.putExtra(CODE,catalog.getCode());
                            intent.putExtra(CATALOG_NAME,catalog.getName());
                        }

                        intent.putExtra(STORE_NUMBER,storeNumber);
                        startActivity(intent);

                    }
                });

            }

            @Override
            public int getItemCount() {
                if(childCodes == null) {
                    return 0;
                } else {
                    return childCodes.size();
                }
            }
        };

        cardRecyclerView.setAdapter(catRecyclerAdapter);
        cardRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager cardLayoutManager = new LinearLayoutManager(getApplicationContext());
        cardRecyclerView.setLayoutManager(cardLayoutManager);
        cardRecyclerView.addItemDecoration(new DividerScheduleDecoration(getApplicationContext()));

    }

    private class LongOp extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... url) {
            return QueryUtils.getScheduleImages(url[0]);

        }
        @Override
        protected void onPostExecute(ArrayList result) {

        }
    }


}
