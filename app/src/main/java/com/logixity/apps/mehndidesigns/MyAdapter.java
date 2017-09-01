package com.logixity.apps.mehndidesigns;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by ahmed on 21/06/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
private ArrayList<Integer> galleryList;
private Context context;

public MyAdapter(Context context,ArrayList<Integer>galleryList){
        this.galleryList=galleryList;
        this.context=context;
        }

@Override
public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item,viewGroup,false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(MyAdapter.ViewHolder viewHolder,int i){
    RequestOptions glideOptions = new RequestOptions().fitCenter().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    Glide.with(context)
            .load(galleryList.get(i)).apply(glideOptions)
            .into(viewHolder.img);
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setOnClickListener(new MainActivity.ImageZoomer(galleryList.get(i)));
//        viewHolder.img.setImageResource((galleryList.get(i)));
        }

@Override
public int getItemCount(){
        return galleryList.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder {

    private ImageView img;

    public ViewHolder(View view) {
        super(view);
        img = (ImageView) view.findViewById(R.id.list_item_img);
    }
}
}