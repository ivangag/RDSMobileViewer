package com.viewer.rds.actia.rdsmobileviewer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;
import com.viewer.rds.actia.rdsmobileviewer.fragments.BaseFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CRDSCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CustomersCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.DriversCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.IFragmentNotification;
import com.viewer.rds.actia.rdsmobileviewer.fragments.MainMenuCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.VehiclesCardsFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainMenuActivity extends Activity implements
        MainMenuCardsFragment.OnMainMenuInteractionListener,
        DownloadUtility.IRemoteDownloadDataListener,BaseFragment.IFragmentsInteractionListener {

    public final MainMenuCardsFragment mMenuCardsFragment = MainMenuCardsFragment.newInstance(DownloadUtility.DownloadRequestType.MAIN_MENU, true);
    public final CustomersCardsFragment mCustomerListFragment = CustomersCardsFragment.newInstance(DownloadUtility.DownloadRequestType.CUSTOMERS_LIST, true);
    public final VehiclesCardsFragment mVehiclesCustomerListFragment = VehiclesCardsFragment.newInstance(DownloadUtility.DownloadRequestType.VEHICLES_OWNED, true);
    public final CRDSCardsFragment mCRDCustomerCardsFragment = CRDSCardsFragment.newInstance(DownloadUtility.DownloadRequestType.CRDS_OWNED, true);
    public final DriversCardsFragment mDriversCustomerListFragment = DriversCardsFragment.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_OWNED,true);
    public final DriversHolderFragment mHolderDriversCustomerListFragment = DriversHolderFragment.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_OWNED, true);
    public final CRDSCardsFragment mHolderCRDCustomerCardsFragment = CRDSHolderFragment.newInstance();
    public final VehiclesCardsFragment mHolderVehiclesCustomerListFragment = VehiclesHolderFragment.newInstance();

    private static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int LANDSCAPE = Configuration.ORIENTATION_LANDSCAPE;
    private static final int PORTRAIT = Configuration.ORIENTATION_PORTRAIT;
    private FragmentManager mFragmentManager;
    private final static DownloadUtility mDownloadUtility = DownloadUtility.getInstance();
    private ShareActionProvider mShareActionProvider;
    private boolean mIsShareIntentPending;

    final private static String FRAGMENT_DRIVERS_TAG    = "DRIVERS_FRAGMENT";
    final private static String FRAGMENT_VEHICLES_TAG   = "VEHICLES_FRAGMENT";
    final private static String FRAGMENT_CRDS_TAG       = "CRDS_FRAGMENT";
    final private static String FRAGMENT_MAIN_MENU_TAG  = "MAIN_MENU_FRAGMENT";
    final private static String FRAGMENT_CUSTOMERS_TAG  = "CUSTOMERS_FRAGMENT";

    private ArrayList<String> mFragmentTags;
    Integer mDisplayOrientation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityCustomersLayout(savedInstanceState);

        init();
    }

    private void setActivityCustomersLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main_menu);

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        mFragmentManager = getFragmentManager();
        mDisplayOrientation = getResources().getConfiguration().orientation;


        if (savedInstanceState == null) {

            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_main, mMenuCardsFragment,FRAGMENT_MAIN_MENU_TAG)
                    //.addToBackStack(null)
                    .commit();
            HideDetailsLayout();
        }
        else {
            if(mFragmentManager.findFragmentById(R.id.fragment_main).getTag().equals(FRAGMENT_MAIN_MENU_TAG))
            {
                HideDetailsLayout();
            }
            if(mDisplayOrientation.equals(PORTRAIT))
            {
                removeDetailFragment();
            }
        }
    }

    private void HideDetailsLayout() {
        View frameLayoutDetails =  findViewById(R.id.fragment_main_details);
        View frameLayoutMain =  findViewById(R.id.fragment_main);
        if(frameLayoutDetails != null) {
            frameLayoutDetails.setVisibility(View.GONE);
            frameLayoutDetails.setLayoutParams((new LinearLayout.LayoutParams(0,
                    0, 0f)));
        }
        if(frameLayoutMain != null)
        {
            frameLayoutMain.setLayoutParams((new LinearLayout.LayoutParams(0,
                    MATCH_PARENT, 3f)));
        }
    }

    private void ShowDetailsLayout() {

        View frameLayoutDetails =  findViewById(R.id.fragment_main_details);
        View frameLayoutMain =  findViewById(R.id.fragment_main);
        if(frameLayoutDetails != null) {
            frameLayoutDetails.setVisibility(View.GONE);
            frameLayoutDetails.setLayoutParams((new LinearLayout.LayoutParams(0,
                    MATCH_PARENT, 2f)));
        }
        if(frameLayoutMain != null)
        {
            frameLayoutMain.setLayoutParams((new LinearLayout.LayoutParams(0,
                    MATCH_PARENT, 1f)));
        }
    }

    private void init()
    {
        mFragmentTags = new ArrayList<String>();
        mFragmentTags.add(FRAGMENT_CRDS_TAG);
        mFragmentTags.add(FRAGMENT_DRIVERS_TAG);
        mFragmentTags.add(FRAGMENT_VEHICLES_TAG);
        mFragmentTags.add(FRAGMENT_CUSTOMERS_TAG);
        mFragmentTags.add(FRAGMENT_MAIN_MENU_TAG);
    }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_download_data:
                if (mCustomerListFragment.isVisible()) {
                    requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.CUSTOMERS_LIST, false));
                }
                else if (mVehiclesCustomerListFragment.isVisible()) {
                    requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.VEHICLES_OWNED, mVehiclesCustomerListFragment.getUniqueCustomerCode(), "", false));
                }
                else if (mHolderDriversCustomerListFragment.isVisible()) {
                    requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_OWNED, mHolderDriversCustomerListFragment.getUniqueCustomerCode(), "", false));
                }
                else if (mCRDCustomerCardsFragment.isVisible()) {
                    requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.CRDS_OWNED, mCRDCustomerCardsFragment.getUniqueCustomerCode(), "", false));
                 }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuSectionSelected(Utils.MainMenuSectionItemType subMenuSelected) {
        switch (subMenuSelected)
        {
            case CUSTOMERS:
                ShowDetailsLayout();
                showNewFragment(mCustomerListFragment,FRAGMENT_CUSTOMERS_TAG);
                break;
            case ITEMS_NOT_TRUSTED:
                showItemNotTrustedSection(subMenuSelected);
        }

    }

    private void showItemNotTrustedSection(Utils.MainMenuSectionItemType requestType) {

        Intent launchDetailsIntent = new Intent(MainMenuActivity.this,DetailsMainMenuActivity.class);
        Bundle data = new Bundle();
        data.putString(Utils.MAIN_MENU_KEY, String.valueOf(requestType));
        startActivity(launchDetailsIntent);

    }

    @Override
    public void onDownloadDataFinished(DownloadRequestSchema requestType, Object result) {

        hideProgressDialog();
        switch (requestType.getDownloadRequestType())
        {
            case VEHICLE_NOT_TRUSTED:
                break;
            case CRDS_NOT_TRUSTED:
                break;
            case DRIVERS_NOT_TRUSTED:
                break;
            case CUSTOMERS_LIST:
                showNewFragment(mCustomerListFragment,FRAGMENT_CUSTOMERS_TAG,false);
                //showNewFragment(mCustomerListFragment,FRAGMENT_CUSTOMERS_TAG);
                mCustomerListFragment.OnUpdateData("",result,MainContractorData.class);
                break;
            case VEHICLES_OWNED:
                showNewFragment(mVehiclesCustomerListFragment,FRAGMENT_VEHICLES_TAG,true);
                mVehiclesCustomerListFragment.OnUpdateData(requestType.getUniqueCustomerCode(), result,VehicleCustom.class);
                break;
            case DRIVERS_OWNED:
                showNewFragment(mHolderDriversCustomerListFragment,FRAGMENT_DRIVERS_TAG);
                mHolderDriversCustomerListFragment.OnUpdateData(requestType.getUniqueCustomerCode(),result,DriverCardData.class);
                break;
            case CRDS_OWNED:
                showNewFragment(mCRDCustomerCardsFragment,FRAGMENT_CRDS_TAG);
                mCRDCustomerCardsFragment.OnUpdateData(requestType.getUniqueCustomerCode(),result,CRDSCustom.class);
                break;
            case MAIN_MENU:
                break;
            case VEHICLE_DIAGNOSTIC:
                showTextFile(((List<VehicleCustom>)result).get(0).get_FileContent());
                break;
        }
    }

    private boolean isProgressDialogVisible(){
        return findViewById(R.id.loadingPanel).getVisibility() != View.GONE;
    }


    private void hideProgressDialog() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        findViewById(R.id.fragment_main).setVisibility(View.VISIBLE);
        View view = findViewById(R.id.fragment_main_details);
        if(view != null)
                view.setVisibility(View.VISIBLE);
    }
    private void showProgressDialog(String text) {
        ((TextView)findViewById(R.id.txt_progress_loading)).setText(String.format(getString(R.string.progress_loading_text),text));
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_main).setVisibility(View.GONE);
        View view = findViewById(R.id.fragment_main_details);
        if(view != null)
            view.setVisibility(View.GONE);
    }

    private void showTextFile(String text)
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

    private void showNewFragment(Fragment fragment, String tag) {
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
    private void removeDetailFragment() {

        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();
        Fragment detailsFragment = mFragmentManager.findFragmentById(R.id.fragment_main_details);
        if (detailsFragment != null) {
            fragmentTransaction.remove(detailsFragment);
//            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            mFragmentManager.executePendingTransactions();
        }
    }


    private void showNewFragment(Fragment fragment, String tag, boolean isDetailsFragment ) {
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
        /*
        if(isProgressDialogVisible())
            hideProgressDialog();
        else
            super.onBackPressed();
            */
        if(!mFragmentManager.findFragmentById(R.id.fragment_main).getTag().equals(FRAGMENT_MAIN_MENU_TAG))
        {
            HideDetailsLayout();
        }
        if(!isProgressDialogVisible())
            super.onBackPressed();



    }

    @Override
    public void onCustomerSelected(String CustomerAncodice) {

    }

    @Override
    public void onCustomerVehiclesDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
            requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.VEHICLES_OWNED, CustomerAncodice, "", cacheIfExist));
    }

    @Override
    public void onCustomerDrivesDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
            requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_OWNED, CustomerAncodice, "", cacheIfExist));
    }

    @Override
    public void onCustomerCRDSDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
            requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.CRDS_OWNED, CustomerAncodice, "", cacheIfExist));
    }


    private void requireDataDownload(DownloadRequestSchema dataRequest)
    {
        showProgressDialog(dataRequest.getDownloadRequestType().getLocalizedName(this) + " "
                + (dataRequest.getUniqueCustomerCode()  )
        );
        //Toast.makeText(this, "Fetching " + dataRequest.getDownloadRequestType().getLocalizedName(this), Toast.LENGTH_SHORT).show();
        DownloadUtility.getInstance().RequireDownloadAsyncTask(this, dataRequest);
    }

    @Override
    public void onFirstFragmentVisualisation(Fragment sender, DownloadUtility.DownloadRequestType requestType) {

    }

    @Override
    public void onRequireVehicleDiagnosticData(String vehicleVIN, boolean cacheIfExist) {
        requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.VEHICLE_DIAGNOSTIC, "", vehicleVIN, cacheIfExist));
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
}
