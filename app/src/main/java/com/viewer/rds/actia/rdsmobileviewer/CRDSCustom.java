package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

/**
 * Created by igaglioti on 09/07/2014.
 */
public class CRDSCustom implements Parcelable {

    private String mActivationState;


    private String mAppName;


    private String mAppType;


    private String mCAP;


    private String mCellulare;


    private String mCitta;


    private String mCulture;


    private String mDataRicezione;


    private String mDiagnosticAction;


    private String mEmail;


    private String mFax;


    private String mIndirizzo;


    private String mLastLifeSignal;


    private String mModoRicezioneCodice;


    private String mNazione;


    private String mPartitaIva;


    private String mPassword;


    private String mRagioneSociale;


    private String mResponsabile;


    private String mTelefono;


    private String mVersione;

    private String mXML;

    private String mCRDSId;

    public CRDSCustom(Parcel in) {

    }

    public CRDSCustom(String CRDSGuid) {
        mCRDSId = CRDSGuid;
    }

    public CRDSCustom() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<CRDSCustom> CREATOR
            = new Parcelable.Creator<CRDSCustom>() {
        public CRDSCustom createFromParcel(Parcel in) {
            return new CRDSCustom(in);
        }

        public CRDSCustom[] newArray(int size) {
            return new CRDSCustom[size];
        }
    };

    public String getCRDSId() {
        return mCRDSId;
    }

    public void setCRDSId(String mCRDSId) {
        this.mCRDSId = mCRDSId;
    }

    public String getActivationState() {
        return mActivationState;
    }

    public void setActivationState(String mActivationState) {
        this.mActivationState = mActivationState;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getAppType() {
        return mAppType;
    }

    public void setAppType(String mAppType) {
        this.mAppType = mAppType;
    }

    public String getCAP() {
        return mCAP;
    }

    public void setCAP(String mCAP) {
        this.mCAP = mCAP;
    }

    public String getCellulare() {
        return mCellulare;
    }

    public void setCellulare(String mCellulare) {
        this.mCellulare = mCellulare;
    }

    public String getCitta() {
        return mCitta;
    }

    public void setCitta(String mCitta) {
        this.mCitta = mCitta;
    }

    public String getCulture() {
        return mCulture;
    }

    public void setCulture(String mCulture) {
        this.mCulture = mCulture;
    }

    public String getDataRicezione() {
        return mDataRicezione;
    }

    public void setDataRicezione(String mDataRicezione) {
        this.mDataRicezione = Utils.getDateTimeFromTicks(mDataRicezione);
    }

    public String getDiagnosticAction() {
        return mDiagnosticAction;
    }

    public void setDiagnosticAction(String mDiagnosticAction) {
        this.mDiagnosticAction = Utils.getDateTimeFromTicks(mDiagnosticAction);
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getFax() {
        return mFax;
    }

    public void setFax(String mFax) {
        this.mFax = mFax;
    }

    public String getIndirizzo() {
        return mIndirizzo;
    }

    public void setIndirizzo(String mIndirizzo) {
        this.mIndirizzo = mIndirizzo;
    }

    public String getLastLifeSignal() {
        return mLastLifeSignal;
    }

    public void setLastLifeSignal(String mLastLifeSignal) {
        this.mLastLifeSignal = Utils.getDateTimeFromTicks(mLastLifeSignal);
    }

    public String getModoRicezioneCodice() {
        return mModoRicezioneCodice;
    }

    public void setModoRicezioneCodice(String mModoRicezioneCodice) {
        this.mModoRicezioneCodice = mModoRicezioneCodice;
    }

    public String getNazione() {
        return mNazione;
    }

    public void setNazione(String mNazione) {
        this.mNazione = mNazione;
    }

    public String getPartitaIva() {
        return mPartitaIva;
    }

    public void setPartitaIva(String mPartitaIva) {
        this.mPartitaIva = mPartitaIva;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getRagioneSociale() {
        return mRagioneSociale;
    }

    public void setRagioneSociale(String mRagioneSociale) {
        this.mRagioneSociale = mRagioneSociale;
    }

    public String getResponsabile() {
        return mResponsabile;
    }

    public void setResponsabile(String mResponsabile) {
        this.mResponsabile = mResponsabile;
    }

    public String getTelefono() {
        return mTelefono;
    }

    public void setTelefono(String mTelefono) {
        this.mTelefono = mTelefono;
    }

    public String getVersione() {
        return mVersione;
    }

    public void setVersione(String mVersione) {
        this.mVersione = mVersione;
    }

    public String getXML() {
        return mXML;
    }

    public void setXML(String mXML) {
        this.mXML = mXML;
    }


    @Override
    public String toString()
    {
        String res = String.format("GUID:%s\t\nRagioneSociale:%s\t\nResp:%s\t\nEmail:%s\t\nCittà:%s\t\nDataRicezione:%s\t\nApplicazione:%s(V%s)",
                mCRDSId,mRagioneSociale,mResponsabile,mEmail,mCitta,mDataRicezione,mAppName,mVersione);
        return res;
    }
}
