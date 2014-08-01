package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by igaglioti on 09/07/2014.
 */
public class CRDSCustom implements Parcelable {


    private String activationState;


    private String appName;


    private String appType;


    private String cap;


    private String cellulare;


    private String citta;


    private String culture;


    private String dataRicezione;


    private String diagnosticAction;


    private String email;


    private String fax;


    private String indirizzo;


    private String lastLifeSignal;


    private String modoRicezioneCodice;


    @JsonProperty("Nazione")
    private String nazione;


    private String partitaIva;


    private String password;


    private String ragioneSociale;


    private String responsabile;


    private String telefono;


    private String versione;

    private String xml;

    private String crdsid;

    public CRDSCustom(Parcel in) {

    }

    public CRDSCustom(String CRDSGuid) {
        crdsid = CRDSGuid;
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
        return crdsid;
    }

    public void setCRDSId(String mCRDSId) {
        this.crdsid = mCRDSId;
    }

    public String getActivationState() {
        return activationState;
    }

    public void setActivationState(String mActivationState) {
        this.activationState = mActivationState;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String mAppName) {
        this.appName = mAppName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String mAppType) {
        this.appType = mAppType;
    }

    public String getCAP() {
        return cap;
    }

    public void setCAP(String mCAP) {
        this.cap = mCAP;
    }

    public String getCellulare() {
        return cellulare;
    }

    public void setCellulare(String mCellulare) {
        this.cellulare = mCellulare;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String mCitta) {
        this.citta = mCitta;
    }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String mCulture) {
        this.culture = mCulture;
    }

    public String getDataRicezione() {
        return dataRicezione;
    }

    public void setDataRicezione(String mDataRicezione) {
        this.dataRicezione = Utils.getDateTimeFromTicks(mDataRicezione);
    }

    public String getDiagnosticAction() {
        return diagnosticAction;
    }

    public void setDiagnosticAction(String mDiagnosticAction) {
        this.diagnosticAction = Utils.getDateTimeFromTicks(mDiagnosticAction);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mEmail) {
        this.email = mEmail;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String mFax) {
        this.fax = mFax;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String mIndirizzo) {
        this.indirizzo = mIndirizzo;
    }

    public String getLastLifeSignal() {
        return lastLifeSignal;
    }

    public void setLastLifeSignal(String mLastLifeSignal) {
        this.lastLifeSignal = Utils.getDateTimeFromTicks(mLastLifeSignal);
    }

    public String getModoRicezioneCodice() {
        return modoRicezioneCodice;
    }

    public void setModoRicezioneCodice(String mModoRicezioneCodice) {
        this.modoRicezioneCodice = mModoRicezioneCodice;
    }

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String mNazione) {
        this.nazione = mNazione;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String mPartitaIva) {
        this.partitaIva = mPartitaIva;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String mPassword) {
        this.password = mPassword;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public void setRagioneSociale(String mRagioneSociale) {
        this.ragioneSociale = mRagioneSociale;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(String mResponsabile) {
        this.responsabile = mResponsabile;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String mTelefono) {
        this.telefono = mTelefono;
    }

    public String getVersione() {
        return versione;
    }

    public void setVersione(String mVersione) {
        this.versione = mVersione;
    }

    public String getXML() {
        return xml;
    }

    public void setXML(String mXML) {
        this.xml = mXML;
    }


    @Override
    public String toString()
    {
        String res = String.format("GUID:%s\t\nRagioneSociale:%s\t\nResp:%s\t\nEmail:%s\t\nCittà:%s\t\nDataRicezione:%s\t\nApplicazione:%s(V%s)",
                crdsid, ragioneSociale, responsabile, email, citta, dataRicezione, appName, versione);
        return res;
    }
}
