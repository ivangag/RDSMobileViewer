package com.viewer.rds.actia.rdsmobileviewer;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CRDSCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.DriversCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.VehiclesCardsFragment;

public abstract class PlaceholderFragmentFactory extends ListFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    //public static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";
    public static final String ARG_SET_TITLE_ACTION_BAR = "ARG_SET_TITLE_ACTION_BAR";
    public static String ARG_FRAGMENT_TYPE = "ARG_FRAGMENT_TYPE";

    //public VehicleListAdapter mVehicleAdapter;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment newInstance(int sectionNumber)
    {
        Fragment fragment = null;
        final int sectionNumberActual = sectionNumber - 1;
        switch (sectionNumberActual) {
            case Utils.TAB_POSITION_VEHICLES:
                fragment = VehiclesCardsFragment.newInstance(DownloadUtility.DownloadRequestType.VEHICLE_NOT_TRUSTED,false);
                break;
            case Utils.TAB_POSITION_DRIVERS:
                fragment = DriversCardsFragment.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_NOT_TRUSTED, false);
                break;
            case Utils.TAB_POSITION_CRDS:
                fragment = CRDSCardsFragment.newInstance(DownloadUtility.DownloadRequestType.CRDS_NOT_TRUSTED,false);
                break;
            /*
            case GlobalConstants.MAIN_TAB_CUSTOMERS:
                fragment = CustomerListFragment.newInstance(sectionNumberActual);
                break;
            case GlobalConstants.MAIN_TAB_CARDS_TEST:
                //fragment = ListBaseFragment.getInstance(sectionNumberActual);
                fragment = MainMenuCardsFragment.newInstance();
                break;
                */
            default:
                break;

        }
        return fragment;
    }

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    public PlaceholderFragmentFactory() {
    }
}
