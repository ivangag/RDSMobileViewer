package com.viewer.rds.actia.rdsmobileviewer.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by igaglioti on 16/07/2014.
 */
public class DownloadRequestSchema implements Parcelable {


    private boolean mObtainCacheIfExist;

    private String mVehicleVIN = "";

    private DownloadUtility.DownloadRequestType mDownloadRequestType;

    private String mUniqueCustomerCode = "";

    private DownloadRequestSchema() {

    }


    public DownloadRequestSchema(Parcel in) {

        boolean[] val = new boolean[1];
        in.readBooleanArray(val);
        mObtainCacheIfExist = val[0];
        mVehicleVIN = in.readString();
        mDownloadRequestType = Enum.valueOf(DownloadUtility.DownloadRequestType.class,in.readString());
        mUniqueCustomerCode = in.readString();
    }

    public static DownloadRequestSchema newInstance(DownloadUtility.DownloadRequestType requestType,
                                                    boolean obtainCacheIfExist) {
        DownloadRequestSchema schema = new DownloadRequestSchema();
        schema.setDownloadRequestType(requestType);
        schema.setCacheOption(obtainCacheIfExist);
        return schema;
    }

    public static DownloadRequestSchema newInstance(DownloadUtility.DownloadRequestType requestType, String customerAncodice,
                                                    String vehicleVIN, boolean obtainCacheIfExist)
    {

        DownloadRequestSchema schema = new DownloadRequestSchema();
        schema.setCacheOption(obtainCacheIfExist);
        schema.setDownloadRequestType(requestType);
        schema.setUniqueCustomerCode(customerAncodice);
        schema.setVehicleVIN(vehicleVIN);
        return schema;
    }


    public void setCacheOption(boolean cacheOption) {
        this.mObtainCacheIfExist = cacheOption;
    }
    public DownloadUtility.DownloadRequestType getDownloadRequestType() {
        return mDownloadRequestType;
    }

    public void setDownloadRequestType(DownloadUtility.DownloadRequestType mDownloadRequestType) {
        this.mDownloadRequestType = mDownloadRequestType;
    }

    private DownloadRequestSchema(DownloadUtility.DownloadRequestType requestType) {
        this.mDownloadRequestType = requestType;
    }


    public String getUniqueCustomerCode() {
        return mUniqueCustomerCode;
    }

    public void setUniqueCustomerCode(String mUniqueCustomerCode) {
        this.mUniqueCustomerCode = mUniqueCustomerCode;
    }


    public String getVehicleVIN() {
        return mVehicleVIN;
    }

    public void setVehicleVIN(String mVehicleVIN) {
        this.mVehicleVIN = mVehicleVIN;
    }

    public static DownloadRequestSchema newInstance() {

        DownloadRequestSchema requestSchema = new DownloadRequestSchema();
        requestSchema.setCacheOption(false);
        return requestSchema;
    }

    public boolean getCacheOption() {
        return mObtainCacheIfExist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[]{mObtainCacheIfExist});
        dest.writeString(mVehicleVIN);
        dest.writeString(mDownloadRequestType.toString());
        dest.writeString(mUniqueCustomerCode);
    }

    public static final Parcelable.Creator<DownloadRequestSchema> CREATOR
            = new Parcelable.Creator<DownloadRequestSchema>() {
        public DownloadRequestSchema createFromParcel(Parcel in) {
            return new DownloadRequestSchema(in);
        }

        public DownloadRequestSchema[] newArray(int size) {
            return new DownloadRequestSchema[size];
        }
    };

}
