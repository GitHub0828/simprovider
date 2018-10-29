
package com.android.settings.simprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.options.wind.WindFeatureOptions;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.view.WindowManager;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.android.settings.R;
import com.android.settings.simprovider.data.ApnData;
import com.android.settings.simprovider.data.SimProviderData;
import com.android.settings.simprovider.data.SimProviderShowingData;

import static com.android.settings.ApnSettings.APN_ID;
import static com.android.settings.ApnSettings.PREFERRED_APN_URI;



public class SimProviderAction {
    private static final String TAG = "wind-sim";

    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int APN_INDEX = 2;
    private static final int TYPES_INDEX = 3;
    private static final int MVNO_TYPE_INDEX = 4;
    private static final int MVNO_MATCH_DATA_INDEX = 5;
    /// M: check source type, some types are not editable
    private static final int SOURCE_TYPE_INDEX = 6;
    public static final String KEY_MCCMNC = "wind.user_last_mccmnc";
    public static final String KEY_IMSI = "wind.user_last_imsi";
    public static final String KEY_USER_APN_KEYS = "wind.user_apn_keys";

    public static final String KEY_PRO_USERMMSAPN = "persist.sys.usermmsapn";
    public static boolean isDialogShowing = false;

    public static void popDialogIfNeed(Context context, int subId, Runnable runAfterOk) {
        if (isDialogShowing) {
            Log.d(TAG, "dialog is showing");
            return;
        }
        // int subId = SubscriptionManager.getDefaultDataSubscriptionId();
        SubscriptionInfo mSubscriptionInfo = SubscriptionManager.from(context).getActiveSubscriptionInfo(subId);

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String mccmnc = mSubscriptionInfo == null ? ""
                : tm.getSimOperator(mSubscriptionInfo.getSubscriptionId());
        Log.d(TAG, "popDialogIfNeed change to 23415 from = " + mccmnc);
         //mccmnc = "23415";

        String where = "numeric=\"" + mccmnc +
                "\" AND NOT (type='ia' AND (apn=\"\" OR apn IS NULL)) AND user_visible!=0";
        Log.d(TAG, "where: " + where);
        Cursor cursor = context.getContentResolver().query(
                Telephony.Carriers.CONTENT_URI,
                new String[]{"_id", "name", "apn", "type", "mvno_type", "mvno_match_data", "sourcetype"},
                where, null, null);
        if (cursor == null) {
            Log.d(TAG, "cursor null return");
            return;
        }
        if (!cursor.moveToFirst()) {
            Log.d(TAG, "cursor empty return");
            return;
        }

        ArrayList<ApnData> apnDatas = new ArrayList<>();

        do {
            ApnData apnData = new ApnData();
            apnData.name = cursor.getString(NAME_INDEX);
            apnData.apn = cursor.getString(APN_INDEX);
            apnData.key = cursor.getString(ID_INDEX);
            apnData.type = cursor.getString(TYPES_INDEX);
            apnData.mvnoType = cursor.getString(MVNO_TYPE_INDEX);
            apnData.mvnoMatchData = cursor.getString(MVNO_MATCH_DATA_INDEX);
            apnData.sourcetype = cursor.getInt(SOURCE_TYPE_INDEX);
            apnDatas.add(apnData);
        } while (cursor.moveToNext());
        cursor.close();
        long defaultCount = apnDatas.stream().filter(apnData -> apnData.type.contains("default")).count();
        if (defaultCount < 2) {
            Log.d(TAG, "apnDatas default size < 2 return");
            return;
        }
        popDialog(context, subId, mccmnc, apnDatas, runAfterOk);
     /*
        if (context instanceof Activity) {
            popDialog(context, subId, mccmnc, apnDatas, runAfterOk);
        } else {
            SimDialogActivity.startShowDialogActivity(context, subId, mccmnc, apnDatas);
        }*/
    }

    static void popDialog(Context context, final int subId, String mccmnc, List<ApnData> apnDatas, Runnable runAfterOk) {
        Log.d(TAG, "dialog show ");
       
        //fix for apn selection when multiple choice found by auto-selection in EU2S
        if (WindFeatureOptions.WIND_DEF_OPTR_A155_ALGB || WindFeatureOptions.WIND_DEF_OPTR_A155_EU2S) {
            popDialogALGB(context, subId, mccmnc, apnDatas, runAfterOk);
        }
      
        if (WindFeatureOptions.WIND_DEF_OPTR_A155_ALRU) {
            popDialogALRU(context, subId, apnDatas);
        }
    }

    private static void popDialogALGB(Context context, final int subId, String mccmnc, List<ApnData> apnDatas, Runnable runAfterOk) {
        SimProviderPolicy simProviderPolicy = SimProviderPolicy.getSimProviderPolicy(mccmnc);
        if (simProviderPolicy == null) {
            Log.d(TAG, "simProviderPolicy null");
            return;
        }
        ArrayMap<SimProviderData, SimProviderShowingData> simProviderMap = new ArrayMap<>();
        for (ApnData apnData : apnDatas) {
            SimProviderData simProviderData = simProviderPolicy.findSimProviderData(apnData.name, apnData.apn);
            if (simProviderData == null) {
                continue;
            }
            SimProviderShowingData showingData = simProviderMap.get(simProviderData);
            if (showingData == null) {
                showingData = new SimProviderShowingData(simProviderData);
                simProviderMap.put(simProviderData, showingData);
            }

            if (apnData.type.contains("default")) {
                Log.d(TAG, "set key " + simProviderData + " = " + apnData.key);
                showingData.setKey(apnData.key);
            }

            if (apnData.type.contains("mms") && !simProviderData.isOnlyInternet()) {
                Log.d(TAG, "set mms key " + simProviderData + " = " + apnData.key);
                showingData.setMmsKey(apnData.key);
            }
        }

        if (simProviderMap.isEmpty()) {
            Log.d(TAG, "simProviderMap empty");
            return;
        }

        List<SimProviderShowingData> showingDatas = simProviderMap.values().stream()
                .sorted()
                .collect(Collectors.toList());

        // create a title list to show in the dialog
        String[] simProviderTitles = showingDatas.stream()
                .map(showingData -> showingData.getSimProviderData().getTitle())
                .toArray(String[]::new);

        final int[] selectedindexWrapper = {0};
        final boolean[] okClicked = {false};
       
        int mSlotId = SubscriptionManager.getSlotId(subId);
        String title1 = context.getResources().getString(R.string.sim_dialog_title);
        String title2 = String.format(context.getResources()
                    .getString(R.string.simprovider_dialog_title_slot), (mSlotId + 1)) ;
       
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                //.setTitle(R.string.sim_dialog_title)
                .setTitle(title1+title2)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok,
                        (d, w) -> {
                            okClicked[0] = true;
                            Log.d(TAG, "click ok");
                        })
                .setSingleChoiceItems(simProviderTitles, -1, (dialog, which) -> selectedindexWrapper[0] = which)
                .setOnDismissListener(dialog -> {
                    String userApnKey = null;
                    String userMmsApnKey = "-1";
                    String userApnKeys = "";
                    int selectedIndex = selectedindexWrapper[0];
                    if (okClicked[0] && selectedIndex >= 0) {
                        SimProviderShowingData showingData = showingDatas.get(selectedIndex);
                        userApnKey = showingData.getKey();
                        userMmsApnKey = showingData.getMmsKey();
                        userApnKeys = showingData.getKey() + "," + showingData.getMmsKey();
                        Log.d(TAG, "clicked ok seleced at " + selectedIndex + " data = " + showingData);
                    }
                    setSelectedApnKey(context, subId, userApnKey);
                    SystemProperties.set(KEY_PRO_USERMMSAPN + subId, userMmsApnKey);
                    Settings.System.putString(context.getContentResolver(), KEY_USER_APN_KEYS + subId, userApnKeys);
                    if (runAfterOk != null) {
                        runAfterOk.run();
                    }
                    writeUserLastMccMncImsi(context, subId);
                    Log.d(TAG, "dialog dismiss");
                })
                .create();

        if (!(context instanceof Activity)) {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            Log.d(TAG, "WindowManager.LayoutParams.TYPE_SYSTEM_ALERT");
        }
        alertDialog.show();
        Log.d(TAG, "algb dialog showing");
    }

    private static void popDialogALRU(Context context, final int subId, List<ApnData> apnDatas) {
        List<ApnData> defaultApnDatas = apnDatas.stream()
                .filter(apnData -> apnData.type.contains("default"))
                .filter(apnData -> !apnData.name.endsWith(" WAP"))
                .collect(Collectors.toList());

        if (defaultApnDatas.isEmpty()) {
            Log.d(TAG, "alru apn data empty");
            return;
        }
        String[] showingTitles = defaultApnDatas.stream()
                .map(apnData -> apnData.name)
                .toArray(String[]::new);

        final int[] selectedindexWrapper = {-1};
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.apn_settings)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> {
                            int selectedIndex = selectedindexWrapper[0];
                            if (selectedIndex < 0) {
                                Log.d(TAG, "selectedIndex < 0");
                                return;
                            }
                            ApnData apnData = defaultApnDatas.get(selectedIndex);
                            setSelectedApnKey(context, subId, apnData.key);
                            Log.d(TAG, "setSelectedApnKey: " + subId + " key -> " + apnData.key);

                        })
                .setSingleChoiceItems(showingTitles, -1, (dialog, which) -> selectedindexWrapper[0] = which)
                .setOnDismissListener(dialog -> {
                    isDialogShowing = false;
                    writeUserLastMccMncImsi(context, subId);
                })
                .create();

        if (!(context instanceof Activity)) {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            Log.d(TAG, "WindowManager.LayoutParams.TYPE_SYSTEM_ALERT");
        }
        alertDialog.show();

        Log.d(TAG, "alru dialog showing");
        isDialogShowing = true;
    }

    private static Uri getPreferApnUri(int subId) {
        Uri preferredUri = Uri.withAppendedPath(Uri.parse(PREFERRED_APN_URI), "/subId/" + subId);
        Log.d(TAG, "getPreferredApnUri: " + preferredUri);
        return preferredUri;
    }

    private static void setSelectedApnKey(Context context, int subid, String key) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(APN_ID, key);
        resolver.update(getPreferApnUri(subid), values,
                null, null);
    }


    public static void clearMmsApnPrefer() {
        SystemProperties.set(KEY_PRO_USERMMSAPN, "-1");
    }

    public static void writeUserLastMccMncImsi(Context context, int subId) {
        // int subId = SubscriptionManager.getDefaultSubId();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // String mccmnc = tm.getSimOperator(subId);
        String imsi = tm.getSubscriberId(subId);
        writeUserLastMccMncImsi(context, subId, imsi);
    }

    public static void writeUserLastMccMncImsi(Context context, int subId, String imsi) {
        // Settings.System.putString(context.getContentResolver(), KEY_MCCMNC, mccmnc);
        Settings.System.putString(context.getContentResolver(), KEY_IMSI+subId, imsi);
    }
/*

    public static String getUserLastMccMnc(Context context) {
        return Settings.System.getString(context.getContentResolver(), KEY_MCCMNC);
    }
*/

    public static String getUserLastImsi(Context context, int subId) {
        return Settings.System.getString(context.getContentResolver(), KEY_IMSI+subId);
    }

    static final class Log {
        static void d(String tag, String msg) {
            android.util.Log.d(tag, msg);
        }
    }
}
