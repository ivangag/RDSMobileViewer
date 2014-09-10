package com.viewer.rds.actia.rdsmobileviewer;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.viewer.rds.actia.rdsmobileviewer.db.RDSDBHelper;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

/**
 * Created by igaglioti on 08/07/2014.
 */
public class VehicleCustom extends Model implements Parcelable {

    @Column
    private transient String CustomerUniqueId;

    @Column
    private String CustomerName;

    @Column
    private String DiagnosticDeviceTime;

    @Column
    private String FileContent;

    @Column
    private String IMEI;

    @Column
    private String IdDevice;

    @Column
    private String JourneyEnableDate;

    @Column
    private String PhoneNumber;

    @Column
    private String StartDate;

    @Column
    private String Status;

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String VIN;

    @Column
    private String VRN;

    @Column
    private String SwVersion;

    @Column
    private String SwName;

    public VehicleCustom(String VIN) {
        this.VIN = VIN;
    }

    public VehicleCustom() {
        super();
    }
    public VehicleCustom(Parcel in) {
        CustomerName = in.readString();
        DiagnosticDeviceTime = in.readString();
        FileContent = in.readString();
        IMEI = in.readString();
        IdDevice = in.readString();
        JourneyEnableDate = in.readString();
        PhoneNumber = in.readString();
        StartDate = in.readString();
        Status = in.readString();
        VIN = in.readString();
        VRN = in.readString();
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        this.CustomerName = customerName;
    }

    public String getDiagnosticDeviceTime() {
        return DiagnosticDeviceTime;
    }


    public void setDiagnosticDeviceTime(String diagnosticDeviceTime) {
        this.DiagnosticDeviceTime = Utils.getDateTimeFromTicks(diagnosticDeviceTime);
    }



    public String getFileContent() {
        return FileContent;
    }

    public void setFileContent(String fileContent) {
        this.FileContent = fileContent;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String imei) {
        this.IMEI = imei;
    }

    public String getIdDevice() {
        return IdDevice;
    }

    public void setIdDevice(String idDevice) {
        this.IdDevice = idDevice;
    }

    public String getJourneyEnableDate() {
        return JourneyEnableDate;
    }

    public void setJourneyEnableDate(String journeyEnableDate) {
        this.JourneyEnableDate = journeyEnableDate;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.PhoneNumber = phoneNumber;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        this.StartDate = Utils.getDateTimeFromTicks(startDate);
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String vin) {
        this.VIN = vin;
    }

    public String getVRN() {
        return VRN;
    }

    public void setVRN(String vrn) {
        this.VRN = vrn;
    }


    public void setSwVersion(String swVersion) {
        this.SwVersion = swVersion;
    }

    public String getSwVersion() {
        return SwVersion;
    }

    public void setSwName(String swName) {
        this.SwName = swName;
    }

    public String getSwName() {
        return SwName;
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
        dest.writeString(CustomerName);
        dest.writeString(DiagnosticDeviceTime);
        dest.writeString(FileContent);
        dest.writeString(IMEI);
        dest.writeString(IdDevice);
        dest.writeString(JourneyEnableDate);
        dest.writeString(PhoneNumber);
        dest.writeString(StartDate);
        dest.writeString(Status);
        dest.writeString(VIN);
        dest.writeString(VRN);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        return  sb.append("VIN:").append(VIN).append("\t\n")
        .append("Plate:").append(VRN).append("\t\n")
        .append("IMEI:").append(IMEI).append("\t\n")
        .append("Phone:").append(PhoneNumber).append("\t\n")
        .append("Sw:").append(SwName).append("\t\n")
        .append("SwVersion:").append(SwVersion).append("\t\n")
        .append("DiagTime:").append(DiagnosticDeviceTime).toString();
        //String res = String.format("VIN:%s\t\nVRN:%s\t\nIMEI:%s\t\nPhoneNumber:%s\t\nSwName:%s\t\nSwVersion:%s\t\nDiagnosticDeviceTime:%s",
         //       VIN,VRN,IMEI,PhoneNumber,SwName, SwVersion,DiagnosticDeviceTime);
        //return res;
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

    public String getCustomerUniqueId() {
        return CustomerUniqueId;
    }

    public void setCustomerUniqueId(String customerUniqueId) {
        CustomerUniqueId = customerUniqueId;
    }
}
