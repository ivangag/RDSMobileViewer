package com.viewer.rds.actia.rdsmobileviewer.utils;

import android.content.ContentValues;
import android.content.Context;

import com.viewer.rds.actia.rdsmobileviewer.CRDSCustom;
import com.viewer.rds.actia.rdsmobileviewer.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.DriverCardData;
import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.ResultOperation;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;
import com.viewer.rds.actia.rdsmobileviewer.db.RDSDBMapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igaglioti on 15/07/2014.
 */

public class CacheDataManager
        /*implements DownloadUtility.IRemoteDownloadDataListener*/{

    WeakReference<Context> mContext = null;
    private static CacheDataManager ourInstance = new CacheDataManager();

    public static CacheDataManager getInstance() {
        return ourInstance;
    }

    private CacheDataManager() {

    }

    public void setContext(Context context){
        if(mContext == null)
            mContext = new WeakReference<Context>(context);
    }



    private Map<String,List<VehicleCustom>> mCachedCustomersVehicles        = new HashMap<String, List<VehicleCustom>>();
    private Map<String,List<DriverCardData>> mCachedCustomersDrivers        = new HashMap<String, List<DriverCardData>>();
    private Map<String,List<CRDSCustom>> mCachedCustomersCRDS               = new HashMap<String, List<CRDSCustom>>();

    private List<MainContractorData> mCachedMainContractors         = new ArrayList<MainContractorData>();
    private List<VehicleCustom> mCachedVehiclesNotTrusted           = new ArrayList<VehicleCustom>();
    private List<CRDSCustom> mCachedCRDSNotTrusted                  = new ArrayList<CRDSCustom>();
    private List<DriverCardData> mCachedDriversNotTrusted           = new ArrayList<DriverCardData>();

    public void UpdateCache(DownloadRequestSchema requestType, Object result) {
        switch (requestType.getDownloadRequestType()) {
            case CRDS_NOT_TRUSTED:
                setCachedCRDSNotTrustedListData((List<CRDSCustom>) result);
                break;
            case CUSTOMERS_LIST:
                setCachedCustomerListData((List<MainContractorData>) result);
                break;
            case VEHICLE_NOT_TRUSTED:
                setCachedVehicleNotTrustedListData((List<VehicleCustom>) result);
                break;
            case VEHICLES_OWNED:
                mCachedCustomersVehicles.put(requestType.getUniqueCustomerCode(), (List<VehicleCustom>) result);
                break;
            case DRIVERS_OWNED:
                mCachedCustomersDrivers.put(requestType.getUniqueCustomerCode(), (List<DriverCardData>) result);
                break;
            case CRDS_OWNED:
                mCachedCustomersCRDS.put(requestType.getUniqueCustomerCode(), (List<CRDSCustom>) result);
                break;
            case DRIVERS_NOT_TRUSTED:
                setCachedDrivesNotTrustedListData((List<DriverCardData>) result);
                break;
        }
    }

    private void setCachedDrivesNotTrustedListData(List<DriverCardData> result) {
        this.mCachedDriversNotTrusted = result;
    }

    private void setCachedCRDSNotTrustedListData(List<CRDSCustom> result) {
        this.mCachedCRDSNotTrusted = result;
    }

    public List<MainContractorData> getCachedCustomerListData()
    {
        return mCachedMainContractors;
    }

    private void setCachedCustomerListData(List<MainContractorData> data)
    {
        mCachedMainContractors = data;
    }

    private void setCachedVehicleNotTrustedListData(List<VehicleCustom> data) {
        mCachedVehiclesNotTrusted = data;
    }

    public List<VehicleCustom> getVehicleNotTrusted() {
      return mCachedVehiclesNotTrusted;
    }

    public List<CRDSCustom> getCRDSNotTrusted() {
        return mCachedCRDSNotTrusted;
    }

    private void setCRDSNotTrusted(List<CRDSCustom> mCRDSNotTrusted) {
        this.mCachedCRDSNotTrusted = mCRDSNotTrusted;
    }


    public List<DriverCardData> getDriversNotTrusted() {
        return mCachedDriversNotTrusted;
    }

    public List<MainContractorData> getCustomers(Context context) {

        return RDSDBMapper.getInstance(context).queryAllCustomers();
        //return mCachedMainContractors;
    }


    public List<CRDSCustom> getCustomerCRDS(String Ancodice) {
        List<CRDSCustom> res = new ArrayList<CRDSCustom>();
        if (mCachedCustomersCRDS.containsKey(Ancodice))
            res = mCachedCustomersCRDS.get(Ancodice);
        return res;
    }

    public List<VehicleCustom> getCustomerVehicles(String Ancodice) {
        List<VehicleCustom> res = new ArrayList<VehicleCustom>();
        if (mCachedCustomersVehicles.containsKey(Ancodice))
            res = mCachedCustomersVehicles.get(Ancodice);
        return res;
    }

    public List<DriverCardData> getCustomerDrivers(String Ancodice) {
        List<DriverCardData> res = new ArrayList<DriverCardData>();
        if (mCachedCustomersDrivers.containsKey(Ancodice))
            res = mCachedCustomersDrivers.get(Ancodice);
        return res;
    }

    public boolean hasCustomers()
    {
        return mCachedMainContractors.size() > 0;
    }


    public boolean hasCustomerVehicles(String Ancodice)
    {
        return mCachedCustomersVehicles.containsKey(Ancodice);
    }

    public boolean hasCustomerDrivers(String Ancodice)
    {
        return mCachedCustomersDrivers.containsKey(Ancodice);
    }

    public boolean hasCustomerCRDS(String Ancodice)
    {
        return mCachedCustomersCRDS.containsKey(Ancodice);
    }

    public boolean hasVehiclesNotTrusted()
    {
        return mCachedVehiclesNotTrusted.size() > 0;
    }

    public boolean hasDriversNotTrusted()
    {
        return mCachedDriversNotTrusted.size() > 0;
    }

    public boolean hasCRDSNotTrusted()
    {
        return mCachedCRDSNotTrusted.size() > 0;
    }

    public Object getValue(DownloadRequestSchema downloadRequestSchema) {

        Object result = null;
        final String customerCode = downloadRequestSchema.getUniqueCustomerCode();
        final String vehicleVIN = downloadRequestSchema.getVehicleVIN();
        switch (downloadRequestSchema.getDownloadRequestType())
        {
            case VEHICLE_NOT_TRUSTED:
                result = CacheDataManager.getInstance().getVehicleNotTrusted();
                break;
            case CRDS_NOT_TRUSTED:
                result = CacheDataManager.getInstance().getCRDSNotTrusted();
                break;
            case CUSTOMERS_LIST:
                result = CacheDataManager.getInstance().getCachedCustomerListData();
                break;
            case VEHICLES_OWNED:
                result = CacheDataManager.getInstance().getCustomerVehicles(customerCode);
                break;
            case DRIVERS_OWNED:
                result = CacheDataManager.getInstance().getCustomerDrivers(customerCode);
                break;
            case CRDS_OWNED:
                result = CacheDataManager.getInstance().getCustomerCRDS(customerCode);
                break;
            case DRIVERS_NOT_TRUSTED:
                result = CacheDataManager.getInstance().getDriversNotTrusted();
                break;
            case MAIN_MENU:
                break;
            case VEHICLE_DIAGNOSTIC:
                break;
        }
        return result;
    }



    public static boolean checkedCachedDataPresence(ResultOperation result, DownloadRequestSchema requestInfo) {
        boolean mHasCacheData = false;
        switch (requestInfo.getDownloadRequestType())
        {
            case VEHICLE_NOT_TRUSTED:
                if((mHasCacheData = CacheDataManager.getInstance().hasVehiclesNotTrusted())){
                    result.setClassReturn(CacheDataManager.getInstance().getVehicleNotTrusted());
                }
                break;
            case CRDS_NOT_TRUSTED:
                if((mHasCacheData = CacheDataManager.getInstance().hasCRDSNotTrusted())){
                    result.setClassReturn(CacheDataManager.getInstance().getCRDSNotTrusted());
                }
                break;
            case DRIVERS_OWNED:
                if((mHasCacheData = CacheDataManager.getInstance().hasCustomerDrivers(requestInfo.getUniqueCustomerCode()))){
                    result.setClassReturn(CacheDataManager.getInstance().getCustomerDrivers(requestInfo.getUniqueCustomerCode()));
                }
                break;
            case CRDS_OWNED:
                if((mHasCacheData = CacheDataManager.getInstance().hasCustomerCRDS(requestInfo.getUniqueCustomerCode()))){
                    result.setClassReturn(CacheDataManager.getInstance().getCustomerCRDS(requestInfo.getUniqueCustomerCode()));
                }
                break;
            case DRIVERS_NOT_TRUSTED:
                if((mHasCacheData = CacheDataManager.getInstance().hasDriversNotTrusted())){
                    result.setClassReturn(CacheDataManager.getInstance().getDriversNotTrusted());
                }
                break;
            case CUSTOMERS_LIST:
                if((mHasCacheData = CacheDataManager.getInstance().hasCustomers())){
                    result.setClassReturn(CacheDataManager.getInstance().getCustomers(null));
                }
                break;
            case VEHICLES_OWNED:
                if((mHasCacheData = CacheDataManager.getInstance().hasCustomerVehicles(requestInfo.getUniqueCustomerCode()))){
                    result.setClassReturn(CacheDataManager.getInstance().getCustomerVehicles(requestInfo.getUniqueCustomerCode()));
                }
                break;
            case MAIN_MENU:
                break;
            case VEHICLE_DIAGNOSTIC:
                break;
        }
        result.setStatus(mHasCacheData);
        return mHasCacheData;
    }

    public String getDownloadRepository(String uuidDownload) {
        return RDSDBMapper.getInstance(mContext.get()).getDownloadRepository(uuidDownload);
    }
}
