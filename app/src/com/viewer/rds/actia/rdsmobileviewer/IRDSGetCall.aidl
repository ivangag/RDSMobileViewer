// IRDSGetCall.aidl
package com.viewer.rds.actia.rdsmobileviewer;

import java.util.List;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;
interface IRDSGetCall {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    //List<CRDSCustom>        getCRDSNotActivated ();
    List<VehicleCustom>    getVehiclesNotActivated ();
}
