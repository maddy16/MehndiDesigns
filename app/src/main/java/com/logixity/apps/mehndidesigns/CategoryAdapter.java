package com.logixity.apps.mehndidesigns;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;


import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ahmed on 21/06/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private Random random;
    static int selectedDesign;
    public CategoryAdapter(Context context) {
        this.context = context;
        random = new Random();
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_recycler_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.ViewHolder viewHolder, int i) {
        AdRequest request = null;
        if (App.instance.testingMode)
            request = new AdRequest.Builder().addTestDevice("55757F6B6D6116FAC42122EC92E5A58C").build();
        else
            request = new AdRequest.Builder().build();

        viewHolder.adView.loadAd(request);
        viewHolder.catViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDesign=viewHolder.getAdapterPosition();
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });
    int pic = -1;
        ArrayList<Integer> data = null;
        switch (i){
            case 0:
                data = App.dataMap.get("arm");

                viewHolder.catTitle.setText("Arm Designs");
                viewHolder.catDesc.setText("Mehndi Designs for Arm");

                break;
            case 1:
                data = App.dataMap.get("upper");
                viewHolder.catTitle.setText("Upper Hand Designs");
                viewHolder.catDesc.setText("Mehndi Designs for Upper Hand");
                break;
            case 2:
                data = App.dataMap.get("palm");
                viewHolder.catTitle.setText("Palm Designs");
                viewHolder.catDesc.setText("Mehndi Designs for Palm");
                break;
            case 3:
                data = App.dataMap.get("foot");
                viewHolder.catDesc.setText("Mehndi Designs for Feet");
                viewHolder.catTitle.setText("Foot Designs");
                break;

        }
        Glide.with(context)
                .load(data.get(random.nextInt(data.size())))
                .thumbnail(0.2f)
                .into(viewHolder.img);
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        viewHolder.img.setOnClickListener(new MenuActivity.ImageZoomer(galleryList.get(pic)));
////        viewHolder.img.setImageResource((galleryList.get(i)));
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private Button catViewBtn;
        private NativeExpressAdView adView;
        private TextView catTitle;
        private TextView catDesc;
        public ViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.catImage);
            catViewBtn = (Button) view.findViewById(R.id.catViewBtn);
            adView = (NativeExpressAdView) view.findViewById(R.id.nativeAdViewCat);
            catTitle = (TextView) view.findViewById(R.id.catText);
            catDesc = (TextView) view.findViewById(R.id.catDesc);
        }
    }
}