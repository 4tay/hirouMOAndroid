package com.littlehouse_design.jsonparsing.Utils.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlehouse_design.jsonparsing.R;
import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Catalog;

/**
 * Created by johnkonderla on 12/24/16.
 */

public abstract class CatRecyclerAdapter extends RecyclerView.Adapter<CatRecyclerAdapter.MyHolder> {

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public TextView pickupTime;
        public LinearLayout wrap;
        public ImageView logo;

        public MyHolder(View view) {
            super(view);
            wrap = (LinearLayout) view.findViewById(R.id.ll_schedule_wrap);
            title = (TextView) view.findViewById(R.id.tv_schedule_name);
            logo = (ImageView) view.findViewById(R.id.iv_schedule_logo);
            pickupTime = (TextView) view.findViewById(R.id.tv_schedule_pickup);

        }
        @Override
        public void onClick(View view) {

        }

    }

    public abstract Catalog getItem(int position);
}
