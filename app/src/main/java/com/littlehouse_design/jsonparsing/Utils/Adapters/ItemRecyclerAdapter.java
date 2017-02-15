package com.littlehouse_design.jsonparsing.Utils.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.littlehouse_design.jsonparsing.R;
import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Item;

import java.util.ArrayList;

/**
 * Created by johnkonderla on 1/7/17.
 */

public abstract class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemHolder> {
    private ArrayList<Item> itemList;
    private Context context;

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout itemWrap;
        public ImageView itemImage;
        public TextView itemPrice;
        public TextView itemTitle;
        public ImageView itemAdd;
        public LinearLayout fullWrap;


        public ItemHolder(View view) {
            super(view);
            itemWrap = (RelativeLayout) view.findViewById(R.id.rv_image_wrap);
            itemImage = (ImageView) view.findViewById(R.id.iv_item_image);
            itemPrice = (TextView) view.findViewById(R.id.tv_item_price);
            itemTitle = (TextView) view.findViewById(R.id.tv_item_title);
            itemAdd = (ImageView) view.findViewById(R.id.iv_add_item);
            fullWrap = (LinearLayout) view.findViewById(R.id.ll_item_wrapper);
        }
        @Override
        public void onClick(View view) {

        }
    }
    public abstract Item getItem(int position);
}
