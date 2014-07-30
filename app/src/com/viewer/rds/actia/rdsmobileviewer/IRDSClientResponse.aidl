// RDSClientResponse.aidl
package com.viewer.rds.actia.rdsmobileviewer;

// Declare any non-default types here with import statements
import com.viewer.rds.actia.rdsmobileviewer.ResultOperation;
import com.viewer.rds.actia.rdsmobileviewer.DownloadRequestSchema;
interface IRDSClientResponse {
    /**
     * This one-way (non-blocking) method allows the
     * RDSViewerService to return the List of objects results
     * associated with a one-way RDSClientRequest.callRequest()
     * call.
     */
    oneway void sendRemoteDataResult(in ResultOperation result, in DownloadRequestSchema downloadRequest);
}
