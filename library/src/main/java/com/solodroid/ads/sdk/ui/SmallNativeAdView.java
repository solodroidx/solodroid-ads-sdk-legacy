package com.solodroid.ads.sdk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.solodroid.ads.sdk.R;

public class SmallNativeAdView extends LinearLayout {

    private Context mContext;
    private AttributeSet attrs;
    private int styleAttr;
    private View view;

    private Drawable adMobDrawable;
    private Drawable startAppDrawable;

    private Button btnNativeAdMob;
    private Button btnNativeStartApp;

    public SmallNativeAdView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public SmallNativeAdView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.attrs = attrs;
        initView();
    }

    public SmallNativeAdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmallNativeAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressLint("CustomViewStyleable")
    private void initView() {
        this.view = this;
        inflate(mContext, R.layout.view_native_ad_radio, this);
        TypedArray arr = mContext.obtainStyledAttributes(attrs, R.styleable.NativeAdView, styleAttr, 0);

        adMobDrawable = arr.getDrawable(R.styleable.NativeAdView_adMobNativeButton);
        startAppDrawable = arr.getDrawable(R.styleable.NativeAdView_startappNativeButton);

        btnNativeAdMob = findViewById(R.id.cta);
        btnNativeStartApp = findViewById(R.id.startapp_native_button);

        if (adMobDrawable != null) {
            setAdMobNativeButtonColor(adMobDrawable);
        }

        if (startAppDrawable != null) {
            setStartAppNativeButtonColor(startAppDrawable);
        }

        arr.recycle();

    }

    public void setAdMobNativeButtonColor(Drawable background) {
        btnNativeAdMob.setBackground(background);
    }

    public void setStartAppNativeButtonColor(Drawable background) {
        btnNativeStartApp.setBackground(background);
    }

}
