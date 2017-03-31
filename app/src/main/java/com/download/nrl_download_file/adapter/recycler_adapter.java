package com.download.nrl_download_file.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.download.nrl_download_file.R;
import com.download.nrl_download_file.fragment.Item_about;

import java.util.ArrayList;


/**
 * Created by garytan on 2016/11/22.
 */

public class recycler_adapter  extends RecyclerView.Adapter<recycler_adapter.ViewHolder>{
    private static final String TAG="recycler_adapter";
    public ArrayList<Item_about> minfo;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView info,name;
        public ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            Log.d(TAG,"viewholder");
            info = (TextView) v.findViewById(R.id.info_text);
            name=(TextView) v.findViewById(R.id.name);
            imageView=(ImageView)v.findViewById(R.id.img);
        }
    }

    public recycler_adapter(ArrayList<Item_about> info) {
        Log.d(TAG,"recycler_adapter");
        this.minfo=info;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_about, parent, false);
        Log.d(TAG,"onCreateViewHolder");
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder");
        Item_about item=minfo.get(position);
        holder.imageView.setImageResource(item.getImg());
        holder.name.setText(item.getName());
        holder.info.setText(item.getInfo());
    }




    @Override
    public int getItemCount() {
        return minfo.size();
    }
}
