package com.viewer.rds.actia.rdsmobileviewer.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.viewer.rds.actia.rdsmobileviewer.DriverCardData;
import com.viewer.rds.actia.rdsmobileviewer.PlaceholderFragmentFactory;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.cards.CustomExpandCard;
import com.viewer.rds.actia.rdsmobileviewer.cards.HeaderCard;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by igaglioti on 14/07/2014.
 */
public class DriversCardsFragment extends BaseFragment implements IFragmentNotification, Filterable {
    private String mLastFilter;
    private List<DriverDataCardWrapper> mLastRetrievedItems = new ArrayList<DriverDataCardWrapper>();
    private List<DriverDataCardWrapper> mLastFilteredItems = new ArrayList<DriverDataCardWrapper>();
    CardArrayAdapter mCardArrayAdapter;
    int mTitleResourceId = R.string.drivers_title;


    private IFragmentsInteractionListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            mListener = (IFragmentsInteractionListener) activity;
        } catch (ClassCastException e) {
            //throw new ClassCastException(activity.toString()
            //        + " must implement OnFragmentInteractionListener");
        }
    }

    boolean mIsFirstVisualization = true;
    @Override
    public void onStart()
    {
        super.onStart();

        if(mIsFirstVisualization) {
            mIsFirstVisualization = false;
            DownloadUtility.DownloadRequestType type = DownloadUtility.DownloadRequestType.valueOf((String) getArguments().get(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
            if(mListener != null)
                mListener.onFirstFragmentVisualisation(this, type);
        }
    }



    @Override
    public boolean getIfHastToSetTitle() {
        return getArguments().getBoolean(PlaceholderFragmentFactory.ARG_SET_TITLE_ACTION_BAR);
    }
    private String mUniqueCustomerCode;

    @Override
    public String getUniqueCustomerCode() {
        return mUniqueCustomerCode;
    }
    @Override
    public int getTitleResourceId() {
        return mTitleResourceId;
    }

    @Override
    public String getCustomTitleText() {
        String res = "";
        if((mCardArrayAdapter != null)
                && (!mCardArrayAdapter.isEmpty())) {
            if(mCardArrayAdapter.getCount() > 1)
                res = String.format(getString(R.string.customerDrivers), ((DriverDataCardWrapper) mCardArrayAdapter.getItem(1)).getCustomerName());
        }
        return res;
    }

    @Override
    public void OnUpdateData(String UniqueCustomerCode, Object dataContentList, Class itemBaseType) {

        mUniqueCustomerCode = UniqueCustomerCode;
        List<DriverCardData> data = (List<DriverCardData>) dataContentList;

        if(itemBaseType.equals(DriverCardData.class)) {
            mCardArrayAdapter.clear();
            mCardArrayAdapter.addAll(this.BuildCardBaseData(getActivity(), data));
        }
        setTitle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_drivers, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_base_drivers, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVehicles();
    }


    private void initVehicles() {

        if(mCardArrayAdapter == null) {
            ArrayList<Card> cards = new ArrayList<Card>();

            mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        }
        //CardListView mListView = (CardListView) getActivity().findViewById(R.id.card_list_base_driver);
        CardListView mListView = (CardListView) getActivity().findViewById(R.id.listDriversId);

        if (mListView != null) {
            mListView.setAdapter(mCardArrayAdapter);
        }
    }


    public static DriversCardsFragment newInstance(DownloadUtility.DownloadRequestType fragmentType,boolean setActionBarTitle) {

        DriversCardsFragment fragment = new DriversCardsFragment();
        Bundle args = new Bundle();
        args.putBoolean(PlaceholderFragmentFactory.ARG_SET_TITLE_ACTION_BAR, setActionBarTitle);
        args.putString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE, fragmentType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public class DriverDataCardWrapper extends Card {

        private final DriverCardData mBaseCustomData;
        protected TextView mTextViewVRN;
        protected TextView mTextViewDiagnosticTime;

        protected String mCustomerName;
        protected String mTitleHeader;
        protected String mIdCard;
        protected String mDiagnosticTime;
        protected ImageView mThumbnailMenuType;
        private ImageButton mButtonExpandCustom;

        public DriverDataCardWrapper(Context context, DriverCardData driverCardData) {
            super(context, R.layout.card_rds_item_simple_inner_content);

            this.mTitleHeader = driverCardData.getName();
            this.mIdCard = driverCardData.getIdCard();
            this.mDiagnosticTime =
                    (driverCardData.getInsertingDate() != null ?  driverCardData.getInsertingDate() : "Not Available")
                            + "(" + (driverCardData.getBindingDate() != null ?  driverCardData.getBindingDate() : "Not Available") + ")";
            this.mCustomerName = driverCardData.getCustomerName();

            this.mBaseCustomData = driverCardData;
            init();
        }


        private void init() {

            //Create a CardHeader
            CardHeader header = new CardHeader(getContext());

            //Set the header title
            header.setTitle(mTitleHeader);

            //Add a popup menu. This method set OverFlow button to visible
            header.setPopupMenu(R.menu.rds_drivers_popmenu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.action_getDriverExtraInfo:
                            Toast.makeText(getContext(), "Implementing Retrieving Extra Indo for " + item.getTitle(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                    //Toast.makeText(getContext(), "Click on card menu" + mTitleHeader + " item=" + item.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
            addCardHeader(header);
            //Add ClickListener
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    //Toast.makeText(getContext(), "Click Listener card=" + mTitleHeader, Toast.LENGTH_SHORT).show();
                }
            });
            /*
            //Set the card inner text
            setTitle(mCRDSGuid);
            */
            setSwipeable(true);

            //This provides a simple (and useless) expand area
            CustomExpandCard expand = new CustomExpandCard(getActivity(),this.mBaseCustomData.toString());
            //Add Expand Area to Card
            addCardExpand(expand);

            setBackgroundResourceId(R.drawable.card_background_color_gray);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            //Retrieve elements
            mThumbnailMenuType = (ImageView) parent.findViewById(R.id.rds_menu_item_thumbnail_image);
            mTextViewVRN = (TextView) parent.findViewById(R.id.card_element_primary_info);
            mTextViewDiagnosticTime = (TextView) parent.findViewById(R.id.card_element_secondary_info);
            mButtonExpandCustom = (ImageButton)parent.findViewById(R.id.card_rds_expand_button_info);

            if (mThumbnailMenuType != null)
                mThumbnailMenuType.setBackgroundResource(R.drawable.ic_rds_driver);

            if (mTextViewVRN != null)
                mTextViewVRN.setText(mIdCard);

            if (mTextViewDiagnosticTime != null)
                mTextViewDiagnosticTime.setText(mDiagnosticTime);

            if(mButtonExpandCustom != null) {
                mButtonExpandCustom.setBackgroundResource(R.drawable.card_menu_button_expand);

                mButtonExpandCustom.setClickable(true);

                ViewToClickToExpand extraCustomButtonExpand =
                        ViewToClickToExpand.builder().highlightView(false)
                                .setupView(mButtonExpandCustom);

                setViewToClickToExpand(extraCustomButtonExpand);
            }
        }

        public String getCustomerName()
        {
            return this.mCustomerName;
        }

        public String toRawString() {
            return this.mCustomerName + "\r\n"  + this.mTitleHeader
                    + "\r\n"  + this.mIdCard + "\r\n"  + this.mDiagnosticTime;
        }

        public String getDriverName(){
            return this.mTitleHeader;
        }

        public String getBaseDataToString()
        {
            return this.mBaseCustomData.toString();
        }

    }


    protected   ArrayList<Card> BuildCardBaseData(Context context, List<DriverCardData> vehicleData) {

        ArrayList<Card> cards = new ArrayList<Card>();
        mLastRetrievedItems.clear();
        HeaderCard headerCard = HeaderCard.newInstance(this,
                vehicleData.size(), getResources().getString(R.string.drivers_title));
        cards.add(headerCard);
        for (DriverCardData vehicle : vehicleData)
        {
            DriverDataCardWrapper card = new DriverDataCardWrapper(context,vehicle);
            mLastRetrievedItems.add(card);
            cards.add(card);
        }
        return cards;
    }

    protected ArrayList<Card> BuildCardFromWrapper(List<DriverDataCardWrapper> vehicleData) {

        ArrayList<Card> cards = new ArrayList<Card>();
        HeaderCard headerCard = HeaderCard.newInstance(this,
                vehicleData.size(), getResources().getString(R.string.drivers_title));
        cards.add(headerCard);
        for (DriverDataCardWrapper card : vehicleData)
        {
            cards.add(card);
        }
        return cards;
    }

    @Override
    public void OnFilterData(String filterPattern) {

        this.getFilter().filter(filterPattern);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mLastFilter = constraint.toString();
                if(!mLastFilter.equals("")){
                    //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                    mLastFilteredItems = (List<DriverDataCardWrapper>) results.values;
                    //VehiclesCardsFragment.this.OnUpdateData(mItems,V);
                }
                else
                    mLastFilteredItems = mLastRetrievedItems;
                mCardArrayAdapter.clear();
                mCardArrayAdapter.addAll(BuildCardFromWrapper(mLastFilteredItems));

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                List<DriverDataCardWrapper> filteredResults = getFilteredResults(constraint,false);

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    private List<DriverDataCardWrapper> getFilteredResults(CharSequence constraint, boolean IsRawSearch) {
        List<DriverDataCardWrapper> values = new ArrayList<DriverDataCardWrapper>();
        for(DriverDataCardWrapper item: mLastRetrievedItems)
        {
            /*
            if(!IsRawSearch)
            {
                if((getActivity().getString(R.string.filterALL).equals(constraint))
                        || item.getNetworkTypeName().contains(constraint))
                    values.add(item);
            }
            else
            {
                if(item.getRawText().contains(constraint))
                    values.add(item);
            }
            */
            if(item.getBaseDataToString().contains(constraint))
                values.add(item);
        }
        Collections.sort(values, new Comparator<DriverDataCardWrapper>() {
            @Override
            public int compare(DriverDataCardWrapper lhs, DriverDataCardWrapper rhs) {
                String mLeftField, mRightField;
                mLeftField = lhs.getDriverName();
                mRightField = rhs.getDriverName();
                return mLeftField.compareTo(mRightField);
            }
        });
        return  values;
    }

}
