package com.viewer.rds.actia.rdsmobileviewer;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.viewer.rds.actia.rdsmobileviewer.utils.CacheDataManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;
import com.viewer.rds.actia.rdsmobileviewer.fragments.IFragmentNotification;
import com.viewer.rds.actia.rdsmobileviewer.fragments.VehiclesCardsFragment;


public class DetailsMainMenuActivity extends Activity implements ActionBar.TabListener,
        DownloadUtility.IRemoteDownloadDataListener,
        VehiclesCardsFragment.IFragmentsInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    Fragment mCurrentFragment;

    DownloadUtility mDownloadUtility;

    final private static String FRAGMENT_DRIVERS_TAG    = "DRIVERS_FRAGMENT";
    final private static String FRAGMENT_VEHICLES_TAG   = "VEHICLES_FRAGMENT";
    final private static String FRAGMENT_CRDS_TAG       = "CRDS_FRAGMENT";
    final private static String FRAGMENT_MAIN_MENU_TAG  = "MAIN_MENU_FRAGMENT";
    final private static String FRAGMENT_CUSTOMERS_TAG  = "CUSTOMERS_FRAGMENT";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Utils.Init(this);

        mDownloadUtility = DownloadUtility.getInstance();
        setActivityDetailsLayout();


    }

    private void setActivityDetailsLayout() {
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_menu, menu);
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
    private IFragmentNotification getCurrentDisplayedFragment()
    {
        return mCurrentFragment != null ? (IFragmentNotification)mCurrentFragment: null;
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
                launchDownloadRequest(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchDownloadRequest(boolean cacheIfExist) {
        DownloadRequestSchema requestType = getDownloadRequestSchema(cacheIfExist);
        mDownloadUtility.RequireDownloadAsyncTask(this, requestType);
        Toast.makeText(this, String.valueOf(requestType.getDownloadRequestType()) + " Fetching...", Toast.LENGTH_SHORT).show();
    }

    private DownloadRequestSchema getDownloadRequestSchema(boolean cacheIfExist) {
        DownloadRequestSchema requestType = DownloadRequestSchema.newInstance();
        DownloadUtility.DownloadRequestType fragmentType = DownloadUtility.DownloadRequestType.valueOf(mCurrentFragment.getArguments().getString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
        requestType.setDownloadRequestType(fragmentType);
        requestType.setCacheOption(cacheIfExist);
        return requestType;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onDownloadDataFinished(DownloadRequestSchema requestType, Object result) {
        PushDataToFragment(mCurrentFragment, requestType, result);
    }

    private void PushDataToFragment(Fragment fragment, DownloadRequestSchema requestType, Object result) {
        if(fragment.isVisible()) {
            switch (requestType.getDownloadRequestType()) {
                case VEHICLE_NOT_TRUSTED:
                    ((IFragmentNotification)fragment).OnUpdateData(requestType.getUniqueCustomerCode(), result, VehicleCustom.class);
                    break;
                case CRDS_NOT_TRUSTED:
                    ((IFragmentNotification)fragment).OnUpdateData(requestType.getUniqueCustomerCode(), result, CRDSCustom.class);
                    break;
                case DRIVERS_NOT_TRUSTED:
                    ((IFragmentNotification)fragment).OnUpdateData(requestType.getUniqueCustomerCode(), result, DriverCardData.class);
                    break;
            }
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
            case DRIVERS_NOT_TRUSTED:
                result = CacheDataManager.getInstance().getDriversNotTrusted();
                break;
        }
        PushDataToFragment(sender,DownloadRequestSchema.newInstance(requestType, false),result);
    }

    @Override
    public void onRequireVehicleDiagnosticData(String vehicleVIN, boolean cacheIfExist) {

    }

    @Override
    public void onCustomerSelected(String CustomerAncodice) {

    }

    @Override
    public void onCustomerVehiclesDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
        launchDownloadRequest(cacheIfExist);
    }

    @Override
    public void onCustomerDrivesDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {
        launchDownloadRequest(cacheIfExist);
    }

    @Override
    public void onCustomerCRDSDataRequiredSelected(DownloadUtility.DownloadRequestType downloadRequestType,String CustomerAncodice,boolean cacheIfExist) {

        launchDownloadRequest(cacheIfExist);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a Fragment Cards Style
            Fragment fragment = PlaceholderFragmentFactory.newInstance(position + 1);

            return fragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mCurrentFragment != object) {
                mCurrentFragment = (Fragment) object;
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return Utils.MAX_TABS_ITEMS_NOT_TRUSTED_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence title = null;
            Locale l = Locale.getDefault();
            switch (position) {
                case Utils.TAB_POSITION_VEHICLES:
                    title = Utils.TITLE_VEHICLES_NOT_TRUSTED;
                    break;
                case Utils.TAB_POSITION_CRDS:
                    title = Utils.TITLE_CRDS_NOT_TRUSTED;
                    break;
                case Utils.TAB_POSITION_DRIVERS:
                    title = Utils.TITLE_DRIVERS_NOT_TRUSTED;
                    break;

            }
            return title;
        }
    }
    @Override
    public void onStop() {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onStop();

    }

}

