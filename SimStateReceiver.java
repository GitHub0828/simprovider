
package com.android.settings.simprovider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.options.wind.WindFeatureOptions;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.settings.simprovider.SimProviderAction.Log;


public class SimStateReceiver extends BroadcastReceiver {
    public static final String ACTION_SIM_STATE_CHANGE = "android.intent.action.SIM_STATE_CHANGED";
    private static final String TAG = "crrq-sim";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!WindFeatureOptions.WIND_DEF_SHOW_APN_POP) {
            Log.d(TAG, "not sim provider");
            return;
        }
        if (!ACTION_SIM_STATE_CHANGE.equals(intent.getAction())) {
            Log.d(TAG, "not ACTION_SIM_STATE_CHANGE");
            return;
        }

        int subId = intent.getIntExtra("subscription", -1);
        if (subId < 0) {
            Log.d(TAG, "subId < 0");
            return;
        }
       
        boolean simReady = TelephonyManager.SIM_STATE_READY == TelephonyManager.getDefault()
                .getSimState(SubscriptionManager.getSlotId(subId));

        if (!simReady) {
            Log.d(TAG, "sim not ready");
            return;
        }
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mccmnc = tm.getSimOperator(subId);
        String imsi = tm.getSubscriberId(subId);
        if (TextUtils.isEmpty(mccmnc) || TextUtils.isEmpty(imsi)) {
            Log.d(TAG, "mccmnc or imsi not ready");
            return;
        }
        //String lastMccMnc = SimProviderAction.getUserLastMccMnc(context);
        String lastimsi = SimProviderAction.getUserLastImsi(context, subId);
        Log.d(TAG, "mccmnc = " + mccmnc + " imsi = " + imsi);
        Log.d(TAG, " lastimsi = " + lastimsi);
        if (!TextUtils.equals(imsi, lastimsi)) {
            SimProviderAction.popDialogIfNeed(context, subId,  null);
            Log.d(TAG, "popDialogIfNeed");
        }
    }
}
