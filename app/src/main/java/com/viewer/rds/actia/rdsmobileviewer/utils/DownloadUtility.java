package com.viewer.rds.actia.rdsmobileviewer.utils;

import android.app.Activity;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.viewer.rds.actia.rdsmobileviewer.CRDSCustom;
import com.viewer.rds.actia.rdsmobileviewer.DriverCardData;
import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.ResultOperation;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by igaglioti on 09/07/2014.
 */
public class DownloadUtility {

    private final static String TAG = DownloadUtility.class
            .getCanonicalName();
    private final static String RDSRestFulURL = "http://iob.actiaitalia.com/ACTIA.ARDIS.RESTFul/Service.svc/json/";

    private final static String mWSVehiclesNotTrustedCallName   = "GetVehiclesNotActivated";
    private final static String mWSCRDSNotTrustedCallName       = "GetCRDSNotActivated";
    private final static String mWSDriversNotTrustedCallName    = "GetCardNotActivated";
    private final static String mWSVCustomersCallName           = "GetCustomers";

    private final static String mWSVCustomersCRDSCallName       = "GetCRDSFromCompany/%s";
    private final static String mWSVCustomersDriversCallName    = "GetCustomerDrivers/%s";
    private final static String mWSVCustomersVehiclesCallName   = "GetCustomerVehicles/%s";
    //private final static String mWSVCustomersVehiclesCallNameResOp   = "GetCustomerVehiclesResOp/%s";
    private final static String mWSVVehicleDataDiagnosticCallName   = "Vehicle/Diagnostic/%s";

    private final static String classReturn  = "ClassReturn";
    private final static String otherInfo  = "OtherInfo";
    private final static String status  = "Status";

    private static DownloadUtility ourInstance = new DownloadUtility();

    public static DownloadUtility getInstance() {
        return ourInstance;
    }

    private DownloadUtility() {
    }

    private Set<IRemoteDownloadDataListener> mListeners = new HashSet<IRemoteDownloadDataListener>();

    public void addListener(IRemoteDownloadDataListener listener) {
        if(!mListeners.contains(listener))
            mListeners.add(listener);
    }

    public void removeAllListeners()
    {
        mListeners.clear();
    }
    public void removeListener(IRemoteDownloadDataListener listener) {
        mListeners.remove(listener);
    }

    public void notifyListeners(DownloadRequestSchema requestType, Object result) {
        for (IRemoteDownloadDataListener listener : mListeners) {
            listener.onDownloadDataFinished(requestType,result);
        }
    }

    public interface IRemoteDownloadDataListener {
        public void onDownloadDataFinished(DownloadRequestSchema requestType, Object result);
    }

    public static enum DownloadRequestType
    {
        VEHICLE_NOT_TRUSTED,
        CRDS_NOT_TRUSTED,
        CUSTOMERS_LIST,
        VEHICLES_OWNED,
        DRIVERS_OWNED,
        CRDS_OWNED,
        DRIVERS_NOT_TRUSTED,
        MAIN_MENU,
        VEHICLE_DIAGNOSTIC;

        public String getLocalizedName(Context context){
        switch (this){
            case VEHICLE_NOT_TRUSTED:
                return context.getResources().getString(R.string.VehicleNotTrustedTitle);
            case CRDS_NOT_TRUSTED:
                return context.getResources().getString(R.string.CRDSNotTrustedTitle);
            case CUSTOMERS_LIST:
                return context.getResources().getString(R.string.CustomersListTitle);
            case VEHICLES_OWNED:
                return context.getResources().getString(R.string.VehiclesTitle);
            case DRIVERS_OWNED:
                return context.getResources().getString(R.string.DriversTitle);
            case CRDS_OWNED:
                return context.getResources().getString(R.string.CRDSTitle);
            case DRIVERS_NOT_TRUSTED:
                return context.getResources().getString(R.string.DriversNotTrustedTitle);
            case MAIN_MENU:
                break;
            case VEHICLE_DIAGNOSTIC:
                return context.getResources().getString(R.string.DiagnosticDataTitle);
        }
            return  "";
        }

        public String getWSCallName(DownloadRequestSchema downloadRequestType)
        {
            switch (this)
            {
                case CRDS_NOT_TRUSTED:
                    return RDSRestFulURL + mWSCRDSNotTrustedCallName;
                case VEHICLE_NOT_TRUSTED:
                    return RDSRestFulURL + mWSVehiclesNotTrustedCallName;
                case DRIVERS_NOT_TRUSTED:
                    return RDSRestFulURL + mWSDriversNotTrustedCallName;
                case DRIVERS_OWNED:
                    return String.format(RDSRestFulURL + mWSVCustomersDriversCallName,downloadRequestType.getUniqueCustomerCode());
                case CRDS_OWNED:
                    return String.format(RDSRestFulURL + mWSVCustomersCRDSCallName,downloadRequestType.getUniqueCustomerCode());
                case CUSTOMERS_LIST:
                    return RDSRestFulURL + mWSVCustomersCallName;
                case VEHICLES_OWNED:
                    return String.format(RDSRestFulURL + mWSVCustomersVehiclesCallName,downloadRequestType.getUniqueCustomerCode());
                    //return String.format(RDSRestFulURL + mWSVCustomersVehiclesCallName,downloadRequestType.getUniqueCustomerCode());
                case MAIN_MENU:
                    break;
                case VEHICLE_DIAGNOSTIC:
                    return String.format(RDSRestFulURL + mWSVVehicleDataDiagnosticCallName,downloadRequestType.getVehicleVIN());
            }
            return "";
        }
    }


    public static ResultOperation FetchingRemoteData(AndroidHttpClient client,
                                             DownloadRequestSchema downloadRequestSchema) {
        // Object that encapsulates the HTTP GET request to the
        // RDS Web service.
        final String url = downloadRequestSchema.getDownloadRequestType().getWSCallName(downloadRequestSchema);
        HttpGet request = new HttpGet(url);

        JSONResponseHandler responseHandler
                = new JSONResponseHandler(downloadRequestSchema.getDownloadRequestType());

        try {
            // Get expanded acronyms from the Web service in JSON
            // format, parse data into a List of AcronymData, and
            // return the results.
            return client.execute(request,
                    responseHandler);
        } catch (ClientProtocolException e) {
            Log.i(TAG, "ClientProtocolException");
        } catch (IOException e) {
            Log.i(TAG, "IOException");
        }
        return null;
    }

    public void RequireDownloadAsyncTask(IRemoteDownloadDataListener clientDownloadDataListener,
                                         DownloadRequestSchema downloadRequestInfo) {

        this.addListener(clientDownloadDataListener);
        DownloadDataTask downloadDataTask = new DownloadDataTask();
        downloadDataTask.execute(downloadRequestInfo);
    }


    /**
     * Object that can invoke HTTP GET requests on URLs.
     */
    private final static AndroidHttpClient mClient =
            AndroidHttpClient.newInstance("");

    static class JSONResponseHandler implements ResponseHandler<ResultOperation>
    {
        private DownloadRequestType mRequestType;

        public JSONResponseHandler(DownloadRequestType requestType)
        {
            mRequestType = requestType;
        }

        @Override
        public ResultOperation handleResponse(HttpResponse httpResponse) throws IOException {

            // A ResponseHandler that returns the response body as a
            // String for successful (2xx) responses.
            String JSONResponse =
                    new BasicResponseHandler().handleResponse(httpResponse);

            ResultOperation resultOperation = ResultOperation.newInstance();
            // Stores the processed results we get back from the
            // Web service.
            ArrayList result = null;
            try {

                // Takes a JSON source string and extracts characters
                // and tokens from it.
                JSONArray jsonArray = null;

                JSONTokener token = new JSONTokener(JSONResponse);

                if(token.nextValue() instanceof JSONArray)
                {
                    jsonArray =  (JSONArray) new JSONTokener(JSONResponse).nextValue();
                    resultOperation.setStatus(true);
                }
                else {
                    resultOperation = getResultOperation(JSONResponse,resultOperation);
                    jsonArray = (JSONArray) resultOperation.getClassReturn();
                }

                if(resultOperation.isStatus()) {
                    switch (mRequestType) {

                        case VEHICLE_DIAGNOSTIC:
                        case VEHICLES_OWNED:
                        case VEHICLE_NOT_TRUSTED:
                            result = parseVehicles(jsonArray);
                            break;
                        case CUSTOMERS_LIST:
                            result = parseCustomers(jsonArray);
                            break;
                        case CRDS_NOT_TRUSTED:
                        case CRDS_OWNED:
                            result = parseCRDS(jsonArray);
                            break;
                        case DRIVERS_OWNED:
                        case DRIVERS_NOT_TRUSTED:
                            result = parseDrivers(jsonArray);
                            break;
                        case MAIN_MENU:
                            break;
                    }
                }
            } catch (JSONException e) {
                resultOperation.setStatus(false);
                resultOperation.setOtherInfo(String.format("Exception on getResponse:%s",e.getLocalizedMessage()));
                e.printStackTrace();
            }
            finally {
                resultOperation.setClassReturn(result);
            }
            //return result;
            return resultOperation;
        }
    }

    private static ResultOperation getResultOperation(String JSONResponse, ResultOperation resultOperationInstance) throws JSONException, IOException {
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject(JSONResponse);
        if (!jsonObject.isNull(classReturn)) {

            // get ClassReturn field
            jsonArray = jsonObject.getJSONArray(classReturn);
            if (Utils.GZIP_MAGICAL == jsonArray.getInt(0)) {
                final ArrayList<Byte> arrayList = new ArrayList<Byte>();
                for (int Idx = 0; Idx < jsonArray.length(); Idx++) {
                    arrayList.add((byte) jsonArray.getInt(Idx));
                }
                byte[] jsonResArray = Utils.decompressFromGZip(new ByteArrayInputStream(Utils.convertToByteArray((arrayList))));
                jsonArray = new JSONArray(new String(jsonResArray));
            }
            // get OtherInfo field
            if(!jsonObject.isNull(otherInfo))
                resultOperationInstance.setOtherInfo(jsonObject.getString(otherInfo));

            // get Status field
            if(!jsonObject.isNull(status))
                resultOperationInstance.setStatus(jsonObject.getBoolean(status));
        }
        else
        {
            resultOperationInstance.setStatus(false);
            resultOperationInstance.setOtherInfo("ResponseError::No ClassReturn Found");
        }
        resultOperationInstance.setClassReturn(jsonArray);
        //return jsonArray;
        return resultOperationInstance;
    }

    private static ArrayList parseCustomers(JSONArray customers) throws JSONException {
        ArrayList result;
        result = new ArrayList<MainContractorData>();
        // Get the JSON array of  results, marked with
        // the 'lfs' name.
        // For each option, create an object and add it
        // to rValue.
        for (int i = 0; i < customers.length(); i++) {
            JSONObject jsonObject = customers.getJSONObject(i);
            MainContractorData remoteItem = new MainContractorData();
            if (!jsonObject.isNull("ancodice"))
                remoteItem.setAncodice(jsonObject.getString("ancodice"));
            if (!jsonObject.isNull("InsertDate"))
                remoteItem.setInsertDate(jsonObject.getString("InsertDate"));
            if (!jsonObject.isNull("FriendlyName"))
                remoteItem.setFriendlyName(jsonObject.getString("FriendlyName"));
            if (!jsonObject.isNull("IdCustomer"))
                remoteItem.setIdCustomer(jsonObject.getInt("IdCustomer"));
            if (!jsonObject.isNull("IsAutomaticDriverAssociationEnabled"))
                remoteItem.setIsAutomaticDriverAssociationEnabled(jsonObject.getBoolean("IsAutomaticDriverAssociationEnabled"));
            if (!jsonObject.isNull("IsFilePushingServiceActive"))
                remoteItem.setIsFilePushingServiceActive(jsonObject.getBoolean("IsFilePushingServiceActive"));
            if (!jsonObject.isNull("IsSuperCRDSServiceActive"))
                remoteItem.setIsSuperCRDSServiceActive(jsonObject.getBoolean("IsSuperCRDSServiceActive"));
            if (!jsonObject.isNull("IsAutomaticDriverAssociationEnabled"))
                remoteItem.setIsEmailForwardServiceActive(jsonObject.getBoolean("IsAutomaticDriverAssociationEnabled"));
            result.add(remoteItem);
        }
        return result;
    }
    private static ArrayList parseVehicles(JSONArray vehicles) throws JSONException {
        ArrayList result;
        result = new ArrayList<VehicleCustom>();
        // Get the JSON array of  results, marked with
        // the 'lfs' name.
        // For each option, create an object and add it
        // to rValue.
        // Get the JSON array of  results, marked with
        // the 'lfs' name.
        // For each option, create an object and add it
        // to rValue.
        for (int i = 0; i < vehicles.length(); i++) {
            JSONObject jsonObject = vehicles.getJSONObject(i);
            VehicleCustom veh = new VehicleCustom();
            veh.set_VIN(jsonObject.getString("VIN"));
            veh.set_IMEI(jsonObject.getString("IMEI"));
            veh.set_VRN(jsonObject.getString("VRN"));
            veh.set_IdDevice(jsonObject.getString("IdDevice"));
            veh.set_PhoneNumber(jsonObject.getString("PhoneNumber"));
            if (!jsonObject.isNull("FileContent"))
                veh.set_FileContent(jsonObject.getString("FileContent"));
            if (!jsonObject.isNull("CustomerName"))
                veh.set_CustomerName(jsonObject.getString("CustomerName"));
            if (!jsonObject.isNull("Status"))
                veh.set_Status(jsonObject.getString("Status"));
            if (!jsonObject.isNull("LastDiag"))
                veh.set_DiagnosticDeviceTime(jsonObject.getString("LastDiag"));
            if (!jsonObject.isNull("DiagnosticDeviceTime"))
                veh.set_DiagnosticDeviceTime(jsonObject.getString("DiagnosticDeviceTime"));
            if (!jsonObject.isNull("SwName"))
                veh.set_SwName(jsonObject.getString("SwName"));
            if (!jsonObject.isNull("SwVersion"))
                veh.setSwVersion(jsonObject.getString("SwVersion"));
            if (!jsonObject.isNull("JourneyEnableDate"))
                veh.set_JourneyEnableDate(jsonObject.getString("JourneyEnableDate"));
            if (!jsonObject.isNull("StartDate"))
                veh.set_StartDate(jsonObject.getString("StartDate"));
            result.add(veh);
        }
        return result;
    }
    private static ArrayList parseDrivers(JSONArray drivers) throws JSONException {
        ArrayList result;
        result = new ArrayList<DriverCardData>();
        // Get the JSON array of  results, marked with
        // the 'lfs' name.
        // For each option, create an object and add it
        // to rValue.
        // Get the JSON array of  results, marked with
        // the 'lfs' name.
        // For each option, create an object and add it
        // to rValue.
        for (int i = 0; i < drivers.length(); i++) {
            JSONObject jsonObject = drivers.getJSONObject(i);
            DriverCardData remoteItem = new DriverCardData();
            if (!jsonObject.isNull("IdCard"))
                remoteItem.setIdCard(jsonObject.getString("IdCard"));
            if (!jsonObject.isNull("InsertDate"))
                remoteItem.setInsertingDate(jsonObject.getString("InsertDate"));
            if (!jsonObject.isNull("StartDate"))
                remoteItem.setBindingDate(jsonObject.getString("StartDate"));
            if (!jsonObject.isNull("IsActivated"))
                remoteItem.setIsActivated(jsonObject.getBoolean("IsActivated"));
            if (!jsonObject.isNull("Name"))
                remoteItem.setName(jsonObject.getString("Name"));
            if (!jsonObject.isNull("VehicleVIN"))
                remoteItem.setDeviceAnnouncer(jsonObject.getString("VehicleVIN"));
            if (!jsonObject.isNull("CustomerName"))
                remoteItem.setCustomerName(jsonObject.getString("CustomerName"));
            if (!jsonObject.isNull("Status"))
                remoteItem.setStatus(jsonObject.getString("Status"));
            result.add(remoteItem);
        }
        return result;
    }
    private static ArrayList parseCRDS(JSONArray crds) throws JSONException {
        ArrayList result;
        result = new ArrayList<CRDSCustom>();
        // Get the JSON array of  results
        for (int i = 0; i < crds.length(); i++) {
            JSONObject jsonObject = crds.getJSONObject(i);
            CRDSCustom crdsItem = new CRDSCustom();
            if(!jsonObject.isNull("ActivationState"))
                crdsItem.setActivationState(jsonObject.getString("ActivationState"));
            if(!jsonObject.isNull("AppName"))
                crdsItem.setAppName(jsonObject.getString("AppName"));
            if(!jsonObject.isNull("AppType"))
                crdsItem.setAppType(jsonObject.getString("AppType"));
            if(!jsonObject.isNull("CAP"))
                crdsItem.setCAP(jsonObject.getString("CAP"));
            if(!jsonObject.isNull("Cellulare"))
                crdsItem.setCellulare(jsonObject.getString("Cellulare"));
            if(!jsonObject.isNull("Citta"))
                crdsItem.setCitta(jsonObject.getString("Citta"));
            if(!jsonObject.isNull("Culture"))
                crdsItem.setCulture(jsonObject.getString("Culture"));
            if(!jsonObject.isNull("DataRicezione"))
                crdsItem.setDataRicezione(jsonObject.getString("DataRicezione"));
            if(!jsonObject.isNull("DiagnosticAction"))
                crdsItem.setDiagnosticAction(jsonObject.getString("DiagnosticAction"));
            if(!jsonObject.isNull("Email"))
                crdsItem.setEmail(jsonObject.getString("Email"));
            if(!jsonObject.isNull("Fax"))
                crdsItem.setFax(jsonObject.getString("Fax"));
            if(!jsonObject.isNull("GUID"))
                crdsItem.setCRDSId(jsonObject.getString("GUID"));
            if(!jsonObject.isNull("Indirizzo"))
                crdsItem.setIndirizzo(jsonObject.getString("Indirizzo"));
            if(!jsonObject.isNull("LastLifeSignal"))
                crdsItem.setLastLifeSignal(jsonObject.getString("LastLifeSignal"));
            if(!jsonObject.isNull("ModoRicezioneCodice"))
                crdsItem.setModoRicezioneCodice(jsonObject.getString("ModoRicezioneCodice"));
            if(!jsonObject.isNull("Nazione"))
                crdsItem.setNazione(jsonObject.getString("Nazione"));
            if(!jsonObject.isNull("v"))
                crdsItem.setPartitaIva(jsonObject.getString("PartitaIva"));
            if(!jsonObject.isNull("RagioneSociale"))
                crdsItem.setRagioneSociale(jsonObject.getString("RagioneSociale"));
            if(!jsonObject.isNull("Responsabile"))
                crdsItem.setResponsabile(jsonObject.getString("Responsabile"));
            if(!jsonObject.isNull("Telefono"))
                crdsItem.setTelefono(jsonObject.getString("Telefono"));
            if(!jsonObject.isNull("Versione"))
                crdsItem.setVersione(jsonObject.getString("Versione"));
            if(!jsonObject.isNull("XML"))
                crdsItem.setXML(jsonObject.getString("XML"));
            result.add(crdsItem);
        }
        return result;
    }


    class DownloadDataTask extends AsyncTask<DownloadRequestSchema,Void,ResultOperation>
    {
        DownloadRequestSchema mRequestInfo;
        @Override
        protected ResultOperation doInBackground(DownloadRequestSchema... params) {
            boolean mHasCacheData = false;
            ResultOperation result = ResultOperation.newInstance(false,"",null);
            mRequestInfo = params[0];
            boolean mObtainCacheIfExist = mRequestInfo.getCacheOption();
            switch (mRequestInfo.getDownloadRequestType())
            {
                case VEHICLE_NOT_TRUSTED:
                    if(mObtainCacheIfExist && (mHasCacheData = CacheDataManager.getInstance().hasVehiclesNotTrusted())){
                        result.setClassReturn(CacheDataManager.getInstance().getVehicleNotTrusted());
                    }
                    break;
                case CRDS_NOT_TRUSTED:
                    if(mObtainCacheIfExist && (mHasCacheData = CacheDataManager.getInstance().hasCRDSNotTrusted())){
                        result.setClassReturn(CacheDataManager.getInstance().getCRDSNotTrusted());
                    }
                    break;
                case DRIVERS_OWNED:
                    if(mObtainCacheIfExist && (mHasCacheData = CacheDataManager.getInstance().hasCustomerDrivers(mRequestInfo.getUniqueCustomerCode()))){
                        result.setClassReturn(CacheDataManager.getInstance().getCustomerDrivers(mRequestInfo.getUniqueCustomerCode()));
                    }
                    break;
                case CRDS_OWNED:
                    if(mObtainCacheIfExist && (mHasCacheData = CacheDataManager.getInstance().hasCustomerCRDS(mRequestInfo.getUniqueCustomerCode()))){
                        result.setClassReturn(CacheDataManager.getInstance().getCustomerCRDS(mRequestInfo.getUniqueCustomerCode()));
                    }
                    break;
                case DRIVERS_NOT_TRUSTED:
                    if(mObtainCacheIfExist && (mHasCacheData = CacheDataManager.getInstance().hasDriversNotTrusted())){
                        result.setClassReturn(CacheDataManager.getInstance().getDriversNotTrusted());
                    }
                    break;
                case CUSTOMERS_LIST:
                    if(mObtainCacheIfExist && (mHasCacheData = CacheDataManager.getInstance().hasCustomers())){
                        result.setClassReturn(CacheDataManager.getInstance().getCustomers(null));
                    }
                    break;
                case VEHICLES_OWNED:
                    if(mObtainCacheIfExist && (mHasCacheData = CacheDataManager.getInstance().hasCustomerVehicles(mRequestInfo.getUniqueCustomerCode()))){
                        result.setClassReturn(CacheDataManager.getInstance().getCustomerVehicles(mRequestInfo.getUniqueCustomerCode()));
                    }
                    break;
                case MAIN_MENU:
                    break;
                case VEHICLE_DIAGNOSTIC:
                    //result = DownloadUtility.FetchingDiagnosticDataResults(mClient, mRequestInfo.getVehicleVIN());
                    //result = ((List<VehicleCustom> )result).get(0).get_FileContent();
                    break;
            }
            if(result.getClassReturn() == null)
                result = DownloadUtility.FetchingRemoteData(mClient, mRequestInfo);

            if((!mObtainCacheIfExist && (result.getClassReturn() != null))
                    || !mHasCacheData)
            {
                CacheDataManager.getInstance().UpdateCache(mRequestInfo, result.getClassReturn());
                Log.i(TAG,"Cache Updated: " + mRequestInfo.getDownloadRequestType() +
                        " (" + mRequestInfo.getUniqueCustomerCode() + ")");
            }
            return result;

        }
        @Override
        protected void onPostExecute(ResultOperation result)
        {
            notifyListeners(mRequestInfo,result.getClassReturn());
        }

    }
}
