package com.logixity.apps.mehndidesigns;

import android.*;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MenuActivity extends AppCompatActivity {
    static MenuActivity instance;
    static int option = 0;
    private Animator mCurrentAnimator;
    RecyclerView favRecycler;
    RecyclerView menuRecycler;
    private int mShortAnimationDuration;
    boolean isZoomed;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 110;
    ImageView expandedImageView;
    int zoomedImage = -1;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        isZoomed = false;
        instance = this;
        mAdView = (AdView) findViewById(R.id.menuBannerAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        favRecycler = (RecyclerView) findViewById(R.id.favRecycler);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        favRecycler.setLayoutManager(layoutManager);

        FavAdapter adapter = new FavAdapter(getApplicationContext(), App.dataMap.get("upper"));

        favRecycler.setAdapter(adapter);
        menuRecycler = (RecyclerView) findViewById(R.id.catRecycler);
        RecyclerView.LayoutManager menuLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        menuRecycler.setLayoutManager(menuLayoutManager);
        CategoryAdapter categoryAdapter = new CategoryAdapter(this);

        menuRecycler.setAdapter(categoryAdapter);
        expandedImageView = (ImageView) findViewById(
                R.id.zoomedImageF);
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

    }
    public static class ImageZoomer implements View.OnClickListener {

        int imageId;

        public ImageZoomer(int imageId) {
            this.imageId = imageId;
        }

        @Override
        public void onClick(View v) {
            instance.zoomImageFromThumb(v, imageId);
        }
    }
    void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        favRecycler.setVisibility(View.INVISIBLE);

        // Load the high-resolution "zoomed-in" image.

        isZoomed = true;
//        invalidateOptionsMenu();
        expandedImageView.setImageResource(imageResId);
        findViewById(R.id.dlBtnF).setVisibility(View.VISIBLE);
        menuRecycler.setVisibility(View.INVISIBLE);
        AdRequest request = null;
        if (App.instance.testingMode)
            request = new AdRequest.Builder().addTestDevice("55757F6B6D6116FAC42122EC92E5A58C").build();
        else
            request = new AdRequest.Builder().build();
        final NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.nativeAdViewFV);
        adView.setVisibility(View.VISIBLE);
        adView.loadAd(request);
        zoomedImage = imageResId;
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
        findViewById(R.id.activity_menu)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        findViewById(R.id.activity_menu).setBackgroundColor(Color.BLACK);
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
                adView.setVisibility(View.GONE);
                findViewById(R.id.dlBtnF).setVisibility(View.GONE);
                favRecycler.setVisibility(View.VISIBLE);
                menuRecycler.setVisibility(View.VISIBLE);
                findViewById(R.id.activity_menu).setBackgroundColor(Color.WHITE);
                isZoomed = false;
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
                        showFullScreenAd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                        showFullScreenAd();
                    }
                });
                set.start();
                mCurrentAnimator = set;

            }
        });
    }
    void showFullScreenAd(){
        InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
        if (fullScreenAd!=null && fullScreenAd.isLoaded()) {
            fullScreenAd.show();
        } else {
            Log.d("MADDY", "Interstitial Not Loaded");
            App.instance.requestNewInterstitial();
//                    App.instance.countIntAd--;
        }
    }
    @Override
    public void onBackPressed() {
        if (expandedImageView.getVisibility() == View.VISIBLE) {
            expandedImageView.performClick();
        } else {
            super.onBackPressed();
        }

    }
    public void downloadImgClicked(View v) {
        InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
        if (fullScreenAd.isLoaded()) {
            fullScreenAd.show();
        } else {
            Log.d("MADDY", "Interstitial Not Loaded");
            App.instance.requestNewInterstitial();
//                    App.instance.countIntAd--;
        }
        option = 2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            permissionAllowed();
        }
    }
    @TargetApi(23)
    public void checkPermissions() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            permissionAllowed();
        }
    }

    public void permissionAllowed() {
        if (option == 1) {
            shareImage();
        } else if (option == 2) {
            downloadImage();
        }

    }
    void downloadImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), zoomedImage);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "mehndi_" + System.currentTimeMillis() + ".jpg";
        OutputStream out = null;
        File file = new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        path = file.getPath();


        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, path);

        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Toast.makeText(this, "Design Saved to Gallery", Toast.LENGTH_LONG).show();
    }

    public void shareImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), zoomedImage);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/LatestShare.jpg";
        OutputStream out = null;
        File file = new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        path = file.getPath();
        Uri bmpUri = Uri.parse("file://" + path);
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out new Exclusive Mehndi Designs. Live at the Playstore.\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Share with"));
    }
    public void shareImg() {
        option = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            permissionAllowed();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionAllowed();
                } else {
                    Toast.makeText(this, "Application Requires Permission for sharing", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}
