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

import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.PlaceholderFragmentFactory;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadManager;
import com.viewer.rds.actia.rdsmobileviewer.cards.CustomExpandCard;

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
/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link com.viewer.rds.actia.rdsmobileviewer.fragments.BaseFragment.IFragmentsInteractionListener}
 * interface.
 */
public class CustomersCardsFragment extends BaseFragment implements IFragmentNotification, Filterable {

    CardArrayAdapter mCardArrayAdapter;
    private IFragmentsInteractionListener mListener;
    private List<CustomerDataCardWrapper> mLastRetrievedItems = new ArrayList<CustomerDataCardWrapper>();
    private List<CustomerDataCardWrapper> mLastFilteredItems;
    private String mLastFilter;
    private DownloadManager.DownloadRequestType fragmentRDSType;

    @Override
    public boolean getIfHastToSetTitle() {
        return getArguments().getBoolean(PlaceholderFragmentFactory.ARG_SET_TITLE_ACTION_BAR);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.title_customers;
    }

    @Override
    public String getUniqueCustomerCode() {
        return "";
    }

    @Override
    public void OnUpdateData(String UniqueCode, Object dataContentList, Class itemBaseType) {

        if(getActivity() != null) {
            List<MainContractorData> data = (List<MainContractorData>) dataContentList;

            if (itemBaseType.equals(MainContractorData.class)) {
                mCardArrayAdapter.clear();
                List<Card> mCustomerDataCardWrappers = this.BuildCardBaseData(getActivity(), data);
                mCardArrayAdapter.addAll(mCustomerDataCardWrappers);
            }
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            mListener = (IFragmentsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_customers, menu);
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
        return inflater.inflate(R.layout.fragment_list_base_customer, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCustomers();
    }

    boolean mIsFirstVisualization = true;
    @Override
    public void onStart()
    {
        super.onStart();

        if(mIsFirstVisualization) {
            mIsFirstVisualization = false;
            fragmentRDSType = DownloadManager.DownloadRequestType.valueOf((String) getArguments().get(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
            if(mListener != null)
                mListener.onFirstFragmentVisualisation(this, fragmentRDSType);
        }
    }

    @Override
    public String getCustomTitleText() {
        return "";
    }

    private void initCustomers() {
        initAdapter();
        initListView();
    }

    private void initListView() {
        CardListView mListView = (CardListView) getActivity().findViewById(R.id.card_list_base_customer);

        if (mListView != null) {
            mListView.setAdapter(mCardArrayAdapter);
        }
    }


    private void initAdapter() {
        if(mCardArrayAdapter == null) {
            ArrayList<Card> cards = new ArrayList<Card>();

            mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        }
    }

    public static CustomersCardsFragment newInstance(DownloadManager.DownloadRequestType fragmentType, boolean setActionBarTitle ) {
        CustomersCardsFragment fragment = new CustomersCardsFragment();
        Bundle args = new Bundle();
        args.putBoolean(PlaceholderFragmentFactory.ARG_SET_TITLE_ACTION_BAR, setActionBarTitle);
        args.putString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE, fragmentType.toString());
        fragment.setArguments(args);
        return fragment;
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
                    mLastFilteredItems = (List<CustomerDataCardWrapper>) results.values;
                }
                else
                    mLastFilteredItems = mLastRetrievedItems;
                mCardArrayAdapter.clear();
                mCardArrayAdapter.addAll(BuildCardFromWrapper(mLastFilteredItems));

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                List<CustomerDataCardWrapper> filteredResults = getFilteredResults(constraint,false);

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    private ArrayList<Card> BuildCardFromWrapper(List<CustomerDataCardWrapper> items) {
        ArrayList<Card> cards = new ArrayList<Card>();
        for (CustomerDataCardWrapper card : items)
        {
            cards.add(card);
        }
        return cards;
    }

    private List<CustomerDataCardWrapper> getFilteredResults(CharSequence constraint, boolean IsRawSearch) {
        List<CustomerDataCardWrapper> values = new ArrayList<CustomerDataCardWrapper>();
        for(CustomerDataCardWrapper item: mLastRetrievedItems)
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
            if(item.getBaseDataToString().toUpperCase().contains(constraint))
                values.add(item);
        }
        Collections.sort(values, new Comparator<CustomerDataCardWrapper>() {
            @Override
            public int compare(CustomerDataCardWrapper lhs, CustomerDataCardWrapper rhs) {
                String mLeftField, mRightField;
                mLeftField = lhs.getCustomerName();
                mRightField = rhs.getCustomerName();
                return mLeftField.compareTo(mRightField);
            }
        });
        return  values;
    }

    public class CustomerDataCardWrapper extends Card {

        private final MainContractorData mBaseCustomData;
        protected TextView mTextViewCustomerAncodice;
        protected TextView mTextViewIdCustomer;
        protected TextView mTextViewCustomerName;

        protected String mCustomerName;
        private String mCustomerAncodice;
        protected String mIdCustomer;
        private ImageView mThumbnailMenuType;
        private ImageButton mButtonExpandCustom;

        public CustomerDataCardWrapper(Context context, final MainContractorData mainContractorData) {
            super(context, R.layout.card_rds_item_simple_inner_content);
            this.mCustomerName = mainContractorData.getFriendlyName();
            this.mCustomerAncodice = mainContractorData.getAncodice();
            this.mIdCustomer = String.valueOf(mainContractorData.getIdCustomer());
            this.mBaseCustomData = mainContractorData;
            init();
        }


        private void init() {

            //Create a CardHeader
            CardHeader header = new CardHeader(getContext());

            //Set the header title
            header.setTitle(mCustomerName);

            //Add a popup menu. This method set OverFlow button to visible
            header.setPopupMenu(R.menu.rds_customers_popmenu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_getVehicles:
                            mListener.onCustomerVehiclesDataRequiredSelected(DownloadManager.DownloadRequestType.VEHICLES_OWNED,((CustomerDataCardWrapper) card).mCustomerAncodice,true);
                            break;
                        case R.id.action_getDrivers:
                            mListener.onCustomerDrivesDataRequiredSelected(DownloadManager.DownloadRequestType.DRIVERS_OWNED,((CustomerDataCardWrapper) card).mCustomerAncodice,true);
                            break;
                        case R.id.action_getCRDS:
                            mListener.onCustomerCRDSDataRequiredSelected(DownloadManager.DownloadRequestType.CRDS_OWNED,((CustomerDataCardWrapper) card).mCustomerAncodice,true);
                            break;
                    }
                }
            });

            addCardHeader(header);


            //This provides a simple (and useless) expand area
            CustomExpandCard expand = new CustomExpandCard(getActivity(),this.mBaseCustomData.toString());
            //Add Expand Area to Card
            addCardExpand(expand);

            //setExpanded(true);

            //Animator listener
            setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
                @Override
                public void onExpandEnd(Card card) {
                    //Toast.makeText(getActivity(), "Expand " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
                }
            });

            setOnCollapseAnimatorEndListener(new Card.OnCollapseAnimatorEndListener() {
                @Override
                public void onCollapseEnd(Card card) {
                    //Toast.makeText(getActivity(),"Collpase " +card.getCardHeader().getTitle(),Toast.LENGTH_SHORT).show();
                }
            });

            //Add ClickListener
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    //Toast.makeText(getContext(), "Click Listener card=" + mCustomerName, Toast.LENGTH_SHORT).show();
                    mListener.onCustomerSelected(((CustomerDataCardWrapper)card).mCustomerAncodice);
                }
            });


            //Add a viewToClickExpand to enable click on whole card
            ViewToClickToExpand viewToClickToExpand =
                    ViewToClickToExpand.builder()
                            .highlightView(false)
                            .setupCardElement(ViewToClickToExpand.CardElementUI.CARD);

            //setSwipeable(true);
        }

        @Override
        public void setupInnerViewElements(final ViewGroup parent, View view) {

            //Retrieve elements
            mThumbnailMenuType = (ImageView) parent.findViewById(R.id.rds_menu_item_thumbnail_image);
            mTextViewCustomerAncodice = (TextView) parent.findViewById(R.id.card_element_primary_info);
            mTextViewIdCustomer = (TextView) parent.findViewById(R.id.card_element_secondary_info);
            //mTextViewCustomerName = (TextView) parent.findViewById(R.id.card_rds_customer_name_info);

            mButtonExpandCustom = (ImageButton)parent.findViewById(R.id.card_rds_expand_button_info);
            if (mThumbnailMenuType != null)
                mThumbnailMenuType.setBackgroundResource(R.drawable.ic_rds_customer);

            if (mTextViewCustomerAncodice != null)
                mTextViewCustomerAncodice.setText(mCustomerAncodice);

            if (mTextViewIdCustomer != null)
                mTextViewIdCustomer.setText(mIdCustomer);

            if(mButtonExpandCustom != null) {
                mButtonExpandCustom.setBackgroundResource(R.drawable.card_menu_button_expand);

                mButtonExpandCustom.setClickable(true);

                ViewToClickToExpand extraCustomButtonExpand =
                        ViewToClickToExpand.builder().highlightView(false)
                                .setupView(mButtonExpandCustom);

                setViewToClickToExpand(extraCustomButtonExpand);
            }

            //if (mTextViewCustomerName != null)
            //    mTextViewCustomerName.setText(mCustomerName);

        }


        public String getCustomerName(){
            return mCustomerName;
        }



        public String toRawString() {
            return this.mCustomerName + "\r\n"  + this.mCustomerAncodice
                    + "\r\n"  + this.mIdCustomer;
        }

        public String getBaseDataToString()
        {
            return this.mBaseCustomData.toString();
        }

    }
    protected ArrayList<Card> BuildCardBaseData(Context context, List<MainContractorData> customersData) {

        ArrayList<Card> cards = new ArrayList<Card>();
        mLastRetrievedItems.clear();
        for (MainContractorData customer : customersData)
        {
            CustomerDataCardWrapper card = new CustomerDataCardWrapper(context,customer);
            mLastRetrievedItems.add(card);
            cards.add(card);
        }
        return cards;
    }

}
