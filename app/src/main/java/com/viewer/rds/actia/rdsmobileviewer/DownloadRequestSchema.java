package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRDSManager;

/**
 * Created by igaglioti on 16/07/2014.
 */
public class DownloadRequestSchema implements Parcelable {


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
          .append("RequestType:").append(mDownloadRequestType.toString())
          .append("Customer:").append(mUniqueCustomerCode)
          .append("Vehicle:").append(mVehicleVIN)
          .append("CacheRequested:").append(mObtainCacheIfExist);

        return sb.toString();

    }

    private boolean mObtainCacheIfExist;

    private String mVehicleVIN = "";

    private DownloadRDSManager.DownloadRequestType mDownloadRequestType;

    private String mUniqueCustomerCode = "";

    private DownloadRequestSchema() {

    }


    public DownloadRequestSchema(Parcel in) {

        boolean[] val = new boolean[1];
        in.readBooleanArray(val);
        mObtainCacheIfExist = val[0];
        mVehicleVIN = in.readString();
        mDownloadRequestType = Enum.valueOf(DownloadRDSManager.DownloadRequestType.class,in.readString());
        mUniqueCustomerCode = in.readString();
    }

    public static DownloadRequestSchema newInstance(DownloadRDSManager.DownloadRequestType requestType,
                                                    boolean obtainCacheIfExist) {
        DownloadRequestSchema schema = new DownloadRequestSchema();
        schema.setDownloadRequestType(requestType);
        schema.setCacheOption(obtainCacheIfExist);
        return schema;
    }

    public static DownloadRequestSchema newInstance(DownloadRDSManager.DownloadRequestType requestType, String customerAncodice,
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
    public DownloadRDSManager.DownloadRequestType getDownloadRequestType() {
        return mDownloadRequestType;
    }

    public DownloadRequestSchema setDownloadRequestType(DownloadRDSManager.DownloadRequestType mDownloadRequestType) {
        this.mDownloadRequestType = mDownloadRequestType;
        return this;
    }

    private DownloadRequestSchema(DownloadRDSManager.DownloadRequestType requestType) {
        this.mDownloadRequestType = requestType;
    }


    public String getUniqueCustomerCode() {
        return mUniqueCustomerCode;
    }

    public DownloadRequestSchema setUniqueCustomerCode(String mUniqueCustomerCode) {
        this.mUniqueCustomerCode = mUniqueCustomerCode;
        return this;
    }


    public String getVehicleVIN() {
        return mVehicleVIN;
    }

    public DownloadRequestSchema setVehicleVIN(String mVehicleVIN) {
        this.mVehicleVIN = mVehicleVIN;
        return this;
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
