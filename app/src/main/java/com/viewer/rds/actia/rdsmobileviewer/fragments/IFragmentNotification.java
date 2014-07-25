package com.viewer.rds.actia.rdsmobileviewer.fragments;

/**
 * Created by igaglioti on 14/07/2014.
 */
public interface IFragmentNotification {
    public void OnUpdateData(String UniqueCustomerCode, Object dataContentList, Class itemBaseType);
    public void OnFilterData(String filterPattern);
}
