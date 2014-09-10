package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

/**
 * Created by igaglioti on 09/07/2014.
 */
@Table(name = "CRDS")
public class CRDSCustom extends Model implements Parcelable {

    @Column
    private transient String CustomerUniqueId;

    @Column
    private String ActivationState;

    @Column
    private String AppName;

    @Column
    private String AppType;

    @Column
    private String CAP;

    @Column
    private String Cellulare;

    @Column
    private String Citta;

    @Column
    private String Culture;

    @Column
    private String DataRicezione;

    @Column
    private String DiagnosticAction;

    @Column
    private String Email;

    @Column
    private String Fax;

    @Column
    private String Indirizzo;

    @Column
    private String LastLifeSignal;

    @Column
    private String ModoRicezioneCodice;

    @Column
    private String Nazione;

    @Column
    private String PartitaIva;

    @Column
    private String Password;

    @Column
    private String RagioneSociale;

    @Column
    private String Responsabile;

    @Column
    private String Telefono;

    @Column
    private String Versione;

    @Column
    private String XML;

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String GUID;

    public CRDSCustom(Parcel in) {

    }

    public CRDSCustom(String CRDSGuid) {
        GUID = CRDSGuid;
    }

    public CRDSCustom() {
        super();
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
        return GUID;
    }

    public void setCRDSId(String mCRDSId) {
        this.GUID = mCRDSId;
    }

    public String getActivationState() {
        return ActivationState;
    }

    public void setActivationState(String mActivationState) {
        this.ActivationState = mActivationState;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String mAppName) {
        this.AppName = mAppName;
    }

    public String getAppType() {
        return AppType;
    }

    public void setAppType(String mAppType) {
        this.AppType = mAppType;
    }

    public String getCAP() {
        return CAP;
    }

    public void setCAP(String mCAP) {
        this.CAP = mCAP;
    }

    public String getCellulare() {
        return Cellulare;
    }

    public void setCellulare(String mCellulare) {
        this.Cellulare = mCellulare;
    }

    public String getCitta() {
        return Citta;
    }

    public void setCitta(String mCitta) {
        this.Citta = mCitta;
    }

    public String getCulture() {
        return Culture;
    }

    public void setCulture(String mCulture) {
        this.Culture = mCulture;
    }

    public String getDataRicezione() {
        return DataRicezione;
    }

    public void setDataRicezione(String mDataRicezione) {
        this.DataRicezione = Utils.getDateTimeFromTicks(mDataRicezione);
    }

    public String getDiagnosticAction() {
        return DiagnosticAction;
    }

    public void setDiagnosticAction(String mDiagnosticAction) {
        this.DiagnosticAction = Utils.getDateTimeFromTicks(mDiagnosticAction);
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String mEmail) {
        this.Email = mEmail;
    }

    public String getFax() {
        return Fax;
    }

    public void setFax(String mFax) {
        this.Fax = mFax;
    }

    public String getIndirizzo() {
        return Indirizzo;
    }

    public void setIndirizzo(String mIndirizzo) {
        this.Indirizzo = mIndirizzo;
    }

    public String getLastLifeSignal() {
        return LastLifeSignal;
    }

    public void setLastLifeSignal(String mLastLifeSignal) {
        this.LastLifeSignal = Utils.getDateTimeFromTicks(mLastLifeSignal);
    }

    public String getModoRicezioneCodice() {
        return ModoRicezioneCodice;
    }

    public void setModoRicezioneCodice(String mModoRicezioneCodice) {
        this.ModoRicezioneCodice = mModoRicezioneCodice;
    }

    public String getNazione() {
        return Nazione;
    }

    public void setNazione(String mNazione) {
        this.Nazione = mNazione;
    }

    public String getPartitaIva() {
        return PartitaIva;
    }

    public void setPartitaIva(String mPartitaIva) {
        this.PartitaIva = mPartitaIva;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String mPassword) {
        this.Password = mPassword;
    }

    public String getRagioneSociale() {
        return RagioneSociale;
    }

    public void setRagioneSociale(String mRagioneSociale) {
        this.RagioneSociale = mRagioneSociale;
    }

    public String getResponsabile() {
        return Responsabile;
    }

    public void setResponsabile(String mResponsabile) {
        this.Responsabile = mResponsabile;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String mTelefono) {
        this.Telefono = mTelefono;
    }

    public String getVersione() {
        return Versione;
    }

    public void setVersione(String mVersione) {
        this.Versione = mVersione;
    }

    public String getXML() {
        return XML;
    }

    public void setXML(String mXML) {
        this.XML = mXML;
    }


    @Override
    public String toString()
    {
        String res = String.format("GUID:%s\t\nRagioneSociale:%s\t\nResp:%s\t\nEmail:%s\t\nCittà:%s\t\nDataRicezione:%s\t\nApplicazione:%s(V%s)",
                GUID, RagioneSociale, Responsabile, Email, Citta, DataRicezione, AppName, Versione);
        return res;
    }

    public String getCustomerUniqueId() {
        return CustomerUniqueId;
    }

    public void setCustomerUniqueId(String customerUniqueId) {
        CustomerUniqueId = customerUniqueId;
    }
}
