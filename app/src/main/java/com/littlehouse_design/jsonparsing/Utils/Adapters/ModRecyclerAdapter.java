package com.littlehouse_design.jsonparsing.Utils.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.littlehouse_design.jsonparsing.R;
import com.littlehouse_design.jsonparsing.Utils.Cart.OrderMod;

import java.util.ArrayList;

/**
 * Created by johnkonderla on 2/14/17.
 */

public abstract class ModRecyclerAdapter extends RecyclerView.Adapter<ModRecyclerAdapter.ModHolder>{
    private ArrayList<OrderMod> mods;
    private Context context;
    private int status;

    public class ModHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView modName;
        public TextView modPrice;

        public ModHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.cv_mod_card);
            modName = (TextView) view.findViewById(R.id.tv_mod_item_name);
            modPrice = (TextView) view.findViewById(R.id.tv_mod_item_price);

        }
        public int getStatus() {
            if(status == 1) {
                status = 0;
            } else {
                status++;
            }
            return status;
        }
    }
    public abstract OrderMod getItem(int position);
}
