package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

/**
 * Created by igaglioti on 11/07/2014.
 */
public class MainContractorData implements Parcelable{

    private String mEmail;


    private String mFriendlyName;


    private int mIdCustomer;


    private String mInsertDate;


    private boolean mIsAutomaticDriverAssociationEnabled;


    private boolean mIsEmailForwardServiceActive;


    private boolean mIsFilePushingServiceActive;


    private boolean mIsSuperCRDSServiceActive;


    private String mAncodice;


    private int mContractLeaseDuration;

    public MainContractorData(Parcel in) {

    }

    public MainContractorData() {

    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.mFriendlyName = friendlyName;
    }

    public int getIdCustomer() {
        return mIdCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.mIdCustomer = idCustomer;
    }

    public String getInsertDate() {
        return mInsertDate;
    }

    public void setInsertDate(String insertDate) {
        this.mInsertDate = Utils.getDateTimeFromTicks(insertDate);
    }

    public boolean ismIsAutomaticDriverAssociationEnabled() {
        return mIsAutomaticDriverAssociationEnabled;
    }

    public void setIsAutomaticDriverAssociationEnabled(boolean automaticDriverAssociationEnabled) {
        this.mIsAutomaticDriverAssociationEnabled = automaticDriverAssociationEnabled;
    }

    public boolean ismIsEmailForwardServiceActive() {
        return mIsEmailForwardServiceActive;
    }

    public void setIsEmailForwardServiceActive(boolean emailForwardServiceActive) {
        this.mIsEmailForwardServiceActive = emailForwardServiceActive;
    }

    public boolean ismIsFilePushingServiceActive() {
        return mIsFilePushingServiceActive;
    }

    public void setIsFilePushingServiceActive(boolean filePushingServiceActive) {
        this.mIsFilePushingServiceActive = filePushingServiceActive;
    }

    public boolean ismIsSuperCRDSServiceActive() {
        return mIsSuperCRDSServiceActive;
    }

    public void setIsSuperCRDSServiceActive(boolean superCRDSServiceActive) {
        this.mIsSuperCRDSServiceActive = superCRDSServiceActive;
    }

    public String getAncodice() {
        return mAncodice;
    }

    public void setAncodice(String mAncodice) {
        this.mAncodice = mAncodice;
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
        dest.writeString(mAncodice);
        dest.writeString(mEmail);
        dest.writeString(mFriendlyName);
        dest.writeString(mInsertDate);
        dest.writeInt(mContractLeaseDuration);
        dest.writeBooleanArray(new boolean[]{mIsAutomaticDriverAssociationEnabled, mIsEmailForwardServiceActive, mIsFilePushingServiceActive, mIsSuperCRDSServiceActive});
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
                mFriendlyName,mAncodice,mInsertDate,mIsFilePushingServiceActive,mIsSuperCRDSServiceActive,mIsEmailForwardServiceActive,mIsAutomaticDriverAssociationEnabled);
                */
        String res = String.format("Name:%s\t\nID:%d\t\nAnCodice:%s\t\nInsertDate:%s\t\nFilePush:%b\t\nSuperCRDS:%b\t\nEmailPush:%b\t\nAutoDriver:%b",
                getFriendlyName(),getIdCustomer(),getAncodice(),getInsertDate(),
                mIsFilePushingServiceActive,mIsSuperCRDSServiceActive,mIsEmailForwardServiceActive,mIsAutomaticDriverAssociationEnabled);
        return res;
    }
}
