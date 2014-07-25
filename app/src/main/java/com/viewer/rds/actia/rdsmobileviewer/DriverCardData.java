package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

/**
 * Created by igaglioti on 11/07/2014.
 */
public class DriverCardData implements Parcelable {

    private String status;

    public DriverCardData(){}
    private String mCustomerName;

    private String mIdCard;

    private String mName;

    private String mInsertingDate;

    private String mBindingDate;

    private String mDeviceAnnouncer;

    private boolean mIsActivated;

    public DriverCardData(Parcel in) {

    }

    public String getCustomerName() {
        return mCustomerName;
    }

    public void setCustomerName(String mCustomerName) {
        this.mCustomerName = mCustomerName;
    }

    public String getIdCard() {
        return mIdCard;
    }

    public void setIdCard(String mIdCard) {
        this.mIdCard = mIdCard;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getInsertingDate() {
        return mInsertingDate;
    }

    public void setInsertingDate(String mInsertingDate) {
        this.mInsertingDate = Utils.getDateTimeFromTicks(mInsertingDate);
    }

    public String getBindingDate() {
        return mBindingDate;
    }

    public void setBindingDate(String mBindingDate) {
        this.mBindingDate = Utils.getDateTimeFromTicks(mBindingDate);
    }

    public String getDeviceAnnouncer() {
        return mDeviceAnnouncer;
    }

    public void setDeviceAnnouncer(String mDeviceAnnouncer) {
        this.mDeviceAnnouncer = mDeviceAnnouncer;
    }

    public boolean ismIsActivated() {
        return mIsActivated;
    }

    public void setIsActivated(boolean mIsActivated) {
        this.mIsActivated = mIsActivated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<DriverCardData> CREATOR
            = new Parcelable.Creator<DriverCardData>() {
        public DriverCardData createFromParcel(Parcel in) {
            return new DriverCardData(in);
        }

        public DriverCardData[] newArray(int size) {
            return new DriverCardData[size];
        }
    };

    @Override
    public String toString()
    {
        String res = String.format("IdCard:%s\t\nName:%s\t\nInsertDate:%s\t\nDeviceAnnouncer:%s\t\nCustomer:%s\t\nIsActivated:%b\t\nBindingDate:%s",
                mIdCard,mName,mInsertingDate,mDeviceAnnouncer,mCustomerName,mIsActivated,mBindingDate);
        return res;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
