package com.viewer.rds.actia.rdsmobileviewer.db;

import android.graphics.AvoidXfermode;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;

/**
 * Created by igaglioti on 09/09/2014.
 */
@Table(name = "VehicleAssociated")
public class VehicleBound extends VehicleCustom {

    @Column(name = "Customer")
    private MainContractorData Customer;

    public MainContractorData getCustomer() {
        return Customer;
    }

    public void setCustomer(MainContractorData customer) {
        Customer = customer;
    }
    public VehicleBound()
    {
        super();
    }
}
