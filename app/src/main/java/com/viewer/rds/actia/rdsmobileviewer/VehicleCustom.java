package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

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

    public String get_CustomerName() {
        return _CustomerName;
    }

    public void set_CustomerName(String customerName) {
        this._CustomerName = customerName;
    }

    public String get_DiagnosticDeviceTime() {
        return _DiagnosticDeviceTime;
    }


    public void set_DiagnosticDeviceTime(String diagnosticDeviceTime) {
        this._DiagnosticDeviceTime = Utils.getDateTimeFromTicks(diagnosticDeviceTime);
    }



    public String get_FileContent() {
        return _FileContent;
    }

    public void set_FileContent(String fileContent) {
        this._FileContent = fileContent;
    }

    public String get_IMEI() {
        return _IMEI;
    }

    public void set_IMEI(String imei) {
        this._IMEI = imei;
    }

    public String get_IdDevice() {
        return _IdDevice;
    }

    public void set_IdDevice(String idDevice) {
        this._IdDevice = idDevice;
    }

    public String get_JourneyEnableDate() {
        return _JourneyEnableDate;
    }

    public void set_JourneyEnableDate(String journeyEnableDate) {
        this._JourneyEnableDate = journeyEnableDate;
    }

    public String get_PhoneNumber() {
        return _PhoneNumber;
    }

    public void set_PhoneNumber(String phoneNumber) {
        this._PhoneNumber = phoneNumber;
    }

    public String get_StartDate() {
        return _StartDate;
    }

    public void set_StartDate(String startDate) {
        this._StartDate = Utils.getDateTimeFromTicks(startDate);
    }

    public String get_Status() {
        return _Status;
    }

    public void set_Status(String status) {
        this._Status = status;
    }

    public String get_VIN() {
        return _VIN;
    }

    public void set_VIN(String vin) {
        this._VIN = vin;
    }

    public String get_VRN() {
        return _VRN;
    }

    public void set_VRN(String vrn) {
        this._VRN = vrn;
    }


    public void setSwVersion(String swVersion) {
        this.mSwVersion = swVersion;
    }

    public String getSwVersion() {
        return mSwVersion;
    }

    public void set_SwName(String swName) {
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
}
