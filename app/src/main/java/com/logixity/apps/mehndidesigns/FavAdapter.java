package com.logixity.apps.mehndidesigns;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ahmed on 21/06/2017.
 */

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder>{
private ArrayList<Integer> galleryList;
private Context context;
private Random random;
public FavAdapter(Context context, ArrayList<Integer>galleryList){
        this.galleryList=galleryList;
        this.context=context;
        random = new Random();
        }

@Override
public FavAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fav_recycler_item,viewGroup,false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(FavAdapter.ViewHolder viewHolder, int i){
    int pic = random.nextInt(galleryList.size());
    Glide.with(context)
            .load(galleryList.get(pic)).thumbnail(
                    0.2f
    )
            .into(viewHolder.img);
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setOnClickListener(new MenuActivity.ImageZoomer(galleryList.get(pic)));
//        viewHolder.img.setImageResource((galleryList.get(i)));
        }

@Override
public int getItemCount(){
        return 7;
        }

public class ViewHolder extends RecyclerView.ViewHolder {

    private ImageView img;

    public ViewHolder(View view) {
        super(view);
        img = (ImageView) view.findViewById(R.id.favImage);
    }
}
}