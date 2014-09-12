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

import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.viewer.rds.actia.rdsmobileviewer.volley.GsonRequest;
import com.viewer.rds.actia.rdsmobileviewer.volley.VolleyRequestManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by igaglioti on 09/07/2014.
 */
public class DownloadRDSManager {
    private AtomicInteger mRequestCounter = new AtomicInteger();

    public final static int DOWNLOAD_DATA_REQUEST = 0;

    public final static int DOWNLOAD_RESULT_OK = 0;
    public final static int DOWNLOAD_RESULT_FAILED = -1;

    public static final String DOWNLOAD_DATA_RESULT = "DOWNLOAD_DATA_RESULT";

    private final static String TAG = DownloadRDSManager.class
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


    private static DownloadRDSManager ourInstance = new DownloadRDSManager();

    public static DownloadRDSManager getInstance() {
        return ourInstance;
    }
    private DownloadRDSManager() {
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
        mRequestCounter.decrementAndGet();
        for (final IRemoteDownloadDataListener listener : mListeners) {
            if(listener instanceof Activity)
                ((Activity)listener).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onDownloadDataFinished(requestType,result);
                    }
                });
            else {
                listener.onDownloadDataFinished(requestType,result);
            }

        }
    }

    public int getRequestCounter() {
        return mRequestCounter.get();
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

        public Class<?> getRemoteDataType() {
            switch (this)
            {
                case CRDS_OWNED:
                case CRDS_NOT_TRUSTED:
                    return CRDSCustom.class;
                case DRIVERS_NOT_TRUSTED:
                case DRIVERS_OWNED:
                    return DriverCardData.class;
                case CUSTOMERS_LIST:
                    return MainContractorData.class;
                case VEHICLE_NOT_TRUSTED:
                case VEHICLES_OWNED:
                case VEHICLE_DIAGNOSTIC:
                    return VehicleCustom.class;
            }
            return null;
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
            ArrayList result;
            JSONArray jsonArray;
            try {

                // Takes a JSON source string and extracts characters
                // and tokens from it.

                resultOperation = DownloadRDSManager.buildResultOperation(JSONResponse, resultOperation);

                if(mTranslateRemoteData){
                    if(resultOperation.isStatus()) {
                        result = ParserFactory.parseJsonToRDSRemoteEntity(new String((byte[]) resultOperation.getClassReturn()), mRequestType);
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


    private static ResultOperation buildResultOperation(String JSONResponse,
                                                        ResultOperation resultOperationInstance) throws JSONException, IOException {

        JSONObject jsonObject = new JSONObject(JSONResponse);
        byte[] streamDecompress = null;
        if (!jsonObject.isNull(classReturn)) {
            // get ClassReturn field
            final JSONArray jsonArray = jsonObject.getJSONArray(classReturn);
            if (Utils.GZIP_MAGICAL_1 == jsonArray.getInt(0)) {
                final byte[] streamCompress = ParserFactory.getBytesFromJsonArray(jsonArray);
                streamDecompress = ParserFactory.decompressFromGZip(new ByteArrayInputStream(streamCompress));

                //jsonArray = new JSONArray(new String(streamDecompress));
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
        resultOperationInstance.setClassReturn(streamDecompress);
        return resultOperationInstance;
    }



    public void RequireDownloadAsyncTask(IRemoteDownloadDataListener clientDownloadDataListener,
                                         DownloadRequestSchema downloadRequestInfo) {

        this.addListener(clientDownloadDataListener);
        try {
            mRDSClientRequest.fetchRemoteData(mRDSServiceResponse,downloadRequestInfo);
        } catch (RemoteException e) {
            Log.e(TAG,"RemoteException => mRDSClientRequest.fetchRemoteData: " + e.getLocalizedMessage());
        }
    }

    public void RequireDownloadVolleyTask(final Context context, IRemoteDownloadDataListener clientDownloadDataListener,
                                         final DownloadRequestSchema downloadRequestInfo) {

        this.addListener(clientDownloadDataListener);

        final String url = downloadRequestInfo.getDownloadRequestType().getWSCallName(downloadRequestInfo);

        GsonRequest<ResultOperation> gsonRequest = new GsonRequest<ResultOperation>(
                url,ResultOperation.class,null,new Response.Listener<ResultOperation>() {
            @Override
            public void onResponse(ResultOperation response) {
                boolean status = response.isStatus();
                try {
                   final ArrayList<Double> arrayList = (ArrayList<Double>) response.getClassReturn();
                    if (Utils.GZIP_MAGICAL_1 == arrayList.get(0)) {
                        final byte[] streamCompress = ParserFactory.getBytesFromJsonArray(arrayList);
                        final byte[] streamDecompress = ParserFactory.decompressFromGZip(new ByteArrayInputStream(streamCompress));
                        final String jsonRaw = new String(streamDecompress);
                        ArrayList data = ParserFactory.parseJsonToRDSRemoteEntity(jsonRaw, downloadRequestInfo.getDownloadRequestType());
                        response.setClassReturn(data);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    CacheDataManager.get().saveDownloadRepository(downloadRequestInfo,response);
                }
                notifyListeners(downloadRequestInfo,response);
            }
            },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMsg = error.getMessage();
                notifyListeners(downloadRequestInfo,ResultOperation.newInstance(false,errorMsg,null));
                Log.e(TAG,"DownloadError: " + errorMsg);
            }
        }
        );
        gsonRequest.setTag(downloadRequestInfo.getDownloadRequestType().toString());
        VolleyRequestManager.getInstance(context).addToRequestQueue(gsonRequest);
    }

    class DownloadDataTask extends AsyncTask<DownloadRequestSchema,Void,ResultOperation>
    {
        DownloadRequestSchema mRequestInfo;
        @Override
        protected ResultOperation doInBackground(DownloadRequestSchema... params) {

            final boolean isResultTranslated = false;
            mRequestInfo = params[0];
            ResultOperation result = ResultOperation.newInstance(false,"",null);
            boolean mHasCacheData = false;

            final boolean mObtainCacheIfExist = mRequestInfo.getCacheOption();

            if(mObtainCacheIfExist){
                mHasCacheData = CacheDataManager.existData(result, mRequestInfo);
            }
            if(!mHasCacheData) {
                result = DownloadRDSManager.FetchingRemoteData(mClient, mRequestInfo, isResultTranslated);
            }



            if(!isResultTranslated){
                final String jsonRaw = new String((byte[]) result.getClassReturn());
                try {
                    result.setClassReturn(ParserFactory.parseJsonToRDSRemoteEntity(jsonRaw, mRequestInfo.getDownloadRequestType()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CacheDataManager.get().saveDownloadRepository(mRequestInfo, jsonRaw);
            }


            if((!mObtainCacheIfExist && (result.getClassReturn() != null))
                    || !mHasCacheData)
            {
                CacheDataManager.get().UpdateCache(mRequestInfo, result.getClassReturn());
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
        Log.i(TAG,"Try to start RDSViewerServiceAsync Service...");
        context.startService(RDSViewerServiceAsync.makeIntent(context));

    }


    public void bindRDService(Context context){
        // Launch the designated Bound Service if they aren't already
        // running via a call to bindService() Bind this activity to
        // the GeoNamesService* Services if they aren't already bound.
        //Debug.waitForDebugger();
        if (mRDSClientCall == null)
            context.bindService(RDSViewerServiceSync.makeIntent(context),
                    mRDSServiceSync,
                    Context.BIND_AUTO_CREATE);

        if (mRDSClientRequest == null)
            context.bindService(RDSViewerServiceAsync.makeIntent(context),
                    mRDSServiceAsync,
                    Context.BIND_AUTO_CREATE);
    }

    public void unbindRDSService(Context context){

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

            if(result.isStatus()){
                ArrayList data = null;
                try {
                    String jsonRaw = "";
                    if(result.getClassReturnType().equals("bytes")) {
                        jsonRaw = new String((byte[]) result.getClassReturn());
                    }
                    else{
                        jsonRaw = CacheDataManager.get().getDownloadRepository((String)result.getClassReturn());
                    }
                    if(!jsonRaw.isEmpty()) {
                        data = ParserFactory.parseJsonToRDSRemoteEntity(jsonRaw, downloadRequest.getDownloadRequestType());
                        result.setClassReturn(data);
                        CacheDataManager.get().UpdateCache(downloadRequest, data);
                    }
                    else{
                        result.setStatus(false);
                    }
                } catch (JSONException e) {
                    result.setStatus(false);
                    e.printStackTrace();
                } catch (RDSEmptyDataException e) {
                    result.setStatus(false);
                    e.printStackTrace();
                }
            }
            notifyListeners(downloadRequest,result);
        }
    };
}
