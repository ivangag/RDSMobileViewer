package com.viewer.rds.actia.rdsmobileviewer.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viewer.rds.actia.rdsmobileviewer.CRDSCustom;
import com.viewer.rds.actia.rdsmobileviewer.DriverCardData;
import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * Created by igaglioti on 31/07/2014.
 */
public class ParserFactory {


    public static ArrayList parseCustomers(JSONArray customers) throws JSONException {
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
    public static ArrayList parseVehicles(JSONArray vehicles) throws JSONException {
        ArrayList result = new ArrayList<VehicleCustom>();
        // Get the JSON array of  results
        for (int i = 0; i < vehicles.length(); i++) {
            JSONObject jsonObject = vehicles.getJSONObject(i);
            VehicleCustom veh = new VehicleCustom();
            veh.setVIN(jsonObject.getString("VIN"));
            veh.setIMEI(jsonObject.getString("IMEI"));
            veh.setVRN(jsonObject.getString("VRN"));
            veh.setIdDevice(jsonObject.getString("IdDevice"));
            veh.setPhoneNumber(jsonObject.getString("PhoneNumber"));
            if (!jsonObject.isNull("FileContent"))
                veh.setFileContent(jsonObject.getString("FileContent"));
            if (!jsonObject.isNull("CustomerName"))
                veh.setCustomerName(jsonObject.getString("CustomerName"));
            if (!jsonObject.isNull("Status"))
                veh.setStatus(jsonObject.getString("Status"));
            if (!jsonObject.isNull("LastDiag"))
                veh.setDiagnosticDeviceTime(jsonObject.getString("LastDiag"));
            if (!jsonObject.isNull("DiagnosticDeviceTime"))
                veh.setDiagnosticDeviceTime(jsonObject.getString("DiagnosticDeviceTime"));
            if (!jsonObject.isNull("SwName"))
                veh.setSwName(jsonObject.getString("SwName"));
            if (!jsonObject.isNull("SwVersion"))
                veh.setSwVersion(jsonObject.getString("SwVersion"));
            if (!jsonObject.isNull("JourneyEnableDate"))
                veh.setJourneyEnableDate(jsonObject.getString("JourneyEnableDate"));
            if (!jsonObject.isNull("StartDate"))
                veh.setStartDate(jsonObject.getString("StartDate"));
            result.add(veh);
        }
        return result;
    }
    public static ArrayList parseDrivers(JSONArray drivers) throws JSONException {
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
                remoteItem.setStartDate(jsonObject.getString("StartDate"));
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
    public static ArrayList parseCRDS(JSONArray crds) throws JSONException {
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

    public static byte[] getBytesFromJsonArray(JSONArray jsonArray) throws JSONException {

        final byte[] streamCompress = new byte[jsonArray.length()];
        for (int Idx = 0; Idx < jsonArray.length(); Idx++) {
            streamCompress[Idx] = ((byte) jsonArray.getInt(Idx));
        }
        return streamCompress;
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


    final static Type typeOfVehicleArray = new TypeToken<ArrayList<VehicleCustom>>(){}.getType();
    public static ArrayList parseJsonToRDSRemoteEntity(String jsonRaw, DownloadRDSManager.DownloadRequestType requestType) throws JSONException {
        final JSONArray jsonArray = new JSONArray(jsonRaw);
        ArrayList result = null;
        switch (requestType) {

            case VEHICLE_DIAGNOSTIC:
            case VEHICLES_OWNED:
            case VEHICLE_NOT_TRUSTED:
                Gson gson = new Gson();
                //result = gson.fromJson(jsonRaw, typeOfVehicleArray);
                result = ParserFactory.parseVehicles(jsonArray);
                break;
            case CUSTOMERS_LIST:
                result = ParserFactory.parseCustomers(jsonArray);
                break;
            case CRDS_NOT_TRUSTED:
            case CRDS_OWNED:
                result = ParserFactory.parseCRDS(jsonArray);
                break;
            case DRIVERS_OWNED:
            case DRIVERS_NOT_TRUSTED:
                result = ParserFactory.parseDrivers(jsonArray);
                break;
            case MAIN_MENU:
                break;
        }
        return result;
    }

    public static byte[] getBytesFromJsonArray(ArrayList<Double> arrayList) {
        final byte[] streamCompress = new byte[arrayList.size()];
        for (int Idx = 0; Idx < arrayList.size(); Idx++) {
            streamCompress[Idx] = arrayList.get(Idx).byteValue();
        }
        return streamCompress;
    }
}
