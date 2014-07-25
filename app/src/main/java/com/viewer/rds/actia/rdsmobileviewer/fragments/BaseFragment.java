package com.viewer.rds.actia.rdsmobileviewer.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;

/**
 * Base Fragment
 *
 */
public abstract class BaseFragment extends Fragment {

    public interface IFragmentsInteractionListener {
        public void onFirstFragmentVisualisation(Fragment sender, DownloadUtility.DownloadRequestType requestType);
        public void onRequireVehicleDiagnosticData(String vehicleVIN,boolean cacheIfExist);
        public void onCustomerSelected(String CustomerAncodice);
        public void onCustomerVehiclesDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist);
        public void onCustomerDrivesDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType, String CustomerAncodice,boolean cacheIfExist);
        public void onCustomerCRDSDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle();

    }




    protected void setTitle(){
        if(getIfHastToSetTitle()) {
            String txtSet = getCustomTitleText();
            if ((txtSet == null)
                    || txtSet.equals("")) {
                getActivity().setTitle(getTitleResourceId());
            } else
                setTitleText();
        }
    }

    private void setTitleText() {
        getActivity().setTitle(getCustomTitleText());
    }

    public abstract boolean getIfHastToSetTitle();

    public abstract String getCustomTitleText();
    public abstract int getTitleResourceId();

    public abstract String getUniqueCustomerCode() ;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

