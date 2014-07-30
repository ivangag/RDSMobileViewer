package com.viewer.rds.actia.rdsmobileviewer.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.viewer.rds.actia.rdsmobileviewer.CRDSCustom;
import com.viewer.rds.actia.rdsmobileviewer.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.DriverCardData;
import com.viewer.rds.actia.rdsmobileviewer.IRDSClientResponse;
import com.viewer.rds.actia.rdsmobileviewer.IRDSGetCall;
import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.IRDSClientRequest;
import com.viewer.rds.actia.rdsmobileviewer.ResultOperation;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;
import com.viewer.rds.actia.rdsmobileviewer.services.RDSViewerServiceAsync;
import com.viewer.rds.actia.rdsmobileviewer.services.RDSViewerServiceSync;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Created by igaglioti on 09/07/2014.
 */
public class DownloadUtility {

    public final static int DOWNLOAD_DATA_REQUEST = 0;

    public final static int DOWNLOAD_RESULT_OK = 0;
    public final static int DOWNLOAD_RESULT_FAILED = -1;

    public static final String DOWNLOAD_DATA_RESULT = "DOWNLOAD_DATA_RESULT";

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
        if((listener != null)
            &&(!mListeners.contains(listener))) {

            mListeners.add(listener);
        }
    }

    public void removeAllListeners()
    {
        mListeners.clear();
    }
    public void removeListener(IRemoteDownloadDataListener listener) {
        mListeners.remove(listener);
    }

    public void notifyListeners(final DownloadRequestSchema requestType,final ResultOperation result) {
        for (final IRemoteDownloadDataListener listener : mListeners) {
            if(listener instanceof Activity)
                ((Activity)listener).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onDownloadDataFinished(requestType,result);
                    }
                });

        }
    }

    public interface IRemoteDownloadDataListener {
        public void onDownloadDataFinished(DownloadRequestSchema requestType, ResultOperation result);
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
                                             DownloadRequestSchema downloadRequestSchema, boolean translateRemoteData) {
        // Object that encapsulates the HTTP GET request to the
        // RDS Web service.
        final DownloadRequestType requestType = downloadRequestSchema.getDownloadRequestType();
        final String url = requestType.getWSCallName(downloadRequestSchema);
        HttpGet request = new HttpGet(url);


        ResultOperation resOp = ResultOperation.newInstance();
        JSONResponseHandler responseHandler
                = new JSONResponseHandler(downloadRequestSchema.getDownloadRequestType(),translateRemoteData);

        try {
            // Get expanded acronyms from the Web service in JSON
            // format, parse data into a List of AcronymData, and
            // return the results.
            resOp = client.execute(request,
                    responseHandler);
        } catch (ClientProtocolException e) {
            Log.w(TAG, "ClientProtocolException: " + e.getLocalizedMessage());
        } catch (IOException e) {
            Log.w(TAG, "IOException: " + e.getLocalizedMessage());
        }
        return resOp;
    }



    /**
     * Object that can invoke HTTP GET requests on URLs.
     */
    private final static AndroidHttpClient mClient =
            AndroidHttpClient.newInstance("");

    static class JSONResponseHandler implements ResponseHandler<ResultOperation>
    {
        final private boolean mTranslateRemoteData;
        final private DownloadRequestType mRequestType;

        public JSONResponseHandler(DownloadRequestType requestType, boolean translateRemoteData) {
            mRequestType = requestType;
            mTranslateRemoteData = translateRemoteData;
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
            JSONArray jsonArray = null;
            try {

                // Takes a JSON source string and extracts characters
                // and tokens from it.
                JSONTokener token = new JSONTokener(JSONResponse);

                if(token.nextValue() instanceof JSONArray)
                {
                    jsonArray =  (JSONArray) new JSONTokener(JSONResponse).nextValue();
                    resultOperation.setStatus(true);
                }
                else {
                    resultOperation = DownloadUtility.buildResultOperation(JSONResponse, resultOperation);
                    //jsonArray = (JSONArray) resultOperation.getClassReturn();
                }

                if(mTranslateRemoteData){
                    if(resultOperation.isStatus()) {
                        jsonArray = new JSONArray(new String((byte[]) resultOperation.getClassReturn()));
                        final DownloadRequestType requestType = mRequestType;
                        result = parseJsonToRDSRemoteObject(jsonArray, requestType);
                        resultOperation.setClassReturn(result);
                    }
                }
            } catch (JSONException e) {
                resultOperation.setStatus(false);
                resultOperation.setOtherInfo(String.format("Exception on getResponse:%s",e.getLocalizedMessage()));
                e.printStackTrace();
            }

            return resultOperation;
        }
    }

    public static ArrayList parseJsonToRDSRemoteObject(JSONArray jsonArray, DownloadRequestType requestType) throws JSONException {
        ArrayList result = null;
        switch (requestType) {

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
        return result;
    }

    private static ResultOperation buildResultOperation(String JSONResponse,
                                                        ResultOperation resultOperationInstance) throws JSONException, IOException {
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject(JSONResponse);
        byte[] streamDecompress = null;
        if (!jsonObject.isNull(classReturn)) {
            // get ClassReturn field
            jsonArray = jsonObject.getJSONArray(classReturn);
            if (Utils.GZIP_MAGICAL == jsonArray.getInt(0)) {
                final byte[] streamCompress = DownloadUtility.getBytesFromJsonArray(jsonArray);
                streamDecompress = DownloadUtility.decompressFromGZip(new ByteArrayInputStream(Utils.convertToByteArray((streamCompress))));

                jsonArray = new JSONArray(new String(streamDecompress));
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
        //resultOperationInstance.setClassReturn(jsonArray);
        resultOperationInstance.setClassReturn(streamDecompress);
        return resultOperationInstance;
    }


    private static ResultOperation buildResultOperationEx(String JSONResponse,
                                                        ResultOperation resultOperationInstance) throws JSONException, IOException {
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject(JSONResponse);
        if (!jsonObject.isNull(classReturn)) {
            // get ClassReturn field
            jsonArray = jsonObject.getJSONArray(classReturn);
            if (Utils.GZIP_MAGICAL == jsonArray.getInt(0)) {
                final byte[] streamCompress = DownloadUtility.getBytesFromJsonArray(jsonArray);
                byte[] streamDecompress = DownloadUtility.decompressFromGZip(new ByteArrayInputStream(Utils.convertToByteArray((streamCompress))));

                jsonArray = new JSONArray(new String(streamDecompress));
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
        return resultOperationInstance;
    }

    public static byte[] getBytesFromJsonArray(JSONArray jsonArray) throws JSONException {

        final byte[] streamCompress = new byte[jsonArray.length()];
        for (int Idx = 0; Idx < jsonArray.length(); Idx++) {
            streamCompress[Idx] = ((byte) jsonArray.getInt(Idx));
        }
        return streamCompress;
    }

    public byte[] makeBytesFromArray(ArrayList<Byte> arrayList)
    {
        ArrayList<Byte> in = arrayList;
        int n = in.size();
        byte[] out = new byte[n];
        for (int i = 0; i < n; i++) {
            out[i] = in.get(i);
        }
        return out;
    }



    public static byte[] decompressFromGZip(ByteArrayInputStream bytesIn) throws IOException {
        GZIPInputStream in = new GZIPInputStream(bytesIn);
        ByteArrayOutputStream contents = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                contents.write(buf, 0, len);
            }
        } finally {
            in.close();
        }
        return contents.toByteArray();
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
            if (!jsonObject.isNull("IsEmailForwardServiceActive"))
                remoteItem.setIsEmailForwardServiceActive(jsonObject.getBoolean("IsEmailForwardServiceActive"));
            result.add(remoteItem);
        }
        return result;
    }
    private static ArrayList parseVehicles(JSONArray vehicles) throws JSONException {
        ArrayList result;
        result = new ArrayList<VehicleCustom>();
        // Get the JSON array of  results
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
        // Get the JSON array of  results
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
            if(!jsonObject.isNull("PartitaIva"))
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

    public void RequireDownloadAsyncTask(IRemoteDownloadDataListener clientDownloadDataListener,
                                         DownloadRequestSchema downloadRequestInfo) {

        this.addListener(clientDownloadDataListener);
        if(downloadRequestInfo.getDownloadRequestType().equals(DownloadRequestType.VEHICLE_NOT_TRUSTED))
        {
            try {
                mRDSClientRequest.fetchRemoteData(mRDSServiceResponse,downloadRequestInfo);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG,"RemoteException => mRDSClientRequest.fetchRemoteData: " + e.getLocalizedMessage());
            }
        } else {
            DownloadDataTask downloadDataTask = new DownloadDataTask();
            downloadDataTask.execute(downloadRequestInfo);
        }
    }

    class DownloadDataTask extends AsyncTask<DownloadRequestSchema,Void,ResultOperation>
    {
        DownloadRequestSchema mRequestInfo;
        @Override
        protected ResultOperation doInBackground(DownloadRequestSchema... params) {

            mRequestInfo = params[0];


            ResultOperation result = ResultOperation.newInstance(false,"",null);

            boolean mHasCacheData = false;
            final boolean mObtainCacheIfExist = mRequestInfo.getCacheOption();
            final DownloadRequestType requestType = mRequestInfo.getDownloadRequestType();

            if(mObtainCacheIfExist){
                mHasCacheData = CacheDataManager.checkedCachedDataPresence(result, mRequestInfo);
            }
            if(!mHasCacheData) {
                result = DownloadUtility.FetchingRemoteData(mClient, mRequestInfo, true);

                /* !! The following (commented) code snippet is only for testing service purpose !!
                if(!requestType.equals(DownloadRequestType.VEHICLE_NOT_TRUSTED)){
                    result = DownloadUtility.FetchingRemoteData(mClient, mRequestInfo,true);
                }
                else{
                    try {
                        List<VehicleCustom> vehicleCustomList = (List<VehicleCustom>) mRDSClientCall.getVehiclesNotActivated();
                        result.setClassReturn(vehicleCustomList);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Log.e(TAG,String.format("RDSServiceCall::ERROR:%s",e.getLocalizedMessage()));
                        result.setStatus(false);
                        result.setOtherInfo(e.getLocalizedMessage());
                    }
                }
                */

            }

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
            notifyListeners(mRequestInfo,result);
        }
    }

    public void startRDService(Context context){
        // Launch the designated Bound Service if they aren't already
        // running via a call to bindService() Bind this activity to
        // the GeoNamesService* Services if they aren't already bound.
        //Debug.waitForDebugger();
        if (mRDSClientCall == null)
            context.bindService(RDSViewerServiceSync.makeIntent(context),
                    mRDSServiceSync,
                    context.BIND_AUTO_CREATE);

        if (mRDSClientRequest == null)
            context.bindService(RDSViewerServiceAsync.makeIntent(context),
                    mRDSServiceAsync,
                    context.BIND_AUTO_CREATE);


    }

    public void stopRDSService(Context context){

        //Debug.waitForDebugger();
        // Unbind the Sync/Async Services if they are connected.
        if (mRDSClientCall != null)
            context.unbindService(mRDSServiceSync);

        //Debug.waitForDebugger();
        // Unbind the Sync/Async Services if they are connected.
        if (mRDSClientRequest != null)
            context.unbindService(mRDSServiceAsync);


    }

    /**
     * The AIDL Interface that's used to make twoway calls to the
     * GeoNamesServiceSync Service.  This object plays the role of
     * Requestor in the Broker Pattern.  If it's null then there's no
     * connection to the Service.
     */
    IRDSGetCall mRDSClientCall = null;
    IRDSClientRequest mRDSClientRequest = null;

    ServiceConnection mRDSServiceSync = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.d(TAG, "ComponentName: " + name);
            mRDSClientCall = IRDSGetCall.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRDSClientCall = null;
        }
    };

    ServiceConnection mRDSServiceAsync = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.d(TAG, "ComponentName: " + name);
            mRDSClientRequest = IRDSClientRequest.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRDSClientRequest = null;
        }
    };

    IRDSClientResponse.Stub mRDSServiceResponse = new IRDSClientResponse.Stub() {

        @Override
        public void sendRemoteDataResult(ResultOperation result, DownloadRequestSchema downloadRequest) throws RemoteException {

            if(result.isStatus())
            {
                ArrayList data = null;
                try {
                    final JSONArray jsonArray  = new JSONArray(new String((byte[]) result.getClassReturn()));
                    final DownloadRequestType requestType = downloadRequest.getDownloadRequestType();
                    data = parseJsonToRDSRemoteObject(jsonArray, requestType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result.setClassReturn(data);
                notifyListeners(downloadRequest,result);
            }
        }
    };
}
