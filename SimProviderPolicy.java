
package com.android.settings.simprovider;

import android.options.wind.WindFeatureOptions;
import android.text.TextUtils;

import com.android.settings.simprovider.algb.ALGBConfig;
import com.android.settings.simprovider.data.SimProviderData;

import java.util.ArrayList;
import java.util.HashMap;


public class SimProviderPolicy {

    private static final HashMap<String, SimProviderPolicy> SimProviderPolicyMap = new HashMap<>();


    static {
        if (WindFeatureOptions.WIND_DEF_OPTR_A155_ALGB || WindFeatureOptions.WIND_DEF_OPTR_A155_EU2S) {
            ALGBConfig.init();
        }
    }

    public static SimProviderPolicy getSimProviderPolicy(String mccmnc) {
        return SimProviderPolicyMap.get(mccmnc);
    }


    public final String mccmnc;
    private final ArrayList<SimProviderData> simProviderDatas = new ArrayList<>();
    private int _count = 0;

    public SimProviderPolicy(String mccmnc) {
        this.mccmnc = mccmnc;
        SimProviderPolicyMap.put(mccmnc, this);
    }

    public SimProviderData findSimProviderData(String name, String apn) {
        for (SimProviderData simProviderData : simProviderDatas) {
            if (TextUtils.equals(simProviderData.getName(), name)
                    && ("*".equals(simProviderData.getApn()) || TextUtils.equals(simProviderData.getApn(), apn))) {
                return simProviderData;
            }
            if (TextUtils.equals(simProviderData.getMmsName(), name)
                    && TextUtils.equals(simProviderData.getMmsApn(), apn)) {
                return simProviderData;
            }
        }
        return null;
    }

    public SimProviderData add(SimProviderData simProviderData) {
        simProviderData.setIndex(_count++);
        simProviderDatas.add(simProviderData);
        return simProviderData;
    }

    public SimProviderData add(String title, String name, String apn) {
        return add(new SimProviderData(title, name, apn));
    }
}
