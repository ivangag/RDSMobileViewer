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
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.fragments.BaseFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CRDSCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.DownloadHandlerFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.DriversCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.IFragmentNotification;
import com.viewer.rds.actia.rdsmobileviewer.fragments.VehiclesCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.utils.CacheDataManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRDSManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.RDSEmptyDataException;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;


public abstract class BaseActivity extends Activity implements
        DownloadRDSManager.IRemoteDownloadDataListener,BaseFragment.IFragmentsInteractionListener {

    public final static String ACTIVITY_TYPE = "ACTIVITY_TYPE";

    protected static final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
    protected static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    protected static final int LANDSCAPE = Configuration.ORIENTATION_LANDSCAPE;
    protected static final int PORTRAIT = Configuration.ORIENTATION_PORTRAIT;
    protected FragmentManager mFragmentManager;
    private final static DownloadRDSManager mDownloadManager = DownloadRDSManager.getInstance();
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
        mDisplayOrientation = getResources().getConfiguration().orientation;
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



    public abstract void handleDownloadDataFinished(DownloadRequestSchema requestType, ResultOperation result);



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
    public void onDownloadDataFinished(DownloadRequestSchema requestType, ResultOperation result) {

        handleDownloadDataFinished(requestType,result);
        hideDownloadProgressDialog();
    }

    private boolean isProgressDialogVisible(){
        return findViewById(R.id.loadingPanel).getVisibility() != View.GONE;
    }

    public abstract void hideDownloadProgressDialog();
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
        //DownloadUtility.get().removeAllListeners();
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
    public void onCustomerVehiclesDataRequiredSelected(DownloadRDSManager.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
        makeDownloadRequest(DownloadRequestSchema.newInstance(downloadRequestType, CustomerAncodice, "", cacheIfExist));
    }

    @Override
    public void onCustomerDrivesDataRequiredSelected(DownloadRDSManager.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
        makeDownloadRequest(DownloadRequestSchema.newInstance(downloadRequestType, CustomerAncodice, "", cacheIfExist));
    }

    @Override
    public void onCustomerCRDSDataRequiredSelected(DownloadRDSManager.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
        makeDownloadRequest(DownloadRequestSchema.newInstance(downloadRequestType, CustomerAncodice, "", cacheIfExist));
    }


    protected void requireDataDownload(DownloadRequestSchema dataRequest)
    {
        showProgressDialog(dataRequest.getDownloadRequestType().getLocalizedName(this) + " "
                + (dataRequest.getUniqueCustomerCode()  )
        );
        DownloadRDSManager.getInstance().RequireDownloadAsyncTask(this, dataRequest);
    }

    @Override
    public void onRequireVehicleDiagnosticData(String vehicleVIN, boolean cacheIfExist) {
        makeDownloadRequest(DownloadRequestSchema.newInstance(DownloadRDSManager.DownloadRequestType.VEHICLE_DIAGNOSTIC, "", vehicleVIN, cacheIfExist));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == DownloadRDSManager.DOWNLOAD_DATA_REQUEST)
        {
            if(resultCode == DownloadRDSManager.DOWNLOAD_RESULT_OK) {
                DownloadRequestSchema downloadRequestSchema = data.getExtras().getParcelable(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE);
                try {
                    handleDownloadDataFinished(downloadRequestSchema, ResultOperation.newInstance(true, "", CacheDataManager.get().getValue(downloadRequestSchema)));
                } catch (RDSEmptyDataException e) {
                    e.printStackTrace();
                }
            }
        }
    }
        protected void makeDownloadRequest(DownloadRequestSchema request ) {


        final Fragment downloadFragment = getFragmentManager().findFragmentByTag(FRAGMENT_DOWNLOAD_TAG);
        if(downloadFragment != null)
        {
            //setTextMsgDownloadFragment(request, downloadFragment);
            showProgressDialog("");
            ((DownloadHandlerFragment)(downloadFragment)).startDownloadRequest(request);
        }
        /*
        if(downloadRequestType.equals(DownloadManager.DownloadRequestType.CUSTOMERS_LIST))
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
                startActivityForResult(intent, DownloadManager.DOWNLOAD_DATA_REQUEST);
            }
        }
          */
        else {
            requireDataDownload(request);
        }

    }

    protected void setTextMsgDownloadFragment(DownloadRequestSchema request, Fragment downloadFragment) {
        TextView txtLoading = (TextView)downloadFragment.getActivity().findViewById(R.id.txt_progress_loading);
        if(txtLoading != null){
            txtLoading.setText(String.format(getResources().getString(R.string.progress_loading_text),
                    request.getDownloadRequestType().getLocalizedName(this)));
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


        public static DriversHolderFragment newInstance(DownloadRDSManager.DownloadRequestType fragmentType, boolean setActionBarTitle)
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
    public void onFirstFragmentVisualisation(Fragment sender, DownloadRDSManager.DownloadRequestType requestType) {
        Object result = null;
        DownloadRequestSchema downloadRequestSchema = DownloadRequestSchema.newInstance(requestType,
                ((BaseFragment) sender).getUniqueCustomerCode(),"",true);

        try {
            result = CacheDataManager.get().getValue(downloadRequestSchema);
            PushDataToFragment(sender,DownloadRequestSchema.newInstance(requestType, false),result);
        } catch (RDSEmptyDataException e) {
            e.printStackTrace();
        }


        /*
        switch (requestType)
        {
            case VEHICLE_NOT_TRUSTED:
                result = CacheDataManager.get().getValue(downloadRequestSchema);
                break;
            case CRDS_NOT_TRUSTED:
                result = CacheDataManager.get().getValue(downloadRequestSchema);
                break;
            case CUSTOMERS_LIST:
                //result = CacheDataManager.get().getCustomers(getApplicationContext());
                result = CacheDataManager.get().getValue(downloadRequestSchema);
                break;
            case VEHICLES_OWNED:
                result = CacheDataManager.get().getCustomerVehicles(((BaseFragment) sender).getUniqueCustomerCode());
                break;
            case DRIVERS_OWNED:
                result = CacheDataManager.get().getCustomerDrivers(((BaseFragment) sender).getUniqueCustomerCode());
                break;
            case CRDS_OWNED:
                result = CacheDataManager.get().getValue(downloadRequestSchema);
                break;
            case DRIVERS_NOT_TRUSTED:
                result = CacheDataManager.get().getDriversNotTrusted();
                break;
            case MAIN_MENU:
                break;
            case VEHICLE_DIAGNOSTIC:
                break;
        }
        */

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

    protected DownloadRDSManager.DownloadRequestType getFragmentType(int fragmentLayoutId)
    {
        Fragment fragment = getFragment(fragmentLayoutId);
        if(fragment != null)
            return Enum.valueOf(DownloadRDSManager.DownloadRequestType.class,fragment.getArguments().getString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
        return null;
    }

    protected Fragment getFragment(int fragmentLayoutId)
    {
        return mFragmentManager.findFragmentById(fragmentLayoutId);
    }

}
