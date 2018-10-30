package com.common.mvp.rx;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Observable;

/**
 * @author Wangxx
 * @date 2017/2/25
 */

public class RxPermissionUtil {

    public static final int REQUEST_CODE_APPLICATION_DETAILS_SETTINGS = 1000;

    public static final String[] BASIC_PERMISSION    = new String[] {
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE
    };
    public static final String[] STORAGE_PERMISSION  = new String[] {
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final String[] CONTACTS_PERMISSION = new String[] {
        Manifest.permission.READ_CONTACTS
    };
    public static final String[] CAMERA_PERMISSION   = new String[] {
        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
    };
    public static final String[] RECORD_PERMISSION   = new String[] {
        Manifest.permission.RECORD_AUDIO
    };
    public static final String[] LOCATION_PERMISSION = new String[] {
        Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static final String[] SMS_PERMISSION      = new String[] {
        Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE
    };

    public static Observable<Boolean> requestBasicPermission(Activity activity) {
        return new RxPermissions(activity).request(BASIC_PERMISSION);
    }

    public static Observable<Boolean> requestStoragePermission(final Activity activity) {
        return new RxPermissions(activity).request(STORAGE_PERMISSION);
    }

    public static Observable<Boolean> requestContactsPermission(final Activity activity) {
        return new RxPermissions(activity).request(CONTACTS_PERMISSION);
    }

    public static Observable<Boolean> requestCameraPermission(final Activity activity) {
        return new RxPermissions(activity).request(CAMERA_PERMISSION);
    }

    public static Observable<Boolean> requestRecordPermission(final Activity activity) {
        return new RxPermissions(activity).request(RECORD_PERMISSION);
    }

    public static Observable<Boolean> requestLocationPermission(final Activity activity) {
        return new RxPermissions(activity).request(LOCATION_PERMISSION);
    }

    public static Observable<Boolean> requestSMSPermission(final Activity activity) {
        return new RxPermissions(activity).request(SMS_PERMISSION);
    }

    public static Observable<Boolean> requestOPPOPermission(final Activity activity) {
        return new RxPermissions(activity).request("oppo.permission.OPPO_COMPONENT_SAFE");
    }

    /**
     * 显示应用详细信息页面
     *
     * @param activity
     */
    public static void showAppDetailsSettingPage(Activity activity) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
        }
        else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(appPkgName, activity.getPackageName());
        }
        activity.startActivityForResult(intent, REQUEST_CODE_APPLICATION_DETAILS_SETTINGS);
    }
}
