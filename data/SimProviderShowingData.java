
package com.android.settings.simprovider.data;


public class SimProviderShowingData implements Comparable<SimProviderShowingData>{
    private final SimProviderData simProviderData;

    public SimProviderShowingData(SimProviderData simProviderData) {
        this.simProviderData = simProviderData;
    }

    private String key = "";
    private String mmsKey = "";

    public SimProviderData getSimProviderData() {
        return simProviderData;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMmsKey() {
        return mmsKey;
    }

    public void setMmsKey(String mmsKey) {
        this.mmsKey = mmsKey;
    }

    @Override
    public int compareTo(SimProviderShowingData o) {
        return simProviderData.compareTo(o.simProviderData);
    }
}
