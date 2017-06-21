package com.logixity.apps.mehndidesigns;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final Integer image_ids[] = {
            R.drawable.mehndi_1,
            R.drawable.mehndi_2,
            R.drawable.mehndi_3,
            R.drawable.mehndi_4,
            R.drawable.mehndi_5,
            R.drawable.mehndi_6,
            R.drawable.mehndi_7,
            R.drawable.mehndi_8,
            R.drawable.mehndi_1,
            R.drawable.mehndi_2,
            R.drawable.mehndi_3,
            R.drawable.mehndi_4,
            R.drawable.mehndi_5,
            R.drawable.mehndi_6,
            R.drawable.mehndi_7,
            R.drawable.mehndi_8,
            R.drawable.mehndi_1,
            R.drawable.mehndi_2,
            R.drawable.mehndi_3,
            R.drawable.mehndi_4,
            R.drawable.mehndi_5,
            R.drawable.mehndi_6,
            R.drawable.mehndi_7,
            R.drawable.mehndi_8,
            R.drawable.mehndi_1,
            R.drawable.mehndi_2,
            R.drawable.mehndi_3,
            R.drawable.mehndi_4,
            R.drawable.mehndi_5,
            R.drawable.mehndi_6,
            R.drawable.mehndi_7,
            R.drawable.mehndi_8

    };
    private Animator mCurrentAnimator;
    static MainActivity instance;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    RecyclerView recyclerView;
    ImageView expandedImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        expandedImageView = (ImageView) findViewById(
                R.id.zoomedImage);
        instance = this;
        //recyclerView.setHasFixedSize(true);

        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Integer> createLists = prepareData();
        MyAdapter adapter = new MyAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);
    }
    public ArrayList<Integer> prepareData(){
        ArrayList<Integer> list = new ArrayList<>();
        for(int id : image_ids){
            list.add(id);
        }
        return list;
    }

    public static class ImageZoomer implements View.OnClickListener{

        int imageId;

        public ImageZoomer(int imageId) {
            this.imageId = imageId;
        }

        @Override
        public void onClick(View v) {
            instance.zoomImageFromThumb(v,imageId);
        }
    }
    void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        recyclerView.setVisibility(View.INVISIBLE);

        // Load the high-resolution "zoomed-in" image.

        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.activity_main)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        findViewById(R.id.activity_main).setBackgroundColor(Color.BLACK);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }
                recyclerView.setVisibility(View.VISIBLE);
                findViewById(R.id.activity_main).setBackgroundColor(Color.WHITE);

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(expandedImageView.getVisibility()==View.VISIBLE){
            expandedImageView.performClick();
        } else {
            super.onBackPressed();
        }

    }
}
