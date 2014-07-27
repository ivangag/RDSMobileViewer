package com.viewer.rds.actia.rdsmobileviewer;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Debug;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.db.RDSDBMapper;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CustomersCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.MainMenuCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.utils.CacheDataManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import java.util.List;

/**
 * Created by igaglioti on 23/07/2014.
 */
public class MainMenuActivityExtended extends BaseActivity implements MainMenuCardsFragment.OnMainMenuInteractionListener {

    private DownloadUtility.DownloadRequestType mCurrentPrimaryFragmentType;
    private DownloadUtility.DownloadRequestType mCurrentSecondaryFragmentType = null;
    private RDSDBMapper mRDSDBMapper;


    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityCustomersLayout(savedInstanceState);
        mRDSDBMapper = RDSDBMapper.getInstance(this);
        mRDSDBMapper.open();
        //mRDSDBMapper.deleteAllCustomers();

    }

    private void setActivityCustomersLayout(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main_menu);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        mDisplayOrientation = getResources().getConfiguration().orientation;

        if (savedInstanceState == null) {

            mCurrentPrimaryFragmentType = DownloadUtility.DownloadRequestType.MAIN_MENU;
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_main, mMenuCardsFragment,FRAGMENT_MAIN_MENU_TAG)
                            //.addToBackStack(null)
                    .commit();
            hideExtraLayout();
        }
        else
        {
            handleOrientation(mDisplayOrientation);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //setActivityCustomersLayout(new Bundle());

        mDisplayOrientation = newConfig.orientation;
        handleOrientation(mDisplayOrientation);
    }

    protected void handleOrientation(Integer newOrientation) {
        mCurrentPrimaryFragmentType = getFragmentType(R.id.fragment_main);
        mCurrentSecondaryFragmentType = getFragmentType(R.id.fragment_main_details);
        if(newOrientation.equals(LANDSCAPE))
        {
            if(mCurrentPrimaryFragmentType == DownloadUtility.DownloadRequestType.MAIN_MENU)
            {

            }
            else if(mCurrentPrimaryFragmentType != DownloadUtility.DownloadRequestType.CUSTOMERS_LIST)
            {
                // add customers on the place of extra info and put extra to secondary fragment place
                removeFragment(getFragment(mCurrentPrimaryFragmentType));
                replaceFragment(R.id.fragment_main, getFragment(mCurrentPrimaryFragmentType), mCustomerListFragment, true);
                addFragment(R.id.fragment_main_details, getFragment(mCurrentPrimaryFragmentType));
            }
        }
        else
        {
            removeExtraInfoFragment();
        }
    }


    private void removeFragment(Fragment fragment) {

        mFragmentManager.popBackStackImmediate(null, mFragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();
        fragmentTransaction.
                remove(fragment).commit();
        mFragmentManager.executePendingTransactions();

    }

    private void addFragment(int fragmentLayoutId, Fragment newFragment) {

        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();
        fragmentTransaction
                .add(fragmentLayoutId, newFragment)
                .addToBackStack(null)
                .commit();
        mFragmentManager.executePendingTransactions();
    }

    private void replaceFragment(int fragmentLayoutId,Fragment oldFragment, Fragment newFragment, boolean executePopBackStack) {


        if(executePopBackStack)
            mFragmentManager.popBackStackImmediate(null, mFragmentManager.POP_BACK_STACK_INCLUSIVE);
        if(oldFragment != null) {
            mFragmentManager.beginTransaction().remove(oldFragment).commit();
            mFragmentManager.executePendingTransactions();
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if(executePopBackStack) {
            fragmentTransaction.replace(fragmentLayoutId, newFragment).commit();
        }
        else
        {
            fragmentTransaction.replace(fragmentLayoutId, newFragment)
                            .addToBackStack(null).commit();
        }
        mFragmentManager.executePendingTransactions();
    }

    private void removeExtraInfoFragment() {

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


    private void hideExtraLayout() {
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

    private void showExtraLayout() {

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

    @Override
    public void onStop() {
        mRDSDBMapper.close();
        super.onStop();
    }

    @Override
    public void handleDownloadDataFinished(DownloadRequestSchema requestType, Object result) {

        switch (requestType.getDownloadRequestType())
        {
            case VEHICLE_NOT_TRUSTED:
                break;
            case CRDS_NOT_TRUSTED:
                break;
            case DRIVERS_NOT_TRUSTED:
                break;
            case CUSTOMERS_LIST:
                //showNewFragment(mCustomerListFragment,FRAGMENT_CUSTOMERS_TAG,false);
                if(mDisplayOrientation.equals(PORTRAIT) // this should never happen
                        && !getFragmentType(R.id.fragment_main).equals(DownloadUtility.DownloadRequestType.CUSTOMERS_LIST))
                    replaceFragment(R.id.fragment_main, null, mCustomerListFragment, false);
                PushDataToFragment(mCustomerListFragment,requestType,result);
                mCustomerListFragment.OnUpdateData("",result,MainContractorData.class);
                for(MainContractorData customer : (List<MainContractorData>)result)
                {
                    mRDSDBMapper.insertCustomerData(customer);
                }
                break;
            case VEHICLES_OWNED:
                //showNewFragment(mVehiclesCustomerListFragment,FRAGMENT_VEHICLES_TAG,true);
                if(mDisplayOrientation.equals(LANDSCAPE))
                    replaceFragment(R.id.fragment_main_details,null,mVehiclesCustomerListFragment,false);
                else
                    replaceFragment(R.id.fragment_main, null, mVehiclesCustomerListFragment, false);
                mVehiclesCustomerListFragment.OnUpdateData(requestType.getUniqueCustomerCode(), result,VehicleCustom.class);
                break;
            case DRIVERS_OWNED:
                if(mDisplayOrientation.equals(LANDSCAPE))
                    replaceFragment(R.id.fragment_main_details,null,mDriversCustomerListFragment,false);
                else
                    replaceFragment(R.id.fragment_main, null, mDriversCustomerListFragment, false);
                //showNewFragment(mDriversCustomerListFragment,FRAGMENT_DRIVERS_TAG,true);
                mDriversCustomerListFragment.OnUpdateData(requestType.getUniqueCustomerCode(), result, DriverCardData.class);
                break;
            case CRDS_OWNED:
                //showNewFragment(mCRDCustomerCardsFragment,FRAGMENT_CRDS_TAG,true);
                if(mDisplayOrientation.equals(LANDSCAPE))
                    replaceFragment(R.id.fragment_main_details,null,mCRDCustomerCardsFragment,false);
                else
                    replaceFragment(R.id.fragment_main, null, mCRDCustomerCardsFragment, false);
                mCRDCustomerCardsFragment.OnUpdateData(requestType.getUniqueCustomerCode(),result,CRDSCustom.class);
                break;
            case MAIN_MENU:
                break;
            case VEHICLE_DIAGNOSTIC:
                showTextFile(((List<VehicleCustom>)result).get(0).get_FileContent());
                break;
        }
    }

    @Override
    public void hideProgressDialog() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        findViewById(R.id.fragment_main).setVisibility(View.VISIBLE);
        View view = findViewById(R.id.fragment_main_details);
        if(view != null)
            view.setVisibility(View.VISIBLE);
    }
    @Override
    public void showProgressDialog(String text) {
        ((TextView)findViewById(R.id.txt_progress_loading)).setText(String.format(getString(R.string.progress_loading_text),text));
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_main).setVisibility(View.GONE);
        View view = findViewById(R.id.fragment_main_details);
        if(view != null)
            view.setVisibility(View.GONE);
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
                makeDownloadRequest( getFragmentType(R.id.fragment_main),false);
                /*
                if (mCustomerListFragment.isVisible()) {
                    requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.CUSTOMERS_LIST, false));
                }
                else if (mVehiclesCustomerListFragment.isVisible()) {
                    requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.VEHICLES_OWNED, mVehiclesCustomerListFragment.getUniqueCustomerCode(), "", false));
                }
                else if (mDriversCustomerListFragment.isVisible()) {
                    requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.DRIVERS_OWNED, mDriversCustomerListFragment.getUniqueCustomerCode(), "", false));
                }
                else if (mCRDCustomerCardsFragment.isVisible()) {
                    requireDataDownload(DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.CRDS_OWNED, mCRDCustomerCardsFragment.getUniqueCustomerCode(), "", false));
                }
                */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onMenuSectionSelected(Utils.MainMenuSectionItemType subMenuSelected) {
        switch (subMenuSelected)
        {
            case CUSTOMERS:
                showExtraLayout();
                showNewFragment(mCustomerListFragment,FRAGMENT_CUSTOMERS_TAG);
                break;
            case ITEMS_NOT_TRUSTED:
                showItemNotTrustedSection(subMenuSelected);
        }

    }

    private void showItemNotTrustedSection(Utils.MainMenuSectionItemType requestType) {

        Intent launchDetailsIntent = new Intent(MainMenuActivityExtended.this,DetailsMainMenuActivityExtended.class);
        Bundle data = new Bundle();
        data.putString(Utils.MAIN_MENU_KEY, String.valueOf(requestType));
        launchDetailsIntent.putExtras(data);
        startActivity(launchDetailsIntent);
    }

    @Override
    public void onBackPressed() {

        boolean callBaseBackPressed = true;
        /*
        if(mDisplayOrientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            //when it's time return to main menu
            DownloadUtility.DownloadRequestType fragmentPrimaryType = getFragmentType(R.id.fragment_main);
            final int bsCount = mFragmentManager.getBackStackEntryCount();
            if((bsCount == 0)
                && !fragmentPrimaryType.equals(DownloadUtility.DownloadRequestType.MAIN_MENU))
            {
                replaceFragment(R.id.fragment_main, mFragmentManager.findFragmentById(R.id.fragment_main), getFragment(DownloadUtility.DownloadRequestType.MAIN_MENU), true);
                callBaseBackPressed = false;
            }
            if(!fragmentPrimaryType.equals(DownloadUtility.DownloadRequestType.MAIN_MENU))
            {
                hideExtraLayout();
            }
        }
        */
        if(callBaseBackPressed)
            super.onBackPressed();
    }

}
