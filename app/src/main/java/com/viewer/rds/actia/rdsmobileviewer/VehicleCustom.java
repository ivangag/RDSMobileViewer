package com.viewer.rds.actia.rdsmobileviewer;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.viewer.rds.actia.rdsmobileviewer.db.RDSDBHelper;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

/**
 * Created by igaglioti on 08/07/2014.
 */
public class VehicleCustom implements Parcelable {

    private String _CustomerName;

    private String _DiagnosticDeviceTime;

    private String _FileContent;

    private String _IMEI;

    private String _IdDevice;

    private String _JourneyEnableDate;

    private String _PhoneNumber;

    private String _StartDate;

    private String _Status;

    private String _VIN;

    private String _VRN;
    private String mSwVersion;
    private String _SwName;

    public VehicleCustom(String VIN) {
        _VIN = VIN;
    }

    public VehicleCustom() {
    }
    public VehicleCustom(Parcel in) {
        _CustomerName = in.readString();
        _DiagnosticDeviceTime = in.readString();
        _FileContent = in.readString();
        _IMEI = in.readString();
        _IdDevice = in.readString();
        _JourneyEnableDate = in.readString();
        _PhoneNumber = in.readString();
        _StartDate = in.readString();
        _Status = in.readString();
        _VIN = in.readString();
        _VRN = in.readString();
    }

    public String getCustomerName() {
        return _CustomerName;
    }

    public void setCustomerName(String customerName) {
        this._CustomerName = customerName;
    }

    public String getDiagnosticDeviceTime() {
        return _DiagnosticDeviceTime;
    }


    public void setDiagnosticDeviceTime(String diagnosticDeviceTime) {
        this._DiagnosticDeviceTime = Utils.getDateTimeFromTicks(diagnosticDeviceTime);
    }



    public String getFileContent() {
        return _FileContent;
    }

    public void setFileContent(String fileContent) {
        this._FileContent = fileContent;
    }

    public String getIMEI() {
        return _IMEI;
    }

    public void setIMEI(String imei) {
        this._IMEI = imei;
    }

    public String getIdDevice() {
        return _IdDevice;
    }

    public void setIdDevice(String idDevice) {
        this._IdDevice = idDevice;
    }

    public String getJourneyEnableDate() {
        return _JourneyEnableDate;
    }

    public void setJourneyEnableDate(String journeyEnableDate) {
        this._JourneyEnableDate = journeyEnableDate;
    }

    public String getPhoneNumber() {
        return _PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this._PhoneNumber = phoneNumber;
    }

    public String getStartDate() {
        return _StartDate;
    }

    public void setStartDate(String startDate) {
        this._StartDate = Utils.getDateTimeFromTicks(startDate);
    }

    public String getStatus() {
        return _Status;
    }

    public void setStatus(String status) {
        this._Status = status;
    }

    public String getVIN() {
        return _VIN;
    }

    public void setVIN(String vin) {
        this._VIN = vin;
    }

    public String getVRN() {
        return _VRN;
    }

    public void setVRN(String vrn) {
        this._VRN = vrn;
    }


    public void setSwVersion(String swVersion) {
        this.mSwVersion = swVersion;
    }

    public String getSwVersion() {
        return mSwVersion;
    }

    public void setSwName(String swName) {
        this._SwName = swName;
    }

    public String getSwName() {
        return _SwName;
    }



    public static final Parcelable.Creator<VehicleCustom> CREATOR
            = new Parcelable.Creator<VehicleCustom>() {
        public VehicleCustom createFromParcel(Parcel in) {
            return new VehicleCustom(in);
        }

        public VehicleCustom[] newArray(int size) {
            return new VehicleCustom[size];
        }
    };



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_CustomerName);
        dest.writeString(_DiagnosticDeviceTime);
        dest.writeString(_FileContent);
        dest.writeString(_IMEI);
        dest.writeString(_IdDevice);
        dest.writeString(_JourneyEnableDate);
        dest.writeString(_PhoneNumber);
        dest.writeString(_StartDate);
        dest.writeString(_Status);
        dest.writeString(_VIN);
        dest.writeString(_VRN);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public String toString()
    {
        String res = String.format("VIN:%s\t\nVRN:%s\t\nIMEI:%s\t\nPhoneNumber:%s\t\nSwName:%s\t\nSwVersion:%s\t\nDiagnosticDeviceTime:%s",
                _VIN,_VRN,_IMEI,_PhoneNumber,_SwName, mSwVersion,_DiagnosticDeviceTime);
        return res;
    }

    public static VehicleCustom getDataFromContentValues(ContentValues values) {
        VehicleCustom customerData = new VehicleCustom();

        customerData.setVRN(values.getAsString(RDSDBHelper.VRN));
        customerData.setVIN(values.getAsString(RDSDBHelper.VIN));
        customerData.setCustomerName(values.getAsString(RDSDBHelper.CUSTOMER_NAME));
        customerData.setStartDate(RDSDBHelper.START_DATE);
        customerData.setDiagnosticDeviceTime(values.getAsString(RDSDBHelper.DIAG_TIME));
        customerData.setFileContent(RDSDBHelper.FILE_CONTENT);
        customerData.setIdDevice(RDSDBHelper.ID_DEVICE);
        customerData.setIMEI(RDSDBHelper.IMEI);
        customerData.setStatus(RDSDBHelper.STATUS);
        customerData.setPhoneNumber(RDSDBHelper.PHONE_NUMBER);
        customerData.setSwName(RDSDBHelper.SW_NAME);
        customerData.setSwVersion(RDSDBHelper.SW_VERSION);
        customerData.setJourneyEnableDate(values.getAsString(RDSDBHelper.JOURNEY_ENBL_DATE));

        return customerData;
    }

    public static ContentValues getCVFromData(VehicleCustom vehicle) {
        ContentValues cv = new ContentValues();
        cv.put(RDSDBHelper.VRN, vehicle.getVRN());
        cv.put(RDSDBHelper.VIN, vehicle.getVIN());
        cv.put(RDSDBHelper.CUSTOMER_NAME,vehicle.getCustomerName());
        cv.put(RDSDBHelper.DIAG_TIME, vehicle.getDiagnosticDeviceTime());
        cv.put(RDSDBHelper.FILE_CONTENT,vehicle.getFileContent());
        cv.put(RDSDBHelper.IMEI, vehicle.getIMEI());
        cv.put(RDSDBHelper.PHONE_NUMBER,vehicle.getPhoneNumber());
        cv.put(RDSDBHelper.STATUS, vehicle.getStatus());
        cv.put(RDSDBHelper.SW_NAME,vehicle.getSwName());
        cv.put(RDSDBHelper.SW_VERSION,vehicle.getSwVersion());
        cv.put(RDSDBHelper.ID_DEVICE,vehicle.getIdDevice());
        cv.put(RDSDBHelper.START_DATE,vehicle.getStartDate());
        cv.put(RDSDBHelper.JOURNEY_ENBL_DATE,vehicle.getJourneyEnableDate());
        return cv;
    }
}
