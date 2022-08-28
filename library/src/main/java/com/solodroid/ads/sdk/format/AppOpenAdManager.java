package com.solodroid.ads.sdk.format;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.solodroid.ads.sdk.util.OnShowAdCompleteListener;

import java.util.Date;

public class AppOpenAdManager {

    private static final String LOG_TAG = "AppOpenAd";
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    public boolean isShowingAd = false;
    //public String appOpenAdUnitId = "";

    /**
     * Keep track of the time an app open ad is loaded to ensure you don't show an expired ad.
     */
    private long loadTime = 0;

    /**
     * Constructor.
     */
    public AppOpenAdManager() {
        //this.appOpenAdUnitId = appOpenAdUnitId;
    }

    /**
     * Load an ad.
     *
     * @param context the context of the activity that loads the ad
     */
    public void loadAd(Context context, String adManagerAppOpenAdUnitId) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable()) {
            return;
        }

        isLoadingAd = true;
        AdManagerAdRequest request = new AdManagerAdRequest.Builder().build();
        AppOpenAd.load(context, adManagerAppOpenAdUnitId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();

                        Log.d(LOG_TAG, "onAdLoaded.");
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoadingAd = false;
                        Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                    }
                });
    }

    /**
     * Check if ad was loaded more than n hours ago.
     */
    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * Check if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     */
    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId) {
        showAdIfAvailable(activity, appOpenAdUnitId, () -> {
            // Empty because the user will go back to the activity that shows the ad.
        });
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity                 the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.d(LOG_TAG, "The app open ad is already showing.");
            return;
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            Log.d(LOG_TAG, "The app open ad is not ready yet.");
            onShowAdCompleteListener.onShowAdComplete();
            loadAd(activity, appOpenAdUnitId);
            return;
        }

        Log.d(LOG_TAG, "Will show ad.");

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            /** Called when full screen content is dismissed. */
            @Override
            public void onAdDismissedFullScreenContent() {
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null;
                isShowingAd = false;

                Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");

                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity, appOpenAdUnitId);
            }

            /** Called when fullscreen content failed to show. */
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                appOpenAd = null;
                isShowingAd = false;
                Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity, appOpenAdUnitId);
            }

            /** Called when fullscreen content is shown. */
            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
            }
        });

        isShowingAd = true;
        appOpenAd.show(activity);
    }
}