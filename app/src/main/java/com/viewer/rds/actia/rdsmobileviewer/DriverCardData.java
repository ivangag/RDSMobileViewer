package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

/**
 * Created by igaglioti on 11/07/2014.
 */
@Table(name = "Drivers")
public class DriverCardData extends Model implements Parcelable {

    @Column
    private transient String CustomerUniqueId;

    @Column
    private String Status;

    @Column
    private String CustomerName;

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String IdCard;

    @Column
    private String Name;

    @Column
    private String InsertingDate;

    @Column
    private String StartDate;

    @Column
    private String VehicleVIN;

    @Column
    private boolean IsActivated;

    public DriverCardData(Parcel in) {

    }
    public DriverCardData(){
        super();
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String mCustomerName) {
        this.CustomerName = mCustomerName;
    }

    public String getIdCard() {
        return IdCard;
    }

    public void setIdCard(String mIdCard) {
        this.IdCard = mIdCard;
    }

    public String getName() {
        return Name;
    }

    public void setName(String mName) {
        this.Name = mName;
    }

    public String getInsertingDate() {
        return InsertingDate;
    }

    public void setInsertingDate(String mInsertingDate) {
        this.InsertingDate = Utils.getDateTimeFromTicks(mInsertingDate);
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String mBindingDate) {
        this.StartDate = Utils.getDateTimeFromTicks(mBindingDate);
    }

    public String getDeviceAnnouncer() {
        return VehicleVIN;
    }

    public void setDeviceAnnouncer(String mDeviceAnnouncer) {
        this.VehicleVIN = mDeviceAnnouncer;
    }

    public boolean isActivated() {
        return IsActivated;
    }

    public void setIsActivated(boolean mIsActivated) {
        this.IsActivated = mIsActivated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<DriverCardData> CREATOR
            = new Parcelable.Creator<DriverCardData>() {
        public DriverCardData createFromParcel(Parcel in) {
            return new DriverCardData(in);
        }

        public DriverCardData[] newArray(int size) {
            return new DriverCardData[size];
        }
    };

    @Override
    public String toString()
    {
        String res = String.format("IdCard:%s\t\nName:%s\t\nInsertDate:%s\t\nDeviceAnnouncer:%s\t\nCustomer:%s\t\nIsActivated:%b\t\nStartDate:%s",
                IdCard, Name, InsertingDate, VehicleVIN, CustomerName, IsActivated, StartDate);
        return res;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getStatus() {
        return Status;
    }

    public String getCustomerUniqueId() {
        return CustomerUniqueId;
    }

    public void setCustomerUniqueId(String customerUniqueId) {
        CustomerUniqueId = customerUniqueId;
    }
}
