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

import com.viewer.rds.actia.rdsmobileviewer.PlaceholderFragmentFactory;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.cards.HeaderCard;
import com.viewer.rds.actia.rdsmobileviewer.cards.CustomExpandCard;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRDSManager;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
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
public class VehiclesCardsFragment extends BaseFragment implements IFragmentNotification, Filterable {

    CardArrayAdapter mCardArrayAdapter;
    int mTitleResourceId = R.string.vehicles_title;
    private String mLastFilter;
    private List<VehicleDataCardWrapper> mLastRetrievedItems = new ArrayList<VehicleDataCardWrapper>();
    private List<VehicleDataCardWrapper> mLastFilteredItems = new ArrayList<VehicleDataCardWrapper>();
    private IFragmentsInteractionListener mListener;
    private DownloadRDSManager.DownloadRequestType fragmentRDSType;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            mListener = (IFragmentsInteractionListener) activity;
        } catch (ClassCastException e) {
           // throw new ClassCastException(activity.toString()
           //         + " must implement OnFragmentInteractionListener");
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
                res = String.format(getString(R.string.customerVehicles), ((VehicleDataCardWrapper) mCardArrayAdapter.getItem(1)).getCustomerName());
        }
        return res;
    }

    @Override
    public void OnUpdateData(String UniqueCustomerCode,Object dataContentList, Class itemBaseType) {

        mUniqueCustomerCode = UniqueCustomerCode;
        if(getActivity() != null) {
            if(dataContentList != null) {
                List<VehicleCustom> data = (List<VehicleCustom>) dataContentList;
                Utils.SortVehiclesByDiagDate(data);
                updateBaseCardAdapter(itemBaseType, data);
                setTitle();
            }
        }
    }

    private void updateBaseCardAdapter(Class itemBaseType, List<VehicleCustom> data) {
            if (itemBaseType.equals(VehicleCustom.class)) {
                mCardArrayAdapter.clear();
                mCardArrayAdapter.addAll(this.BuildCardBaseData(getActivity(), data));
            }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_vehicles, menu);
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
        //return inflater.inflate(R.layout.demo_fragment_list_base, container, false);
        return inflater.inflate(R.layout.fragment_list_base_vehicles, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVehicles();
    }


    boolean mIsFirstVisualization = true;
    @Override
    public void onStart()
    {
        super.onStart();

        if(mIsFirstVisualization) {
            mIsFirstVisualization = false;
            fragmentRDSType = DownloadRDSManager.DownloadRequestType.valueOf((String) getArguments().get(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
            if(mListener != null)
                mListener.onFirstFragmentVisualisation(this, fragmentRDSType);
        }
    }

    private void initVehicles() {

        if(mCardArrayAdapter == null) {
            ArrayList<Card> cards = new ArrayList<Card>();

            mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        }
        //CardListView mListView = (CardListView) getActivity().findViewById(R.id.carddemo_list_base1);
        CardListView mListView = (CardListView) getActivity().findViewById(R.id.listId);

        if (mListView != null) {
            mListView.setAdapter(mCardArrayAdapter);
        }
    }


    public static VehiclesCardsFragment newInstance(DownloadRDSManager.DownloadRequestType fragmentType, boolean setActionBarTitle) {

        VehiclesCardsFragment fragment = new VehiclesCardsFragment();
        Bundle args = new Bundle();
        args.putBoolean(PlaceholderFragmentFactory.ARG_SET_TITLE_ACTION_BAR, setActionBarTitle);
        args.putString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE, fragmentType.toString());
        fragment.setArguments(args);
        return fragment;
    }


    public class VehicleDataCardWrapper extends Card {

        private VehicleCustom mBaseCustomData;
        private final boolean mIsHeaderColoredCard;
        protected TextView mTextViewVRN;
        protected TextView mTextViewDiagnosticTime;

        protected String mCustomerName;
        protected String mTitleHeader;
        protected String mVRN;
        protected String mDiagnosticTime;
        protected ImageView mThumbnailMenuType;
        private ImageButton mButtonExpandCustom;

        public VehicleDataCardWrapper(Context context, VehicleCustom vehicleData) {
            super(context, R.layout.card_rds_item_simple_inner_content);

            mIsHeaderColoredCard = false;
            setLayout(vehicleData, 0);
            init();
        }

        public VehicleDataCardWrapper(Context context, boolean isHeaderColoredCard, int elementCount) {
            super(context, R.layout.card_rds_extras_color_inner_base_main);

            mIsHeaderColoredCard = isHeaderColoredCard;
            setLayout(null, elementCount);

            init();
        }

        private void setLayout(VehicleCustom vehicleData, int elementCount) {
            if(!isHeaderColoredCard()) {
                this.mTitleHeader = vehicleData.getVIN();
                this.mVRN = vehicleData.getVRN();
                this.mDiagnosticTime = vehicleData.getDiagnosticDeviceTime();
                this.mCustomerName = vehicleData.getCustomerName();
            }
            else
            {
                this.mTitleHeader = String.format("%d " + getResources().getString(R.string.vehicles_title),elementCount);
            }
            mBaseCustomData = vehicleData;
        }


        private void init() {

            //Create a CardHeader
            CardHeader header = new CardHeader(getContext());

            //Set the header title
            header.setTitle(mTitleHeader);

            int popMenuRes = isHeaderColoredCard() ? R.menu.rds_popmenu_header_card : R.menu.rds_vehicles_popmenu;
            //Add a popup menu. This method set OverFlow button to visible
            header.setPopupMenu(popMenuRes, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_getDiagnosticData:
                            //Toast.makeText(getContext(), "Implementing Retrieving Diagnostic for " + item.(getTitle(), Toast.LENGTH_SHORT).show();
                            mListener.onRequireVehicleDiagnosticData(((VehicleDataCardWrapper) card).getVIN(),false);
                            break;
                        case R.id.action_download_data:
                            mListener.onCustomerVehiclesDataRequiredSelected(fragmentRDSType,VehiclesCardsFragment.this.getUniqueCustomerCode(),false);
                            break;
                    }
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

            setSwipeable(!isHeaderColoredCard());

            setShadow(true);
            //This provides an expand area
            CustomExpandCard expand = new CustomExpandCard(getActivity(), this.mBaseCustomData.toString());
            //Add Expand Area to Card
            addCardExpand(expand);

        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            //Retrieve elements
            mThumbnailMenuType = (ImageView) parent.findViewById(R.id.rds_menu_item_thumbnail_image);
            mTextViewVRN = (TextView) parent.findViewById(R.id.card_element_primary_info);
            mTextViewDiagnosticTime = (TextView) parent.findViewById(R.id.card_element_secondary_info);
            mButtonExpandCustom = (ImageButton) parent.findViewById(R.id.card_rds_expand_button_info);

            if (mThumbnailMenuType != null)
                mThumbnailMenuType.setBackgroundResource(R.drawable.ic_rds_truck);

            if (mTextViewVRN != null)
                mTextViewVRN.setText(mVRN);

            if (mTextViewDiagnosticTime != null)
                mTextViewDiagnosticTime.setText(mDiagnosticTime);

            if (mButtonExpandCustom != null) {
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

        public String toRawString()
        {
            return this.mCustomerName + "\r\n"  + this.mTitleHeader
                    + "\r\n"  + this.mVRN + "\r\n"  + this.mDiagnosticTime;
        }

        public String getBaseDataToString()
        {
            return this.mBaseCustomData.toString();
        }

        public String getVIN() {
            return mTitleHeader;
        }

        public boolean isHeaderColoredCard() {
            return mIsHeaderColoredCard;
        }
    }


    protected ArrayList<Card> BuildCardBaseData(Context context, List<VehicleCustom> vehicleData) {

        ArrayList<Card> cards = new ArrayList<Card>();
        mLastRetrievedItems.clear();
        HeaderCard headerCard = HeaderCard.newInstance(this,
                vehicleData.size(), getResources().getString(R.string.vehicles_title));
        cards.add(headerCard);
        for (VehicleCustom vehicle : vehicleData)
        {
            VehicleDataCardWrapper card = new VehicleDataCardWrapper(context,vehicle);
            mLastRetrievedItems.add(card);
            cards.add(card);
        }
        return cards;
    }


    protected ArrayList<Card> BuildCardFromWrapper(List<VehicleDataCardWrapper> vehicleData) {

        ArrayList<Card> cards = new ArrayList<Card>();
        HeaderCard headerCard = HeaderCard.newInstance(this,
                vehicleData.size(), getResources().getString(R.string.vehicles_title));
        cards.add(headerCard);
        for (VehicleDataCardWrapper card : vehicleData)
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
                if(!mLastFilter.equals(""))
                    mLastFilteredItems = (List<VehicleDataCardWrapper>) results.values;
                else
                    mLastFilteredItems = mLastRetrievedItems;
                mCardArrayAdapter.clear();
                mCardArrayAdapter.addAll(BuildCardFromWrapper(mLastFilteredItems));

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                List<VehicleDataCardWrapper> filteredResults = getFilteredResults(constraint,false);

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    private List<VehicleDataCardWrapper> getFilteredResults(CharSequence constraint, boolean IsRawSearch) {
        List<VehicleDataCardWrapper> values = new ArrayList<VehicleDataCardWrapper>();
        for(VehicleDataCardWrapper item: mLastRetrievedItems)
        {
            if(item.getBaseDataToString().contains(constraint))
                values.add(item);
        }
        Collections.sort(values, new Comparator<VehicleDataCardWrapper>() {
            @Override
            public int compare(VehicleDataCardWrapper lhs, VehicleDataCardWrapper rhs) {
                String mLeftField, mRightField;
                mLeftField = lhs.getVIN();
                mRightField = rhs.getVIN();
                return mLeftField.compareTo(mRightField);
            }
        });
        return  values;
    }

}
