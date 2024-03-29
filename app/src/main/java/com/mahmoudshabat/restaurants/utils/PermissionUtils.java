package com.mahmoudshabat.restaurants.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.fragment.app.Fragment;


public  class PermissionUtils {

        public static boolean useRunTimePermissions() {
            return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
        }

        public static boolean hasPermission(Activity activity, String permission) {
            if (useRunTimePermissions()) {
                return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
            return true;
        }

        public static void requestPermissions(Activity activity, String[] permission, int requestCode) {
            if (useRunTimePermissions()) {
                activity.requestPermissions(permission, requestCode);
            }
        }

        public static void requestPermissions(Fragment fragment, String[] permission, int requestCode) {
            if (useRunTimePermissions()) {
                fragment.requestPermissions(permission, requestCode);
            }
        }



}