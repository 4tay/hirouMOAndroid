package com.littlehouse_design.jsonparsing;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.littlehouse_design.jsonparsing.Utils.Adapters.CatRecyclerAdapter;
import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Catalog;
import com.littlehouse_design.jsonparsing.Utils.LoaderManagers.CatalogLoader;
import com.littlehouse_design.jsonparsing.Utils.Adapters.DividerScheduleDecoration;
import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by johnkonderla on 12/26/16.
 */

public class SubCatList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Catalog>{

    public static final String LOG_TAG = SubCatList.class.getSimpleName();
    private ArrayList<String> catNames;
    private ArrayList<String> catCodes;
    //private ArrayList<Catalog> scheduleArray;

    public Catalog topCat;
    private ListView catList;
    private String storeNumber = "898";
    private String myUrl = "http://store" + storeNumber + ".collegestoreonline.com/ePOS?form=shared3/json/merchandise/catlist.json&store=" + storeNumber + "&cat=Front%20Page&depth=3";
    private static final String PARENT_CODE = "Parent";
    private static final String CODE = "Code";
    private static final String JSON = "JSON";
    private static final String LEVEL = "Level";
    private static final String CATALOG_NAME = "catalogName";
    private Catalog schedule;
    private String response;
    private String parent;
    private String catCode;
    private int levelDepth;
    private String catName;
    private String itemList;
    private static final String SUBCATALOG_NAME = "subCatName";
    private String subCatName;
    private ArrayList<Item> itemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_list);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            response = intent.getStringExtra(JSON);
            parent = intent.getStringExtra(PARENT_CODE);
            catCode = intent.getStringExtra(CODE);
            catName = intent.getStringExtra(CATALOG_NAME);
            levelDepth = intent.getIntExtra(LEVEL, 0);
            Log.d(LOG_TAG, "This is my level depth: " + Integer.toString(levelDepth));
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
        textView.setText(catName);
        aBar.setCustomView(viewActionBar,params);
        aBar.setDisplayShowCustomEnabled(true);
        aBar.setDisplayShowTitleEnabled(false);
        aBar.setDisplayHomeAsUpEnabled(true);

        getLoaderManager().initLoader(3, null, this).forceLoad();
        //new LongOp().execute();
    }

    @Override
    public Loader<Catalog> onCreateLoader(int id, Bundle params) {
        Log.d(LOG_TAG,"onCreateLoader was called");

        return new CatalogLoader(this,myUrl, parent,catCode,2);
    }
    @Override
    public void onLoadFinished(Loader<Catalog> loader,Catalog catalog) {
        schedule = catalog;
        Log.d(LOG_TAG, "Using 3 variable method");
        Log.d(LOG_TAG, "With a parent code of this: " + parent + " and catCode of this: " + catCode);
        catCodes = schedule.getChildCodes();
        catNames = schedule.getChildNames();
        updateUI();
    }
    @Override
    public void onLoaderReset(Loader<Catalog> loader) {
        schedule = new Catalog();
    }

    public void updateUI(){
        final RecyclerView cardRecyclerView = (RecyclerView) findViewById(R.id.rv_catalog_list);
        CatRecyclerAdapter catRecyclerAdapter = new CatRecyclerAdapter() {
            @Override
            public Catalog getItem(int position) {
                return new Catalog(catCodes.get(position), schedule.getCode(), catNames.get(position), schedule.getLevel() + 1);
            }

            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, parent, false);
                return new MyHolder(view);
            }

            @Override
            public void onBindViewHolder(final CatRecyclerAdapter.MyHolder holder, final int position) {
                Log.d(LOG_TAG, catNames.get(position));
                final Catalog catalog = new Catalog(catCodes.get(position), schedule.getParentCode(), catNames.get(position));


                if (catalog.getName() != null) {
                    Log.d(LOG_TAG, "Got a cat name: " + catalog.getName());
                } else {
                    Log.e(LOG_TAG, "No Cat name");
                }

                holder.title.setText(catalog.getName());
                Picasso.with(getApplicationContext()).load("http://store" + storeNumber + ".collegestoreonline.com/webitemimages/" + storeNumber + "/catalog/" + catCodes.get(position) + "-ct.jpg").placeholder(R.drawable.no_image).into(holder.logo);
                Log.d(LOG_TAG, "http://store" + storeNumber + ".collegestoreonline.com/webitemimages/" + storeNumber + "/catalog/" + catCodes.get(position) + "-ct.jpg");
                holder.wrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), ItemList.class);
                        intent.putExtra("SCHED", schedule.getParentCode());
                        intent.putExtra("CAT", schedule.getCode());
                        intent.putExtra("SUBCAT", catalog.getCode());
                        intent.putExtra(JSON, response);
                        intent.putExtra(SUBCATALOG_NAME,catalog.getName());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public int getItemCount() {
                if(catNames == null) {
                    return 0;
                } else {
                    return catNames.size();
                }
            }
        };

        cardRecyclerView.setAdapter(catRecyclerAdapter);
        cardRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager cardLayoutManager = new LinearLayoutManager(getApplicationContext());
        cardRecyclerView.setLayoutManager(cardLayoutManager);
        cardRecyclerView.addItemDecoration(new DividerScheduleDecoration(getApplicationContext()));
    }
}
