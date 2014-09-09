package com.viewer.rds.actia.rdsmobileviewer.utils;

import android.content.Context;
import android.util.Log;

import com.viewer.rds.actia.rdsmobileviewer.CRDSCustom;
import com.viewer.rds.actia.rdsmobileviewer.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.DriverCardData;
import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.ResultOperation;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;
import com.viewer.rds.actia.rdsmobileviewer.db.RDSDBMapper;

import org.json.JSONException;

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
    private final static String TAG = CacheDataManager.class
            .getCanonicalName();

    WeakReference<Context> mContext = null;
    private static CacheDataManager ourInstance = new CacheDataManager();

    public static CacheDataManager get() {
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

    private List<MainContractorData> getCustomers() {

        // try to refresh data from db
        List<MainContractorData> res = null;
        String jsonStream = RDSDBMapper.get(mContext.get()).getDownloadRepository(DownloadRequestSchema.newInstance().
                setDownloadRequestType(DownloadRDSManager.DownloadRequestType.CUSTOMERS_LIST));
        try {
            res  = ParserFactory.parseJsonToRDSRemoteEntity(jsonStream, DownloadRDSManager.DownloadRequestType.CUSTOMERS_LIST);
            mCachedMainContractors = res;
        } catch (JSONException e) {
            Log.e(TAG,"Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return res;
    }

    private void setCachedCustomerListData(List<MainContractorData> data)
    {
        mCachedMainContractors = data;
    }

    private void setCachedVehicleNotTrustedListData(List<VehicleCustom> data) {
        mCachedVehiclesNotTrusted = data;
    }

    private List<VehicleCustom> getVehicleNotTrusted() {
        // try to refresh data from db
        List<VehicleCustom> res = null;
        String jsonStream = RDSDBMapper.get(mContext.get()).getDownloadRepository(DownloadRequestSchema.newInstance().
                setDownloadRequestType(DownloadRDSManager.DownloadRequestType.VEHICLE_NOT_TRUSTED));
        try {
            res = ParserFactory.parseJsonToRDSRemoteEntity(jsonStream, DownloadRDSManager.DownloadRequestType.VEHICLE_NOT_TRUSTED);
            mCachedVehiclesNotTrusted = res;
        } catch (JSONException e) {
            Log.e(TAG,"Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
      return res;
    }

    private List<CRDSCustom> getCRDSNotTrusted() {
        // try to refresh data from db
        List<CRDSCustom> res = null;
        String jsonStream = RDSDBMapper.get(mContext.get()).getDownloadRepository(DownloadRequestSchema.newInstance().
                setDownloadRequestType(DownloadRDSManager.DownloadRequestType.CRDS_NOT_TRUSTED));
        try {
            res = ParserFactory.parseJsonToRDSRemoteEntity(jsonStream, DownloadRDSManager.DownloadRequestType.CRDS_NOT_TRUSTED);
            mCachedCRDSNotTrusted = res;
        } catch (JSONException e) {
            Log.e(TAG,"Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return res;
    }

    private void setCRDSNotTrusted(List<CRDSCustom> mCRDSNotTrusted) {
        this.mCachedCRDSNotTrusted = mCRDSNotTrusted;
    }


    private List<DriverCardData> getDriversNotTrusted() {
        // try to refresh data from db
        List<DriverCardData> res = null;
        String jsonStream = RDSDBMapper.get(mContext.get()).getDownloadRepository(DownloadRequestSchema.newInstance().
                setDownloadRequestType(DownloadRDSManager.DownloadRequestType.DRIVERS_NOT_TRUSTED));
        try {
            res = ParserFactory.parseJsonToRDSRemoteEntity(jsonStream, DownloadRDSManager.DownloadRequestType.DRIVERS_NOT_TRUSTED);
            mCachedDriversNotTrusted = res;
        } catch (JSONException e) {
            Log.e(TAG,"Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return res;
    }


    private List<CRDSCustom> getCustomerCRDS(String Ancodice) {

        List<CRDSCustom> res = null;
        // try to refresh data from db
        Log.d(TAG, "getCustomerCRDS. mContext:" + (mContext.get() != null));
        DownloadRequestSchema downloadRequestSchema = DownloadRequestSchema.newInstance().
                setDownloadRequestType(DownloadRDSManager.DownloadRequestType.CRDS_OWNED).setUniqueCustomerCode(Ancodice);
        String jsonStream = RDSDBMapper.get(mContext.get()).getDownloadRepository(downloadRequestSchema);
        try {
            res = ParserFactory.parseJsonToRDSRemoteEntity(jsonStream, DownloadRDSManager.DownloadRequestType.CRDS_OWNED);
            mCachedCustomersCRDS.put(Ancodice,res);
        } catch (JSONException e) {
            Log.e(TAG,"Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return res;
    }

    private List<VehicleCustom> getCustomerVehicles(String Ancodice) {
        List<VehicleCustom> res = null;
        // try to refresh data from db
        String jsonStream = RDSDBMapper.get(mContext.get()).getDownloadRepository(DownloadRequestSchema.newInstance().
                setDownloadRequestType(DownloadRDSManager.DownloadRequestType.VEHICLES_OWNED));
        try {
            res = ParserFactory.parseJsonToRDSRemoteEntity(jsonStream, DownloadRDSManager.DownloadRequestType.VEHICLES_OWNED);
            mCachedCustomersVehicles.put(Ancodice,res);
        } catch (JSONException e) {
            Log.e(TAG,"Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return res;
    }

    private List<DriverCardData> getCustomerDrivers(String Ancodice) {

        List<DriverCardData> res = null;
        // try to refresh data from db
        String jsonStream = RDSDBMapper.get(mContext.get()).getDownloadRepository(DownloadRequestSchema.newInstance().
                setDownloadRequestType(DownloadRDSManager.DownloadRequestType.DRIVERS_OWNED));
        try {
            res = ParserFactory.parseJsonToRDSRemoteEntity(jsonStream, DownloadRDSManager.DownloadRequestType.DRIVERS_OWNED);
            mCachedCustomersDrivers.put(Ancodice,res);
        } catch (JSONException e) {
            Log.e(TAG,"Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return res;
    }

    /*
    public boolean hasCustomers()
    {
        return mCachedMainContractors.size() > 0;
    }
    */


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

    public Object getValue(DownloadRequestSchema downloadRequestSchema) throws RDSEmptyDataException {

        Object result = null;
        final String customerCode = downloadRequestSchema.getUniqueCustomerCode();
        final String vehicleVIN = downloadRequestSchema.getVehicleVIN();
        Log.d(TAG, "getValue: " + downloadRequestSchema.getUniqueCustomerCode() + " " + downloadRequestSchema.getDownloadRequestType());
        switch (downloadRequestSchema.getDownloadRequestType())
        {
            case VEHICLE_NOT_TRUSTED:
                result = CacheDataManager.get().getVehicleNotTrusted();
                break;
            case CRDS_NOT_TRUSTED:
                result = CacheDataManager.get().getCRDSNotTrusted();
                break;
            case CUSTOMERS_LIST:
                result = CacheDataManager.get().getCustomers();
                break;
            case VEHICLES_OWNED:
                result = CacheDataManager.get().getCustomerVehicles(customerCode);
                break;
            case DRIVERS_OWNED:
                result = CacheDataManager.get().getCustomerDrivers(customerCode);
                break;
            case CRDS_OWNED:
                Log.d(TAG, "getValue: " + downloadRequestSchema.getDownloadRequestType());
                result = CacheDataManager.get().getCustomerCRDS(customerCode);
                break;
            case DRIVERS_NOT_TRUSTED:
                result = CacheDataManager.get().getDriversNotTrusted();
                break;
            case MAIN_MENU:
                break;
            case VEHICLE_DIAGNOSTIC:
                break;
        }
        Log.d(TAG, "Value: " + (result != null));
        if (result == null) {
            Log.d(TAG, "Raise RDSEmptyDataException");
            throw new RDSEmptyDataException("RDS request: " + downloadRequestSchema.toString());
        }
        return result;
    }



    public static boolean existData(ResultOperation result, DownloadRequestSchema requestInfo){
        boolean mHasCacheData;
        Log.d(TAG, "existData");
        try {
            result.setClassReturn(CacheDataManager.get().getValue(requestInfo));
            Log.d(TAG, "existData::result.setClassReturn");
            mHasCacheData = result.getClassReturn() != null;
        } catch (RDSEmptyDataException e) {
            Log.d(TAG, "existData::RDSEmptyDataException");
            mHasCacheData = false;
        }
        Log.d(TAG, "existData::setStatus");
        result.setStatus(mHasCacheData);
        return mHasCacheData;
    }

    public String getDownloadRepository(String uuidDownload) throws RDSEmptyDataException {
        String res =  RDSDBMapper.get(mContext.get()).getDownloadRepository(uuidDownload);
        if(res.isEmpty())
            throw  new RDSEmptyDataException("Repo Empty: " + uuidDownload);
        return  res;
    }
    public String getDownloadRepository(DownloadRequestSchema downloadRequestSchema) throws RDSEmptyDataException {
        String res = RDSDBMapper.get(mContext.get()).getDownloadRepository(downloadRequestSchema);
        if(res.isEmpty())
            throw  new RDSEmptyDataException("Repo Empty: " + downloadRequestSchema.toString());
        return  res;
    }

    public String saveDownloadRepository(DownloadRequestSchema downloadRequestSchema, String jsonStream) {

        return RDSDBMapper.get(mContext.get()).saveDownloadToRepository(downloadRequestSchema,jsonStream);
    }
}
