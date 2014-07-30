// RDSClientRequest.aidl
package com.viewer.rds.actia.rdsmobileviewer;

// Declare any non-default types here with import statements

import com.viewer.rds.actia.rdsmobileviewer.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.IRDSClientResponse;
interface IRDSClientRequest {
   /**
    * A one-way (non-blocking) call to the AcronymServiceAsync that
    * retrieves information about an acronym from the Acronym Web
    * service.  The AcronymServiceAsync subsequently uses the
    * AcronymResults parameter to return a List of AcronymData
    * containing the results from the Web service back to the
    * AcronymActivity.
    */
     oneway void fetchRemoteData (in IRDSClientResponse remoteResult,
                                   in DownloadRequestSchema downloadRequest);
}
