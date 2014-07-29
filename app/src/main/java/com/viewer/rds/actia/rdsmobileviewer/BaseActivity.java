package com.viewer.rds.actia.rdsmobileviewer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.ShareActionProvider;

import com.viewer.rds.actia.rdsmobileviewer.fragments.BaseFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CRDSCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.DownloadHandlingFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.DriversCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.IFragmentNotification;
import com.viewer.rds.actia.rdsmobileviewer.fragments.VehiclesCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.utils.CacheDataManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;


public abstract class BaseActivity extends Activity implements
        DownloadUtility.IRemoteDownloadDataListener,BaseFragment.IFragmentsInteractionListener {

    public final static String ACTIVITY_TYPE = "ACTIVITY_TYPE";

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
    final protected static String FRAGMENT_DOWNLOAD_TAG  = "FRAGMENT_DOWNLOAD_TAG";

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
        /*
    }
        if(!isFragmentsInit)
        {
            mMenuCardsFragment = MainMenuCardsFragment.newInstance(DownloadUtility.DownloadRequestType.MAIN_MENU, true);
            mCustomerListFragment = CustomersCardsFragment.newInstance(DownloadUtility.DownloadRequestType.CUSTOMERS_LIST, true);
            mVehiclesCustomerListFragment = VehiclesCardsFragment.newInstance(DownloadUtility.DownloadRequestType.VEHICLES_OWNED, true);
            mCRDCustomerCardsFragment = CRDSCardsFragment.newInstance(DownloadUtility.DownloadRequestType.CRDS_OWNED, true);
            mDriversCustomerListFragment = DriversHolderFragment.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_OWNED, true);
            isFragmentsInit = true;
        }
        */
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

        handleDownloadDataFinished(requestType,result);
        hideProgressDialog();
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
        super.onStart();


    }

    @Override
    public void onStop()   {
        //DownloadUtility.getInstance().removeAllListeners();
        super.onStop();
    }

    @Override
    public void onBackPressed() {

/*        if((mFragmentManager != null)
                && !mFragmentManager.findFragmentById(R.id.fragment_main).getTag().equals(FRAGMENT_MAIN_MENU_TAG))
        {
            HideDetailsLayout();
        }*/
        //if(!isProgressDialogVisible())
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == DownloadUtility.DOWNLOAD_DATA_REQUEST)
        {
            if(resultCode == DownloadUtility.DOWNLOAD_RESULT_OK) {
                DownloadRequestSchema downloadRequestSchema = data.getExtras().getParcelable(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE);
                handleDownloadDataFinished(downloadRequestSchema, CacheDataManager.getInstance().getValue(downloadRequestSchema));
            }
        }
    }

    protected void makeDownloadRequest(BaseFragment requiringFragment, DownloadUtility.DownloadRequestType downloadRequestType, boolean getCacheIfExists) {
        DownloadRequestSchema request = DownloadRequestSchema.newInstance(downloadRequestType,
                requiringFragment.getUniqueCustomerCode(), "", getCacheIfExists);

        if(downloadRequestType.equals(DownloadUtility.DownloadRequestType.CUSTOMERS_LIST))
        {
            final Fragment downloadFragment = getFragmentManager().findFragmentByTag(FRAGMENT_DOWNLOAD_TAG);
            if(downloadFragment != null)
            {
                showProgressDialog("");
                ((DownloadHandlingFragment)(downloadFragment)).startDownloadRequest(request);
            }
            else {
                Bundle args = new Bundle();
                args.putParcelable(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE, request);
                Intent intent = new Intent(this, DownloadActivity.class);
                intent.putExtras(args);
                startActivityForResult(intent, DownloadUtility.DOWNLOAD_DATA_REQUEST);
            }
        }
        else {
            requireDataDownload(request);
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
                result = CacheDataManager.getInstance().getCustomers(getApplicationContext());
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
        Fragment fragment = getFragment(fragmentLayoutId);
        if(fragment != null)
            return Enum.valueOf(DownloadUtility.DownloadRequestType.class,fragment.getArguments().getString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
        return null;
    }

    protected Fragment getFragment(int fragmentLayoutId)
    {
        return mFragmentManager.findFragmentById(fragmentLayoutId);
    }

}
