package com.viewer.rds.actia.rdsmobileviewer.utils;

/**
 * Created by igaglioti on 16/07/2014.
 */
public class DownloadRequestSchema {


    private boolean mObtainCacheIfExist;

    private String mVehicleVIN = "";

    private DownloadUtility.DownloadRequestType mDownloadRequestType;

    private String mUniqueCustomerCode = "";

    private DownloadRequestSchema() {

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
}
