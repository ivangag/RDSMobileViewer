package com.viewer.rds.actia.rdsmobileviewer.db;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;

/**
 * Created by igaglioti on 09/09/2014.
 */
@Table(name = "VehicleNotBound")
public class VehicleNotBound extends VehicleCustom{

    public VehicleNotBound()
    {
        super();
    }
}
