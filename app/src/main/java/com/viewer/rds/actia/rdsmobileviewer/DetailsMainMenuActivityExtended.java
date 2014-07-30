package com.viewer.rds.actia.rdsmobileviewer;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.fragments.BaseFragment;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import java.util.Locale;

/**
 * Created by igaglioti on 23/07/2014.
 */
public class DetailsMainMenuActivityExtended extends BaseActivity implements ActionBar.TabListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityDetailsLayout();
    }

    private void setActivityDetailsLayout() {
        setContentView(R.layout.activity_details_extended);
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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        int i = tab.getPosition();

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        int i = tab.getPosition();

    }
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
            if (mCurrentTabFragment != object) {
                mCurrentTabFragment = (Fragment) object;
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
    public void hideProgressDialog() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        findViewById(R.id.pager).setVisibility(View.VISIBLE);
        View view = findViewById(R.id.fragment_main_details);
        if(view != null)
            view.setVisibility(View.VISIBLE);
    }
    @Override
    public void showProgressDialog(String text) {
        ((TextView)findViewById(R.id.txt_progress_loading)).setText(String.format(getString(R.string.progress_loading_text),text));
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        findViewById(R.id.pager).setVisibility(View.GONE);
        View view = findViewById(R.id.fragment_main_details);
        if(view != null)
            view.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
     //   DownloadUtility.getInstance().stopRDSService(this);
    }

    @Override
    public void onStart() {
       super.onStart();
   //    DownloadUtility.getInstance().startRDService(this);

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
                //launchDownloadRequest(false);
                //requireDataDownload(makeDownloadRequestSchema(false));
                DownloadUtility.DownloadRequestType fragmentType =
                        DownloadUtility.DownloadRequestType.valueOf(mCurrentTabFragment.getArguments().
                        getString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
                makeDownloadRequest((BaseFragment) mCurrentTabFragment, fragmentType,false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private DownloadRequestSchema makeDownloadRequestSchema(boolean cacheIfExist) {

        DownloadRequestSchema requestType = DownloadRequestSchema.newInstance();
        DownloadUtility.DownloadRequestType fragmentType = DownloadUtility.DownloadRequestType.valueOf(mCurrentTabFragment.getArguments().getString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
        requestType.setDownloadRequestType(fragmentType);
        requestType.setCacheOption(cacheIfExist);
        return requestType;
    }

   @Override
    public void handleDownloadDataFinished(DownloadRequestSchema requestType, ResultOperation result)
    {
        if(result.isStatus()) {
            PushDataToFragment(mCurrentTabFragment, requestType, result);
        }
    }


}
