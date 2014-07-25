package com.viewer.rds.actia.rdsmobileviewer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.fragments.BaseFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CRDSCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CustomersCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.DriversCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.IFragmentNotification;
import com.viewer.rds.actia.rdsmobileviewer.fragments.MainMenuCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.VehiclesCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.utils.CacheDataManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public abstract class BaseActivity extends Activity implements
        DownloadUtility.IRemoteDownloadDataListener,BaseFragment.IFragmentsInteractionListener {

    public final static String ACTIVITY_TYPE = "ACTIVITY_TYPE";

    protected final MainMenuCardsFragment mMenuCardsFragment = MainMenuCardsFragment.newInstance(DownloadUtility.DownloadRequestType.MAIN_MENU, true);
    protected final CustomersCardsFragment mCustomerListFragment = CustomersCardsFragment.newInstance(DownloadUtility.DownloadRequestType.CUSTOMERS_LIST, true);
    public final VehiclesCardsFragment mVehiclesCustomerListFragment = VehiclesCardsFragment.newInstance(DownloadUtility.DownloadRequestType.VEHICLES_OWNED, true);
    protected final CRDSCardsFragment mCRDCustomerCardsFragment = CRDSCardsFragment.newInstance(DownloadUtility.DownloadRequestType.CRDS_OWNED, true);
    protected final DriversCardsFragment mDriversCustomerListFragment = DriversHolderFragment.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_OWNED, true);

    protected static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    protected static final int LANDSCAPE = Configuration.ORIENTATION_LANDSCAPE;
    protected static final int PORTRAIT = Configuration.ORIENTATION_PORTRAIT;
    protected FragmentManager mFragmentManager;
    private final static DownloadUtility mDownloadUtility = DownloadUtility.getInstance();
    private ShareActionProvider mShareActionProvider;
    private boolean mIsShareIntentPending;

    final protected static String FRAGMENT_DRIVERS_TAG    = "DRIVERS_FRAGMENT";
    final protected static String FRAGMENT_VEHICLES_TAG   = "VEHICLES_FRAGMENT";
    final protected static String FRAGMENT_CRDS_TAG       = "CRDS_FRAGMENT";
    final protected static String FRAGMENT_MAIN_MENU_TAG  = "MAIN_MENU_FRAGMENT";
    final protected static String FRAGMENT_CUSTOMERS_TAG  = "CUSTOMERS_FRAGMENT";

    private ArrayList<String> mFragmentTags;
    Integer mDisplayOrientation;
    protected Fragment mCurrentTabFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init()
    {
        Utils.Init(this);
        mFragmentManager = getFragmentManager();

        mFragmentTags = new ArrayList<String>();
        mFragmentTags.add(FRAGMENT_CRDS_TAG);
        mFragmentTags.add(FRAGMENT_DRIVERS_TAG);
        mFragmentTags.add(FRAGMENT_VEHICLES_TAG);
        mFragmentTags.add(FRAGMENT_CUSTOMERS_TAG);
        mFragmentTags.add(FRAGMENT_MAIN_MENU_TAG);
    }



    public abstract void handleDownloadDataFinished(DownloadRequestSchema requestType, Object result);



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        setupMenuActions(menu);
        return true;
    }

    private void setupMenuActions(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)shareItem.getActionProvider();
        mShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

        /*
        mShareActionProvider.setShareIntent(getDefaultIntent());
        if(mIsShareIntentPending)
            updateShareIntentWithText();
            */
        mShareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
            @Override
            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getActivity().getApplicationContext(), "onQueryTextSubmit:" + query, Toast.LENGTH_SHORT).show();
                //mNetAdapter.update(query.toUpperCase());
                IFragmentNotification notifier = getCurrentDisplayedFragment();
                if(notifier != null)
                    notifier.OnFilterData(query.toUpperCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity().getApplicationContext(), "onQueryTextChange:" + newText,Toast.LENGTH_SHORT).show();
                IFragmentNotification notifier = getCurrentDisplayedFragment();
                if(notifier != null)
                    notifier.OnFilterData(newText.toUpperCase());
                return true;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //mNetAdapter.getFilter().filter(getResources().getString(R.string.filterALL));
                return true;
            }
        });
    }

    @Override
    public void onDownloadDataFinished(DownloadRequestSchema requestType, Object result) {

        hideProgressDialog();
        handleDownloadDataFinished(requestType,result);
    }

    private boolean isProgressDialogVisible(){
        return findViewById(R.id.loadingPanel).getVisibility() != View.GONE;
    }

    public abstract void hideProgressDialog();
    public abstract void showProgressDialog(String text);

    protected void showTextFile(String text)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text).setType("text/plain");
        startActivity(intent);
    }

    private IFragmentNotification getCurrentDisplayedFragment()
    {
        Fragment mFragment = null;
        String tag;
        Iterator<String> iteration = mFragmentTags.iterator();
        while(iteration.hasNext()) {
            tag = iteration.next();
            mFragment = getFragmentManager().findFragmentByTag(tag);
            if ((mFragment != null)
                    && mFragment.isVisible()) {
                // add your code here
                break;
            }
        }
        return mFragment != null ? (IFragmentNotification)mFragment: null;
    }

    protected void showNewFragment(Fragment fragment, String tag) {
        if(!fragment.isAdded())
        {
            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragment_main,fragment,tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            mFragmentManager.executePendingTransactions();
        }
    }

    protected void showNewFragment(Fragment fragment, String tag, boolean isDetailsFragment ) {

        if(mDisplayOrientation.equals(Configuration.ORIENTATION_LANDSCAPE)) {
            if (!fragment.isAdded()) {
                FragmentTransaction fragmentTransaction = mFragmentManager
                        .beginTransaction();
                if (!isDetailsFragment)
                    fragmentTransaction.replace(R.id.fragment_main, fragment, tag);
                else
                    fragmentTransaction.replace(R.id.fragment_main_details, fragment, tag);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mFragmentManager.executePendingTransactions();
            }
        }
        else
        {
            showNewFragment(fragment,tag);
        }
    }

    @Override
    public void onStart()   {
        //DownloadUtility.getInstance().addListener(CacheDataManager.getInstance());
        super.onStart();

    }

    @Override
    public void onStop()   {
        DownloadUtility.getInstance().removeAllListeners();
        super.onStop();

    }

    @Override
    public void onBackPressed() {

/*        if((mFragmentManager != null)
                && !mFragmentManager.findFragmentById(R.id.fragment_main).getTag().equals(FRAGMENT_MAIN_MENU_TAG))
        {
            HideDetailsLayout();
        }*/
        if(!isProgressDialogVisible())
            super.onBackPressed();
    }

    @Override
    public void onCustomerSelected(String CustomerAncodice) {

    }

    @Override
    public void onCustomerVehiclesDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
            requireDataDownload(DownloadRequestSchema.newInstance(downloadRequestType, CustomerAncodice, "", true));
    }

    @Override
    public void onCustomerDrivesDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
            requireDataDownload(DownloadRequestSchema.newInstance(downloadRequestType, CustomerAncodice, "", true));
    }

    @Override
    public void onCustomerCRDSDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
            requireDataDownload(DownloadRequestSchema.newInstance(downloadRequestType, CustomerAncodice, "", true));
    }


    protected void requireDataDownload(DownloadRequestSchema dataRequest)
    {
        showProgressDialog(dataRequest.getDownloadRequestType().getLocalizedName(this) + " "
                + (dataRequest.getUniqueCustomerCode()  )
        );
        DownloadUtility.getInstance().RequireDownloadAsyncTask(this, dataRequest);
    }

    @Override
    public void onRequireVehicleDiagnosticData(String vehicleVIN, boolean cacheIfExist) {
        requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.VEHICLE_DIAGNOSTIC, "", vehicleVIN, cacheIfExist));
    }

    protected void makeDownloadRequest(DownloadUtility.DownloadRequestType downloadRequestType, boolean getCacheIfExists) {
        //switch (getFragmentType(R.id.fragment_main))
        switch (downloadRequestType)
        {
            case VEHICLE_NOT_TRUSTED:
                requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.VEHICLE_NOT_TRUSTED, mVehiclesCustomerListFragment.getUniqueCustomerCode(), "", getCacheIfExists));
                break;
            case VEHICLES_OWNED:
                requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.VEHICLES_OWNED, mVehiclesCustomerListFragment.getUniqueCustomerCode(), "", getCacheIfExists));
                break;
            case CUSTOMERS_LIST:
                requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.CUSTOMERS_LIST, false));
                break;
            case CRDS_NOT_TRUSTED:
                requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.CRDS_NOT_TRUSTED, mCRDCustomerCardsFragment.getUniqueCustomerCode(), "", getCacheIfExists));
                break;
            case CRDS_OWNED:
                requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.CRDS_OWNED, mCRDCustomerCardsFragment.getUniqueCustomerCode(), "", getCacheIfExists));
                break;
            case DRIVERS_NOT_TRUSTED:
                requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_NOT_TRUSTED, mDriversCustomerListFragment.getUniqueCustomerCode(), "", getCacheIfExists));
                break;
            case DRIVERS_OWNED:
                requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_OWNED, mDriversCustomerListFragment.getUniqueCustomerCode(), "", getCacheIfExists));
                break;
            case MAIN_MENU:
                break;
            case VEHICLE_DIAGNOSTIC:
                break;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
            return rootView;
        }
    }

    public static class DriversHolderFragment extends DriversCardsFragment{

        public DriversHolderFragment() {
        }


        public static DriversHolderFragment newInstance(DownloadUtility.DownloadRequestType fragmentType, boolean setActionBarTitle)
        {
            DriversHolderFragment fragment = new DriversHolderFragment();
            Bundle args = new Bundle();
            args.putString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE, fragmentType.toString());
            args.putBoolean(PlaceholderFragmentFactory.ARG_SET_TITLE_ACTION_BAR, setActionBarTitle);
            fragment.setArguments(args);
            return fragment;
        }
    }

    public static class VehiclesHolderFragment extends VehiclesCardsFragment{
        public VehiclesHolderFragment() {
        }

        public static VehiclesHolderFragment newInstance()
        {
            return new VehiclesHolderFragment();
        }
    }

    public static class CRDSHolderFragment extends CRDSCardsFragment{
        public CRDSHolderFragment() {
        }

        public static CRDSHolderFragment newInstance()
        {
            return new CRDSHolderFragment();
        }
    }

    @Override
    public void onFirstFragmentVisualisation(Fragment sender, DownloadUtility.DownloadRequestType requestType) {
        Object result = null;
        switch (requestType)
        {
            case VEHICLE_NOT_TRUSTED:
                result = CacheDataManager.getInstance().getVehicleNotTrusted();
                break;
            case CRDS_NOT_TRUSTED:
                result = CacheDataManager.getInstance().getCRDSNotTrusted();
                break;
            case CUSTOMERS_LIST:
                result = CacheDataManager.getInstance().getCustomers();
                break;
            case VEHICLES_OWNED:
                result = CacheDataManager.getInstance().getCustomerVehicles(((BaseFragment) sender).getUniqueCustomerCode());
                break;
            case DRIVERS_OWNED:
                result = CacheDataManager.getInstance().getCustomerDrivers(((BaseFragment) sender).getUniqueCustomerCode());
                break;
            case CRDS_OWNED:
                result = CacheDataManager.getInstance().getCustomerCRDS(((BaseFragment)sender).getUniqueCustomerCode());
                break;
            case DRIVERS_NOT_TRUSTED:
                result = CacheDataManager.getInstance().getDriversNotTrusted();
                break;
            case MAIN_MENU:
                break;
            case VEHICLE_DIAGNOSTIC:
                break;
        }
        PushDataToFragment(sender,DownloadRequestSchema.newInstance(requestType, false),result);
    }

    protected void PushDataToFragment(Fragment fragment, DownloadRequestSchema requestType, Object result) {
        if(fragment.isVisible()) {
            switch (requestType.getDownloadRequestType()) {
                case CUSTOMERS_LIST:
                    ((IFragmentNotification)fragment).OnUpdateData(requestType.getUniqueCustomerCode(), result, MainContractorData.class);
                    break;
                case VEHICLES_OWNED:
                case VEHICLE_NOT_TRUSTED:
                    ((IFragmentNotification)fragment).OnUpdateData(requestType.getUniqueCustomerCode(), result, VehicleCustom.class);
                    break;
                case CRDS_OWNED:
                case CRDS_NOT_TRUSTED:
                    ((IFragmentNotification)fragment).OnUpdateData(requestType.getUniqueCustomerCode(), result, CRDSCustom.class);
                    break;
                case DRIVERS_OWNED:
                case DRIVERS_NOT_TRUSTED:
                    ((IFragmentNotification)fragment).OnUpdateData(requestType.getUniqueCustomerCode(), result, DriverCardData.class);
                    break;
                case MAIN_MENU:
                    break;
                case VEHICLE_DIAGNOSTIC:
                    break;
            }
        }
    }

    protected DownloadUtility.DownloadRequestType getFragmentType(int fragmentLayoutId)
    {
        Fragment fragment = mFragmentManager.findFragmentById(fragmentLayoutId);
        if(fragment != null)
            return Enum.valueOf(DownloadUtility.DownloadRequestType.class,fragment.getArguments().getString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
        return null;
    }

    protected Fragment getFragment(DownloadUtility.DownloadRequestType  fragmentType)
    {

        Fragment fragment = null;
        switch (fragmentType)
        {
            case VEHICLES_OWNED:
            case VEHICLE_NOT_TRUSTED:
                fragment = mVehiclesCustomerListFragment;
                break;
            case CRDS_OWNED:
            case CRDS_NOT_TRUSTED:
                fragment = mCRDCustomerCardsFragment;
                break;
            case CUSTOMERS_LIST:
                fragment = mCustomerListFragment;
                break;
            case DRIVERS_OWNED:
            case DRIVERS_NOT_TRUSTED:
                fragment = mDriversCustomerListFragment;
                break;
            case MAIN_MENU:
                fragment = mMenuCardsFragment;
                break;
            case VEHICLE_DIAGNOSTIC:
                break;
        }
        return fragment;
    }
}
