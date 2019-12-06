package com.mahmoudshabat.restaurants.utils;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mahmoudshabat.restaurants.R;

public class Utils {



    public static void sendBlankBroadcastWithData(Context mContext, String intentFilter, Bundle bundle) {
        Intent intent = new Intent(intentFilter) ;
        intent.putExtras(bundle) ;
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    public static void sendBlankBroadcast(Context mContext, String intentFilter) {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(intentFilter));
    }

    public static void registerBroadcast(Context mContext, BroadcastReceiver mReceiver, String intentFilter) {
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter(intentFilter));
    }

    public static void unRegisterBroadcast(Context mContext, BroadcastReceiver mReciever) {
        if (mReciever != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReciever);
        }
    }

    public static int dpToPx(Context context ,int dp) {
        return Math.round(dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    public static void  setupUIForKeyBoard(View view, final Activity activity) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }

            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUIForKeyBoard(innerView,activity);
            }
        }
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static void invokeShare(Activity activity, String text) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);

        activity.startActivity(Intent.createChooser(shareIntent,activity.getString(R.string.share_via)));
    }


}
