package com.littlehouse_design.jsonparsing.Utils.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlehouse_design.jsonparsing.R;

/**
 * Created by johnkonderla on 1/15/17.
 */

public abstract class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.ItemHolder>{

    public class ItemHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemName;
        public TextView itemDescription;
        public TextView itemQty;
        public TextView itemPrice;
        public LinearLayout itemWrapper;

        public ItemHolder(View view) {
            super(view);
            itemWrapper = (LinearLayout) view.findViewById(R.id.ll_cart_item_wrap); 
            itemImage = (ImageView) view.findViewById(R.id.iv_cart_item_image);
            itemName = (TextView) view.findViewById(R.id.tv_cart_item_name);
            itemDescription = (TextView) view.findViewById(R.id.tv_cart_item_description);
            itemQty = (TextView) view.findViewById(R.id.tv_cart_item_qty);
            itemPrice = (TextView) view.findViewById(R.id.tv_cart_item_price);


        }
        
        public View getSwipableView() {
            return itemWrapper;
            
        }
    }
}
