
package com.android.settings.simprovider.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ApnData implements Parcelable {
    public String name = "";
    public String apn = "";
    public String key = "";
    public String type = "";
    public String mvnoType = "";
    public String mvnoMatchData = "";
    public int sourcetype = -1;

    public ApnData() {
    }

    protected ApnData(Parcel in) {
        name = in.readString();
        apn = in.readString();
        key = in.readString();
        type = in.readString();
        mvnoType = in.readString();
        mvnoMatchData = in.readString();
        sourcetype = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(apn);
        dest.writeString(key);
        dest.writeString(type);
        dest.writeString(mvnoType);
        dest.writeString(mvnoMatchData);
        dest.writeInt(sourcetype);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApnData> CREATOR = new Creator<ApnData>() {
        @Override
        public ApnData createFromParcel(Parcel in) {
            return new ApnData(in);
        }

        @Override
        public ApnData[] newArray(int size) {
            return new ApnData[size];
        }
    };

    @Override
    public String toString() {
        return "ApnData{" +
                "name='" + name + '\'' +
                ", apn='" + apn + '\'' +
                ", key='" + key + '\'' +
                ", type='" + type + '\'' +
                ", mvnoType='" + mvnoType + '\'' +
                ", mvnoMatchData='" + mvnoMatchData + '\'' +
                ", sourcetype=" + sourcetype +
                '}';
    }
}
