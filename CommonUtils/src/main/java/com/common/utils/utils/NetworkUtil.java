package com.common.utils.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.common.utils.utils.log.LogUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网络工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class NetworkUtil {

    public static final String TAG = "NetworkUtil";

    public static final byte CURRENT_NETWORK_TYPE_NONE = 0;

    /**
     * 网络类型
     */
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_CLASS_2_G     = 1;
    public static final int NETWORK_CLASS_3_G     = 2;
    public static final int NETWORK_CLASS_4_G     = 3;
    public static final int NETWORK_CLASS_WIFI    = 10;

    /*
     * 根据APN区分网络类型
     */
    public static final byte CURRENT_NETWORK_TYPE_WIFI   = 1;     // wifi
    public static final byte CURRENT_NETWORK_TYPE_CTNET  = 2;    // ctnet
    public static final byte CURRENT_NETWORK_TYPE_CTWAP  = 3;    // ctwap
    public static final byte CURRENT_NETWORK_TYPE_CMWAP  = 4;    // cmwap
    public static final byte CURRENT_NETWORK_TYPE_UNIWAP = 5;   // uniwap,3gwap
    public static final byte CURRENT_NETWORK_TYPE_CMNET  = 6;    // cmnet
    public static final byte CURRENT_NETWORK_TYPE_UNIET  = 7;    // uninet,3gnet

    /**
     * 根据运营商区分网络类型
     */
    public static final byte CURRENT_NETWORK_TYPE_CTC = 10; // 中国电信ctwap,ctnet
    public static final byte CURRENT_NETWORK_TYPE_CUC = 11; // 中国联通uniwap,3gwap,uninet,3gnet
    public static final byte CURRENT_NETWORK_TYPE_CM  = 12;  // 中国移动cmwap,cmnet

    /**
     * APN值
     */
    private static final String CONNECT_TYPE_WIFI     = "wifi";
    private static final String CONNECT_TYPE_CTNET    = "ctnet";
    private static final String CONNECT_TYPE_CTWAP    = "ctwap";
    private static final String CONNECT_TYPE_CMNET    = "cmnet";
    private static final String CONNECT_TYPE_CMWAP    = "cmwap";
    private static final String CONNECT_TYPE_UNIWAP   = "uniwap";
    private static final String CONNECT_TYPE_UNINET   = "uninet";
    private static final String CONNECT_TYPE_UNI3GWAP = "3gwap";
    private static final String CONNECT_TYPE_UNI3GNET = "3gnet";

    /**
     * APN的URI
     */
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    /**
     * 当前网络类型
     */
    public static byte currentNetworkType = CURRENT_NETWORK_TYPE_NONE;

    /**
     * 获取网络类型
     *
     * @param context
     *
     * @return
     */
    public static int getNetType(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo == null) {
            return -1;
        }
        else {
            return networkInfo.getType();
        }
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     *
     * @return
     */
    public static byte getCurrentNetType(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        byte type = CURRENT_NETWORK_TYPE_NONE;
        if (networkInfo != null) {
            String typeName = networkInfo.getExtraInfo();
            if (TextUtils.isEmpty(typeName)) {
                typeName = networkInfo.getTypeName();
            }
            if (!TextUtils.isEmpty(typeName)) {
                String temp = typeName.toLowerCase();
                if (temp.contains(CONNECT_TYPE_WIFI)) { // wifi
                    type = CURRENT_NETWORK_TYPE_WIFI;
                }
                else if (temp.contains(CONNECT_TYPE_CTNET)) {   // ctnet
                    type = CURRENT_NETWORK_TYPE_CTNET;
                }
                else if (temp.contains(CONNECT_TYPE_CTWAP)) {   // ctwap
                    type = CURRENT_NETWORK_TYPE_CTWAP;
                }
                else if (temp.contains(CONNECT_TYPE_CMNET)) {   // cmnet
                    type = CURRENT_NETWORK_TYPE_CMNET;
                }
                else if (temp.contains(CONNECT_TYPE_CMWAP)) {   // cmwap
                    type = CURRENT_NETWORK_TYPE_CMWAP;
                }
                else if (temp.contains(CONNECT_TYPE_UNIWAP)) {  // uniwap
                    type = CURRENT_NETWORK_TYPE_UNIWAP;
                }
                else if (temp.contains(CONNECT_TYPE_UNI3GWAP)) {    // 3gwap
                    type = CURRENT_NETWORK_TYPE_UNIWAP;
                }
                else if (temp.contains(CONNECT_TYPE_UNINET)) {  // uninet
                    type = CURRENT_NETWORK_TYPE_UNIET;
                }
                else if (temp.contains(CONNECT_TYPE_UNI3GNET)) {    // 3gnet
                    type = CURRENT_NETWORK_TYPE_UNIET;
                }
            }
        }

        if (type == CURRENT_NETWORK_TYPE_NONE) {
            String apnType = getApnType(context);
            if (apnType != null && apnType.equals(CONNECT_TYPE_CTNET)) {    // ctnet
                type = CURRENT_NETWORK_TYPE_CTNET;
            }
            else if (apnType != null && apnType.equals(CONNECT_TYPE_CTWAP)) {   // ctwap
                type = CURRENT_NETWORK_TYPE_CTWAP;
            }
            else if (apnType != null && apnType.equals(CONNECT_TYPE_CMWAP)) {   // cmwap
                type = CURRENT_NETWORK_TYPE_CMWAP;
            }
            else if (apnType != null && apnType.equals(CONNECT_TYPE_CMNET)) {   // cmnet
                type = CURRENT_NETWORK_TYPE_CMNET;
            }
            else if (apnType != null && apnType.equals(CONNECT_TYPE_UNIWAP)) {  // uniwap
                type = CURRENT_NETWORK_TYPE_UNIWAP;
            }
            else if (apnType != null && apnType.equals(CONNECT_TYPE_UNI3GWAP)) {    // 3gwap
                type = CURRENT_NETWORK_TYPE_UNIWAP;
            }
            else if (apnType != null && apnType.equals(CONNECT_TYPE_UNINET)) {  // uninet
                type = CURRENT_NETWORK_TYPE_UNIET;
            }
            else if (apnType != null && apnType.equals(CONNECT_TYPE_UNI3GNET)) {    // 3gnet
                type = CURRENT_NETWORK_TYPE_UNIET;
            }
        }
        currentNetworkType = type;
        return type;
    }

    /**
     * 获取APNTYPE
     *
     * @param context
     *
     * @return
     */
    public static String getApnType(Context context) {
        String apnType = "nomatch";
        Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                String user = c.getString(c.getColumnIndex("user"));
                if (user != null && user.startsWith(CONNECT_TYPE_CTNET)) {
                    apnType = CONNECT_TYPE_CTNET;
                }
                else if (user != null && user.startsWith(CONNECT_TYPE_CTWAP)) {
                    apnType = CONNECT_TYPE_CTWAP;
                }
                else if (user != null && user.startsWith(CONNECT_TYPE_CMWAP)) {
                    apnType = CONNECT_TYPE_CMWAP;
                }
                else if (user != null && user.startsWith(CONNECT_TYPE_CMNET)) {
                    apnType = CONNECT_TYPE_CMNET;
                }
                else if (user != null && user.startsWith(CONNECT_TYPE_UNIWAP)) {
                    apnType = CONNECT_TYPE_UNIWAP;
                }
                else if (user != null && user.startsWith(CONNECT_TYPE_UNINET)) {
                    apnType = CONNECT_TYPE_UNINET;
                }
                else if (user != null && user.startsWith(CONNECT_TYPE_UNI3GWAP)) {
                    apnType = CONNECT_TYPE_UNI3GWAP;
                }
                else if (user != null && user.startsWith(CONNECT_TYPE_UNI3GNET)) {
                    apnType = CONNECT_TYPE_UNI3GNET;
                }
            }
            c.close();
        }
        return apnType;
    }

    /**
     * 判断是否有网络可用
     *
     * @param context
     *
     * @return
     */
    public static boolean isNetAvailable(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isAvailable();
    }

    /**
     * 判断是否有网络连接
     * 注意：此方法不可靠
     *
     * @param context
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 判断是否网络已连接或正在连接
     *
     * @param context
     *
     * @return
     */
    public static boolean isConnectedOrConnecting(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * 获取可用的网络信息
     *
     * @param context
     *
     * @return
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否是wifi或4G
     *
     * @param context
     *
     * @return
     */
    public static boolean isWifiOr4G(Context context) {
        return isWifi(context) || is4G(context);
    }

    /**
     * 判断是否是2G
     *
     * @param context
     *
     * @return
     */
    public static boolean is2G(Context context) {
        return !is3G(context) && !isWifiOr4G(context);
    }

    /**
     * 判断是否是3G
     *
     * @param context
     *
     * @return
     */
    public static boolean is3G(Context context) {
        int type = getNetworkClass(context);
        return type == NETWORK_CLASS_3_G;
    }

    /**
     * 判断是否是4G
     *
     * @param context
     *
     * @return
     */
    public static boolean is4G(Context context) {
        int type = getNetworkClass(context);
        return type == NETWORK_CLASS_4_G;
    }

    /**
     * 判断是否是wifi
     *
     * @param context
     *
     * @return
     */
    public static boolean isWifi(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取网络连接状态
     *
     * @param context
     *
     * @return
     */
    public static boolean getNetworkConnectionStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return tm != null && (tm.getDataState() == TelephonyManager.DATA_CONNECTED || tm.getDataState() == TelephonyManager.DATA_ACTIVITY_NONE) && info.isAvailable();
    }

    /**
     * 获取网络代理信息
     *
     * @param context
     *
     * @return
     */
    public static String getNetworkProxyInfo(Context context) {
        String proxyHost = android.net.Proxy.getDefaultHost();
        int proxyPort = android.net.Proxy.getDefaultPort();
        String szport = String.valueOf(proxyPort);
        String proxyInfo = null;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return null;
        }
        else {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null) {
                String typeName = info.getTypeName().toLowerCase();
                if (typeName.equals("wifi")) {
                    return null;
                }
            }
            else {
                return null;
            }
        }

        if (proxyHost != null && (0 < proxyPort && proxyPort < 65535)) {
            proxyInfo = proxyHost + ":" + szport;
            return proxyInfo;
        }
        else {
            return null;
        }
    }

    /**
     * 获取网络代理url
     *
     * @param context
     *
     * @return
     */
    public static String getNetworkProxyUrl(Context context) {
        if (isWifi(context)) {
            return null;
        }
        String proxyHost = android.net.Proxy.getDefaultHost();
        return proxyHost;
    }

    /**
     * 获取网络代理url
     *
     * @return
     */
    public static String getNetworkProxyUrl() {
        if (currentNetworkType == CURRENT_NETWORK_TYPE_WIFI) {
            return null;
        }
        String proxyHost = android.net.Proxy.getDefaultHost();
        return proxyHost;
    }

    /**
     * 获取网络代理端口号
     *
     * @return
     */
    public static int getNetworkProxyPort() {
        return android.net.Proxy.getDefaultPort();
    }

    /**
     * 判断apnType是否是CTWAP
     *
     * @param context
     *
     * @return
     */
    public static boolean isCtwap(Context context) {
        return getApnType(context).equals(CONNECT_TYPE_CTWAP);
    }

    /**
     * 判断apnType是否是UNIWAP
     *
     * @param context
     *
     * @return
     */
    public static boolean isUniwap(Context context) {
        return getApnType(context).equals(CONNECT_TYPE_UNIWAP);
    }

    /**
     * 判断apnType是否是CMWAP
     *
     * @param context
     *
     * @return
     */
    public static boolean isCmwap(Context context) {
        return getApnType(context).equals(CONNECT_TYPE_CMWAP);
    }

    /**
     * 判断是否是电信网络(ctwap,ctnet)
     *
     * @param context
     *
     * @return
     */
    public static boolean isCtcNetwork(Context context) {
        byte type = getCurrentNetType(context);
        return isCtcNetwork(type);
    }

    /**
     * 判断是否是电信网络(ctwap,ctnet)
     *
     * @param apnName
     *
     * @return
     */
    public static boolean isCtcNetwork(String apnName) {
        if (apnName == null) {
            return false;
        }
        return apnName.equals(CONNECT_TYPE_CTWAP) || apnName.equals(CONNECT_TYPE_CTNET);
    }

    /**
     * 判断是否是电信网络(ctwap,ctnet)
     *
     * @param type
     *
     * @return
     */
    public static boolean isCtcNetwork(byte type) {
        return type == CURRENT_NETWORK_TYPE_CTWAP || type == CURRENT_NETWORK_TYPE_CTNET;
    }

    /**
     * 判断是否是联通网络(uniwap,uninet,3gwap,3gnet)
     *
     * @param context
     *
     * @return
     */
    public static boolean isCucNetwork(Context context) {
        byte type = getCurrentNetType(context);
        return isCucNetwork(type);
    }

    /**
     * 判断是否是联通网络(uniwap,uninet,3gwap,3gnet)
     *
     * @param apnName
     *
     * @return
     */
    public static boolean isCucNetwork(String apnName) {
        if (apnName == null) {
            return false;
        }
        return apnName.equals(CONNECT_TYPE_UNIWAP) || apnName.equals(CONNECT_TYPE_UNINET) || apnName.equals(CONNECT_TYPE_UNI3GWAP) || apnName.equals(CONNECT_TYPE_UNI3GNET);
    }

    /**
     * 判断是否是联通网络(uniwap,uninet,3gwap,3gnet)
     *
     * @param type
     *
     * @return
     */
    public static boolean isCucNetwork(byte type) {
        return type == CURRENT_NETWORK_TYPE_UNIWAP || type == CURRENT_NETWORK_TYPE_UNIET;
    }

    /**
     * 判断是否是移动网络(cmwap,cmnet)
     *
     * @param context
     *
     * @return
     */
    public static boolean isCmbNetwork(Context context) {
        byte type = getCurrentNetType(context);
        return isCmbNetwork(type);
    }

    /**
     * 判断是否是移动网络(cmwap,cmnet)
     *
     * @param apnName
     *
     * @return
     */
    public static boolean isCmbNetwork(String apnName) {
        if (apnName == null) {
            return false;
        }
        return apnName.equals(CONNECT_TYPE_CMWAP) || apnName.equals(CONNECT_TYPE_CMNET);
    }

    /**
     * 判断是否是移动网络(cmwap,cmnet)
     *
     * @param type
     *
     * @return
     */
    public static boolean isCmbNetwork(byte type) {
        return type == CURRENT_NETWORK_TYPE_CMWAP || type == CURRENT_NETWORK_TYPE_CMNET;
    }

    /**
     * 获取网络运营商类型(中国移动,中国联通,中国电信,wifi)
     *
     * @param context
     *
     * @return
     */
    public static byte getNetworkOperators(Context context) {
        if (isWifi(context)) {
            return CURRENT_NETWORK_TYPE_WIFI;
        }
        else if (isCtcNetwork(context)) {
            return CURRENT_NETWORK_TYPE_CTC;
        }
        else if (isCmbNetwork(context)) {
            return CURRENT_NETWORK_TYPE_CM;
        }
        else if (isCucNetwork(context)) {
            return CURRENT_NETWORK_TYPE_CUC;
        }
        else {
            return CURRENT_NETWORK_TYPE_NONE;
        }
    }

    /**
     * 获取网络运营商类型(中国移动,中国联通,中国电信,wifi)
     *
     * @param type
     *
     * @return
     */
    public static byte getNetworkOperators(byte type) {
        if (type == CURRENT_NETWORK_TYPE_NONE) {
            return CURRENT_NETWORK_TYPE_NONE;
        }
        else if (type == CURRENT_NETWORK_TYPE_WIFI) {
            return CURRENT_NETWORK_TYPE_WIFI;
        }
        else if (type == CURRENT_NETWORK_TYPE_CTNET || type == CURRENT_NETWORK_TYPE_CTWAP) {
            return CURRENT_NETWORK_TYPE_CTC;
        }
        else if (type == CURRENT_NETWORK_TYPE_CMWAP || type == CURRENT_NETWORK_TYPE_CMNET) {
            return CURRENT_NETWORK_TYPE_CM;
        }
        else if (type == CURRENT_NETWORK_TYPE_UNIWAP || type == CURRENT_NETWORK_TYPE_UNIET) {
            return CURRENT_NETWORK_TYPE_CUC;
        }
        else {
            return CURRENT_NETWORK_TYPE_NONE;
        }
    }

    /**
     * 是否需要设置代理（网络请求，一般用于wap网络，但有些机型设置代理会导致系统异常）
     *
     * @return
     */
    public static boolean isNeedSetProxyForNetRequest() {
        return !(Build.MODEL.equals("SCH-N719") || Build.MODEL.equals("SCH-I939D"));
    }

    /**
     * 获取mac地址
     *
     * @param context
     *
     * @return
     */
    public static String getActiveMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            return info.getMacAddress();
        }
        return "";
    }

    /**
     * 获取网络信息
     *
     * @param context
     *
     * @return
     */
    public static String getNetworkInfo(Context context) {
        String info = "";
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetInfo = connectivity.getActiveNetworkInfo();
            if (activeNetInfo != null) {
                if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    info = activeNetInfo.getTypeName();
                }
                else {
                    StringBuilder sb = new StringBuilder();
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    sb.append(activeNetInfo.getTypeName());
                    sb.append(" [");
                    if (tm != null) {
                        sb.append(tm.getNetworkOperatorName());
                        sb.append("#");
                    }
                    sb.append(activeNetInfo.getSubtypeName());
                    sb.append("]");
                    info = sb.toString();
                }
            }
        }
        return info;
    }

    /**
     * 网速模式
     */
    public enum NetworkSpeedMode {
        LOW, NORMAL, HIGH, UNKNOWN
    }

    /**
     * 仅判断Mobile网络的慢速.蓝牙等其他网络不做判断.
     *
     * @param context
     *
     * @return
     */
    /**
     * 获取mobile的网速
     *
     * @param context
     *
     * @return
     */
    public static NetworkSpeedMode getNetworkSpeedModeInMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    switch (networkInfo.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_IDEN: // ~25 kbps
                            return NetworkSpeedMode.LOW;
                        case TelephonyManager.NETWORK_TYPE_CDMA: // ~ 14-64 kbps
                            return NetworkSpeedMode.LOW;
                        case TelephonyManager.NETWORK_TYPE_1xRTT: // ~ 50-100 kbps
                            return NetworkSpeedMode.LOW;
                        case TelephonyManager.NETWORK_TYPE_EDGE: // ~ 50-100 kbps
                            return NetworkSpeedMode.LOW;
                        case TelephonyManager.NETWORK_TYPE_GPRS: // ~ 100 kbps
                            return NetworkSpeedMode.LOW;
                        case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
                            return NetworkSpeedMode.NORMAL;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
                            return NetworkSpeedMode.NORMAL;
                        case TelephonyManager.NETWORK_TYPE_HSPA: // ~ 700-1700 kbps
                            return NetworkSpeedMode.NORMAL;
                        case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 kbps
                            return NetworkSpeedMode.NORMAL;
                        case TelephonyManager.NETWORK_TYPE_EHRPD: // ~ 1-2 mbps
                            return NetworkSpeedMode.NORMAL;
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: // ~ 5 mbps
                            return NetworkSpeedMode.NORMAL;
                        case TelephonyManager.NETWORK_TYPE_HSDPA: // ~ 2-14 mbps
                            return NetworkSpeedMode.HIGH;
                        case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 mbps
                            return NetworkSpeedMode.HIGH;
                        case TelephonyManager.NETWORK_TYPE_HSPAP: // ~ 10-20 mbps
                            return NetworkSpeedMode.HIGH;
                        case TelephonyManager.NETWORK_TYPE_LTE: // ~ 10+ mbps
                            return NetworkSpeedMode.HIGH;
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            return NetworkSpeedMode.NORMAL;
                        default:
                            break;
                    }
                }
            }
        }
        return NetworkSpeedMode.UNKNOWN;
    }

    /**
     * 获取在Mobile网络下的网络类型. 2G,3G,4G
     *
     * @param context
     *
     * @return
     */
    public static int getNetworkClass(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    switch (networkInfo.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORK_CLASS_2_G;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORK_CLASS_3_G;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORK_CLASS_4_G;
                        default:
                            return NETWORK_CLASS_UNKNOWN;
                    }
                }
                else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return NETWORK_CLASS_WIFI;
                }
            }
        }
        return NETWORK_CLASS_UNKNOWN;
    }

    /**
     * 获取网络类型名称
     *
     * @param context
     *
     * @return
     */
    public static String getNetworkTypeName(Context context) {
        String networkName = "UNKNOWN";
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                networkName = getNetworkTypeName(networkInfo.getType());
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    networkName += "#" + getNetworkTypeNameInMobile(networkInfo.getSubtype());
                }
            }
        }
        return networkName;
    }

    /**
     * 获取当前网络名称
     *
     * @param context
     *
     * @return
     */
    public static String getNetworkName(Context context) {
        String strNetworkType = "";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            }
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String strSubtypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;

                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;

                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;

                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (strSubtypeName.equalsIgnoreCase("TD-SCDMA") || strSubtypeName.equalsIgnoreCase("WCDMA") || strSubtypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        }
                        else {
                            strNetworkType = strSubtypeName;
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }

    /**
     * 获取mobile的网络类型名称
     *
     * @param type
     *
     * @return
     */
    private static String getNetworkTypeNameInMobile(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "CDMA - EvDo rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "CDMA - EvDo rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "CDMA - EvDo rev. B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "CDMA - 1xRTT";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "CDMA - eHRPD";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDEN";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * 获取网络类型名称
     *
     * @param type
     *
     * @return
     */
    private static String getNetworkTypeName(int type) {
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE:
                return "MOBILE";
            case ConnectivityManager.TYPE_WIFI:
                return "WIFI";
            case ConnectivityManager.TYPE_MOBILE_MMS:
                return "MOBILE_MMS";
            case ConnectivityManager.TYPE_MOBILE_SUPL:
                return "MOBILE_SUPL";
            case ConnectivityManager.TYPE_MOBILE_DUN:
                return "MOBILE_DUN";
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
                return "MOBILE_HIPRI";
            case ConnectivityManager.TYPE_WIMAX:
                return "WIMAX";
            case ConnectivityManager.TYPE_BLUETOOTH:
                return "BLUETOOTH";
            case ConnectivityManager.TYPE_DUMMY:
                return "DUMMY";
            case ConnectivityManager.TYPE_ETHERNET:
                return "ETHERNET";
            case 10: // ConnectivityManager.TYPE_MOBILE_FOTA:
                return "MOBILE_FOTA";
            case 11: // ConnectivityManager.TYPE_MOBILE_IMS:
                return "MOBILE_IMS";
            case 12: // ConnectivityManager.TYPE_MOBILE_CBS:
                return "MOBILE_CBS";
            case 13: // ConnectivityManager.TYPE_WIFI_P2P:
                return "WIFI_P2P";
            default:
                return Integer.toString(type);
        }
    }

    /**
     * 连接的网络类型
     */
    public interface LinkNetWorkType {
        int UNKNOWN = 0;
        int WIFI    = 1;
        int WWAN    = 2;
        int _2G     = 3;
        int _3G     = 4;
        int _4G     = 5;
    }

    /**
     * 获取连接的网络类型
     *
     * @param context
     *
     * @return
     */
    public static int getNetworkTypeForLink(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null) {
                if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                    return LinkNetWorkType.WIFI;
                }
                else {
                    if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
                        switch (ni.getSubtype()) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                return LinkNetWorkType._2G;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                return LinkNetWorkType._3G;
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                return LinkNetWorkType._4G;
                            default:
                                return LinkNetWorkType._2G;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return LinkNetWorkType.UNKNOWN;
        }
        return LinkNetWorkType.UNKNOWN;
    }

    /**
     * 打开网络设置页面
     *
     * @param activity
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            LogUtil.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }

    /**
     * 获取外网的IP(要访问Url，要放到后台线程里处理)
     */
    public static String getNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String ipLine = "";
        HttpURLConnection httpConnection = null;
        try {
            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strber.append(line + "\n");
                }
                Pattern pattern = Pattern.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                Matcher matcher = pattern.matcher(strber.toString());
                if (matcher.find()) {
                    ipLine = matcher.group();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                httpConnection.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        LogUtil.e("getNetIp", ipLine);
        return ipLine;
    }
}
