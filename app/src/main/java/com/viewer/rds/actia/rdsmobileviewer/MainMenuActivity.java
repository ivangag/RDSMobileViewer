package com.viewer.rds.actia.rdsmobileviewer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.viewer.rds.actia.rdsmobileviewer.db.RDSDBMapper;
import com.viewer.rds.actia.rdsmobileviewer.fragments.BaseFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CRDSCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.CustomersCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.DownloadHandlingFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.DriversCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.MainMenuCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.fragments.VehiclesCardsFragment;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import java.util.List;

/**
 * Created by igaglioti on 23/07/2014.
 */
public class MainMenuActivity extends BaseActivity
        implements MainMenuCardsFragment.OnMainMenuInteractionListener, DownloadHandlingFragment.TaskDownloadCallbacks, FragmentManager.OnBackStackChangedListener {

    private static MainMenuCardsFragment mMenuCardsFragment;
    private static CustomersCardsFragment mCustomerListFragment;
    private static VehiclesCardsFragment mVehiclesCustomerListFragment;
    private static CRDSCardsFragment mCRDCustomerCardsFragment;
    private static DriversCardsFragment mDriversCustomerListFragment;

    private DownloadManager.DownloadRequestType mCurrentPrimaryFragmentType;
    private DownloadManager.DownloadRequestType mCurrentSecondaryFragmentType = null;
    private RDSDBMapper mRDSDBMapper;
    private static boolean isFragmentsInit = false;
    private static String mLastAddedFragment = "";
    private static int mTrackBackStackCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isFragmentsInit)
        {
            mMenuCardsFragment = MainMenuCardsFragment.newInstance(DownloadManager.DownloadRequestType.MAIN_MENU, true);
            mCustomerListFragment = CustomersCardsFragment.newInstance(DownloadManager.DownloadRequestType.CUSTOMERS_LIST, true);
            mVehiclesCustomerListFragment = VehiclesCardsFragment.newInstance(DownloadManager.DownloadRequestType.VEHICLES_OWNED, true);
            mCRDCustomerCardsFragment = CRDSCardsFragment.newInstance(DownloadManager.DownloadRequestType.CRDS_OWNED, true);
            mDriversCustomerListFragment = DriversHolderFragment.newInstance(DownloadManager.DownloadRequestType.DRIVERS_OWNED, true);
            isFragmentsInit = true;
        }
        setActivityCustomersLayout(savedInstanceState);
        mRDSDBMapper = RDSDBMapper.getInstance(this);

        mFragmentManager.addOnBackStackChangedListener(this);
    }

    private void setActivityCustomersLayout(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main_menu);
        if (savedInstanceState == null) {

            FragmentTransaction ft = mFragmentManager.beginTransaction();
            mCurrentPrimaryFragmentType = DownloadManager.DownloadRequestType.MAIN_MENU;

            ft.add(R.id.fragment_main, mMenuCardsFragment, FRAGMENT_MAIN_MENU_TAG)
                    .add(R.id.fragment_handling_download, DownloadHandlingFragment.newIstance(null), FRAGMENT_DOWNLOAD_TAG)
                    .commit();

            mFragmentManager.executePendingTransactions();

            hideDownloadProgressDialog();
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
            if(mCurrentPrimaryFragmentType == DownloadManager.DownloadRequestType.MAIN_MENU)
            {

            }
            else if(mCurrentPrimaryFragmentType != DownloadManager.DownloadRequestType.CUSTOMERS_LIST)
            {
                // add customers on the place of extra info and put extra to secondary fragment place
                final Fragment fragmentPrimary = getFragment(R.id.fragment_main);
                removeFragment(fragmentPrimary,false);
                replaceFragment(R.id.fragment_main, fragmentPrimary, mCustomerListFragment, true);
                addFragment(R.id.fragment_main_details, fragmentPrimary);
            }
        }
        else
        {
            //int layoutId =  getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getId();
            if((getFragmentManager().findFragmentById(R.id.fragment_main_details) != null))
                getFragmentManager().popBackStackImmediate();

            removeExtraInfoFragment();
        }
        hideDownloadProgressDialog();
    }


    private void removeFragment(Fragment fragment,boolean popStack) {

        if(popStack)
            getFragmentManager().popBackStackImmediate();
            //mFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(executePopBackStack)
            getFragmentManager().popBackStackImmediate();
            //getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if(oldFragment != null) {
            fragmentTransaction.remove(oldFragment).commit();
            getFragmentManager().executePendingTransactions();
        }
        fragmentTransaction = getFragmentManager().beginTransaction();
        if(executePopBackStack) {
            fragmentTransaction.replace(fragmentLayoutId, newFragment).commit();
        }
        else
        {
            fragmentTransaction.replace(fragmentLayoutId, newFragment)
                            .addToBackStack(String.valueOf(fragmentLayoutId)).commit();
        }
        getFragmentManager().executePendingTransactions();
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

        LinearLayout frameLayout = (LinearLayout) findViewById(R.id.fragment_main_details_container);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,MATCH_PARENT);
        frameLayout.setLayoutParams(lp);

        LinearLayout frameLayoutMain = (LinearLayout) findViewById(R.id.fragment_main_container);
        LinearLayout.LayoutParams lpMain = new LinearLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT,1f);
        frameLayoutMain.setLayoutParams(lpMain);

    }

    private void showExtraLayout() {

        LinearLayout frameLayout = (LinearLayout) findViewById(R.id.fragment_main_details_container);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT,1f);
        frameLayout.setLayoutParams(lp);

        LinearLayout frameLayoutMain = (LinearLayout) findViewById(R.id.fragment_main_container);
        LinearLayout.LayoutParams lpMain = new LinearLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT,1f);
        frameLayoutMain.setLayoutParams(lpMain);
    }

    @Override
    public void onStart() {
        super.onStart();
        mRDSDBMapper.open();
        DownloadManager.getInstance().addListener(this);
        //DownloadUtility.getInstance().bindRDService(this);
        //mRDSDBMapper.deleteDatabase();
       // mRDSDBMapper.deleteTable(); //!!only for test!!

    }


    @Override
    public void onStop() {
        super.onStop();
        DownloadManager.getInstance().removeListener(this);
        //DownloadUtility.getInstance().unbindRDSService(this);
    }

    @Override
    protected void onDestroy() {
        mRDSDBMapper.close();
        super.onDestroy();
    }

    @Override
    public void handleDownloadDataFinished(DownloadRequestSchema requestType, ResultOperation result) {

        if(result.isStatus()) {
            switch (requestType.getDownloadRequestType()) {
                case VEHICLE_NOT_TRUSTED:
                    break;
                case CRDS_NOT_TRUSTED:
                    break;
                case DRIVERS_NOT_TRUSTED:
                    break;
                case CUSTOMERS_LIST:
                    if (mDisplayOrientation.equals(PORTRAIT) // this should never happen
                            && !getFragmentType(R.id.fragment_main).equals(DownloadManager.DownloadRequestType.CUSTOMERS_LIST))
                        replaceFragment(R.id.fragment_main, null, mCustomerListFragment, false);

                    //PushDataToFragment(mCustomerListFragment, requestType, result.getClassReturn());
                    mCustomerListFragment.OnUpdateData("", result.getClassReturn(), MainContractorData.class);
                    for (MainContractorData customer : (List<MainContractorData>) result.getClassReturn()) {
                        mRDSDBMapper.insertOrUpdateCustomerData(customer);
                    }
                    break;
                case VEHICLES_OWNED:
                    handleDetailsFragmentViewOnUpdateData(mVehiclesCustomerListFragment);
                    mVehiclesCustomerListFragment.OnUpdateData(requestType.getUniqueCustomerCode(), result.getClassReturn(), VehicleCustom.class);
                    for (VehicleCustom vehicleCustom : (List<VehicleCustom>) result.getClassReturn()) {
                        mRDSDBMapper.insertOrUpdateVehicleData(vehicleCustom, requestType.getUniqueCustomerCode(), true);
                    }
                    break;
                case DRIVERS_OWNED:
                    handleDetailsFragmentViewOnUpdateData(mDriversCustomerListFragment);
                    mDriversCustomerListFragment.OnUpdateData(requestType.getUniqueCustomerCode(), result.getClassReturn(), DriverCardData.class);
                    break;
                case CRDS_OWNED:
                    handleDetailsFragmentViewOnUpdateData(mCRDCustomerCardsFragment);
                    mCRDCustomerCardsFragment.OnUpdateData(requestType.getUniqueCustomerCode(), result.getClassReturn(), CRDSCustom.class);
                    break;
                case MAIN_MENU:
                    break;
                case VEHICLE_DIAGNOSTIC:
                    showTextFile(((List<VehicleCustom>) result.getClassReturn()).get(0).getFileContent());
                    break;
            }

            if(mDisplayOrientation.equals(LANDSCAPE))
                showExtraLayout();
        }
    }

    public void handleDetailsFragmentViewOnUpdateData(BaseFragment fragment) {
        if (getFragmentType(R.id.fragment_main).equals(DownloadManager.DownloadRequestType.CUSTOMERS_LIST)) {
            int newLayoutId = R.id.fragment_main;
            if (mDisplayOrientation.equals(LANDSCAPE))
                newLayoutId = R.id.fragment_main_details;

            if(((fragment.getId() != 0)
                && fragment.getId() != newLayoutId)) {
            }

            replaceFragment(newLayoutId, null, fragment, false);

        }
    }

    @Override
    public void hideDownloadProgressDialog() {

        boolean showInfo = false;
        final Fragment downloadFragment = getFragmentManager().findFragmentByTag(FRAGMENT_DOWNLOAD_TAG);
        if((null != downloadFragment)
            && (downloadFragment instanceof DownloadHandlingFragment))
        {
            showInfo = (((DownloadHandlingFragment)(downloadFragment)).getDownloadRequestCountPending() > 0);
        }
        if(!showInfo) {
            if (mDisplayOrientation.equals(LANDSCAPE)) {

                LinearLayout frameLayout = (LinearLayout) findViewById(R.id.fragment_handling_download_container);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
                frameLayout.setLayoutParams(lp);
            } else {
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_handling_download);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, 0, 0f);
                frameLayout.setLayoutParams(lp);
            }
        }
    }

    @Override
    public void showProgressDialog(String text) {

        if(mDisplayOrientation.equals(LANDSCAPE)){

            LinearLayout frameLayout = (LinearLayout) findViewById(R.id.fragment_handling_download_container);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT);
            frameLayout.setLayoutParams(lp);
        }
        else{
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_handling_download);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT,0,0.5f);
            frameLayout.setLayoutParams(lp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //item.getMenuInfo()
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_download_data:
                if(!(getFragmentType(R.id.fragment_main).equals(DownloadManager.DownloadRequestType.MAIN_MENU))) {
                    DownloadRequestSchema downloadRequestSchema = DownloadRequestSchema.newInstance
                            (getFragmentType(R.id.fragment_main),((BaseFragment) getFragment(R.id.fragment_main)).getUniqueCustomerCode(),"",false);
                    makeDownloadRequest(downloadRequestSchema);
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
                //showExtraLayout();
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

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {

    }

    @Override
    public void onBackStackChanged() {
        if(mDisplayOrientation.equals(LANDSCAPE)
                && (getFragmentManager().findFragmentById(R.id.fragment_main_details) == null)){
            hideExtraLayout();
        }
        mTrackBackStackCount = getFragmentManager().getBackStackEntryCount();
    }
}
