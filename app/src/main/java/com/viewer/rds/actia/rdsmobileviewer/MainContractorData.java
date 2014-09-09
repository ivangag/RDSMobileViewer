package com.viewer.rds.actia.rdsmobileviewer;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.viewer.rds.actia.rdsmobileviewer.db.RDSDBHelper;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

/**
 * Created by igaglioti on 11/07/2014.
 */

public class MainContractorData implements Parcelable{

    private String Email;

    private String FriendlyName;


    private int IdCustomer;


    private String InsertDate;


    private boolean IsAutomaticDriverAssociationEnabled;


    private boolean IsEmailForwardServiceActive;


    private boolean IsFilePushingServiceActive;


    private boolean IsSuperCRDSServiceActive;


    private String Ancodice;


    private int ContractLeaseDuration;

    public MainContractorData(Parcel in) {

        Ancodice = in.readString();
        Email = in.readString();
        FriendlyName = in.readString();
        InsertDate = in.readString();
        ContractLeaseDuration = in.readInt();
        boolean[] values = new boolean[4];
        in.readBooleanArray(values);
        IsAutomaticDriverAssociationEnabled = values[0];
        IsEmailForwardServiceActive = values[1];
        IsFilePushingServiceActive = values[2];
        IsSuperCRDSServiceActive = values[3];
    }

    public MainContractorData() {

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getFriendlyName() {
        return FriendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.FriendlyName = friendlyName;
    }

    public int getIdCustomer() {
        return IdCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.IdCustomer = idCustomer;
    }

    public String getInsertDate() {
        return InsertDate;
    }

    public void setInsertDate(String insertDate) {
        this.InsertDate = Utils.getDateTimeFromTicks(insertDate);
    }

    public boolean isAutomaticDriverAssociationEnabled() {
        return IsAutomaticDriverAssociationEnabled;
    }

    public void setIsAutomaticDriverAssociationEnabled(boolean automaticDriverAssociationEnabled) {
        this.IsAutomaticDriverAssociationEnabled = automaticDriverAssociationEnabled;
    }

    public boolean isEmailForwardServiceActive() {
        return IsEmailForwardServiceActive;
    }

    public void setIsEmailForwardServiceActive(boolean emailForwardServiceActive) {
        this.IsEmailForwardServiceActive = emailForwardServiceActive;
    }

    public boolean isFilePushingServiceActive() {
        return IsFilePushingServiceActive;
    }

    public void setIsFilePushingServiceActive(boolean filePushingServiceActive) {
        this.IsFilePushingServiceActive = filePushingServiceActive;
    }

    public boolean isSuperCRDSServiceActive() {
        return IsSuperCRDSServiceActive;
    }

    public void setIsSuperCRDSServiceActive(boolean superCRDSServiceActive) {
        this.IsSuperCRDSServiceActive = superCRDSServiceActive;
    }

    public String getAncodice() {
        return Ancodice;
    }

    public void setAncodice(String mAncodice) {
        this.Ancodice = mAncodice;
    }


    public static final Parcelable.Creator<MainContractorData> CREATOR
            = new Parcelable.Creator<MainContractorData>() {
        public MainContractorData createFromParcel(Parcel in) {
            return new MainContractorData(in);
        }

        public MainContractorData[] newArray(int size) {
            return new MainContractorData[size];
        }
    };



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Ancodice);
        dest.writeString(Email);
        dest.writeString(FriendlyName);
        dest.writeString(InsertDate);
        dest.writeInt(ContractLeaseDuration);
        dest.writeBooleanArray(new boolean[]{IsAutomaticDriverAssociationEnabled, IsEmailForwardServiceActive, IsFilePushingServiceActive, IsSuperCRDSServiceActive});
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public String toString()
    {
        /*
        String res = String.format("Name:%s;AnCode:%s;InsertDate:%s;FilePush:%b;SuperCRDS:%b;EmailPush:%b;AutoDriver:%b",
                FriendlyName,Ancodice,InsertDate,IsFilePushingServiceActive,IsSuperCRDSServiceActive,IsEmailForwardServiceActive,IsAutomaticDriverAssociationEnabled);
                */
        String res = String.format("Name:%s\t\nPRIMARY_CUSTOMER_ID:%d\t\nAnCodice:%s\t\nInsertDate:%s\t\nFilePush:%b\t\nSuperCRDS:%b\t\nEmailPush:%b\t\nAutoDriver:%b",
                getFriendlyName(),getIdCustomer(),getAncodice(),getInsertDate(),
                IsFilePushingServiceActive, IsSuperCRDSServiceActive, IsEmailForwardServiceActive, IsAutomaticDriverAssociationEnabled);
        return res;
    }

    public static MainContractorData getCustomerDataFromValues(ContentValues values) {
        MainContractorData customerData = new MainContractorData();
        customerData.setFriendlyName(values.getAsString(RDSDBHelper.NAME));
        customerData.setAncodice(values.getAsString(RDSDBHelper.ANCODICE));
        customerData.setEmail(values.getAsString(RDSDBHelper.EMAIL));
        customerData.setIdCustomer(Integer.valueOf(values.getAsString(RDSDBHelper.ID_CUSTOMER)));
        customerData.setInsertDate(values.getAsString(RDSDBHelper.INSERT_DATE));
        customerData.setIsAutomaticDriverAssociationEnabled(Boolean.valueOf(values.getAsString(RDSDBHelper.AUTO_DRIVER)));
        customerData.setIsEmailForwardServiceActive(Boolean.valueOf(values.getAsString(RDSDBHelper.EMAIL_FORWARD)));
        customerData.setIsSuperCRDSServiceActive(Boolean.valueOf(values.getAsString(RDSDBHelper.SUPER_CRDS)));
        customerData.setIsFilePushingServiceActive(Boolean.valueOf(values.getAsString(RDSDBHelper.FILE_PUSH)));
        return customerData;
    }

    public static ContentValues getCVFromCustomerData(MainContractorData customer) {
        ContentValues cv = new ContentValues();
        cv.put(RDSDBHelper.NAME, customer.getFriendlyName());
        cv.put(RDSDBHelper.ANCODICE, customer.getAncodice());
        cv.put(RDSDBHelper.EMAIL,customer.getEmail());
        cv.put(RDSDBHelper.ID_CUSTOMER, customer.getIdCustomer());
        cv.put(RDSDBHelper.INSERT_DATE,customer.getInsertDate());
        cv.put(RDSDBHelper.AUTO_DRIVER, customer.isAutomaticDriverAssociationEnabled());
        cv.put(RDSDBHelper.EMAIL_FORWARD,customer.isEmailForwardServiceActive());
        cv.put(RDSDBHelper.SUPER_CRDS, customer.isSuperCRDSServiceActive());
        cv.put(RDSDBHelper.FILE_PUSH,customer.isFilePushingServiceActive());
        return cv;
    }
}
