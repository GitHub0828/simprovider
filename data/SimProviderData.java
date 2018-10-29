
package com.android.settings.simprovider.data;

import android.text.TextUtils;


public class SimProviderData implements Comparable<SimProviderData> {
    private final String title;
    private final String name;
    private final String apn;
    private String mmsName;
    private String mmsApn;
    private int index = -1;
    private boolean onlyInternet = false;

    public SimProviderData(String title, String name, String apn) {
        this.title = title;
        this.name = name;
        this.apn = apn;
    }

    public String getTitle() {
        return title;
    }


    public String getName() {
        return name;
    }

    public String getApn() {
        return apn;
    }

    public String getMmsName() {
        return mmsName;
    }


    public String getMmsApn() {
        return mmsApn;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isOnlyInternet() {
        return onlyInternet;
    }

    public SimProviderData onlyInternet() {
        this.onlyInternet = true;
        return this;
    }

    public SimProviderData addMms(String mmsName, String mmsApn) {
        this.mmsName = mmsName;
        this.mmsApn = mmsApn;
        return this;
    }
/*
    public SimProviderData newInstance() {
        return new SimProviderData(titleRes, name, apn).addMms(mmsName, mmsApn);
    }*/


    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SimProviderData) {
            SimProviderData obj2 = (SimProviderData) obj;
            return apn.equals(obj2.apn) && name.equals(obj2.name)
                    && TextUtils.equals(mmsName, obj2.mmsName)
                    && TextUtils.equals(mmsApn, obj2.mmsApn);
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(SimProviderData otherObject) {
        return index - otherObject.index;
    }

    @Override
    public String toString() {
        return "SimProvider{" +
                ", name='" + name + '\'' +
                ", apn='" + apn + '\'' +
                ", index=" + index +
                ", mmsName='" + mmsName + '\'' +
                ", mmsApn='" + mmsApn + '\'' +
                '}';
    }
}
