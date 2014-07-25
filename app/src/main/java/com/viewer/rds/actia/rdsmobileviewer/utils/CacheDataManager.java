package com.viewer.rds.actia.rdsmobileviewer.utils;

import com.viewer.rds.actia.rdsmobileviewer.CRDSCustom;
import com.viewer.rds.actia.rdsmobileviewer.DriverCardData;
import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igaglioti on 15/07/2014.
 */

public class CacheDataManager
        /*implements DownloadUtility.IRemoteDownloadDataListener*/{

    private static CacheDataManager ourInstance = new CacheDataManager();

    public static CacheDataManager getInstance() {
        return ourInstance;
    }

    private CacheDataManager() {

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

    public List<MainContractorData> getCustomers() {
        return mCachedMainContractors;
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
}
