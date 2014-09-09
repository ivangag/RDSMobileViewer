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

import com.viewer.rds.actia.rdsmobileviewer.CRDSCustom;
import com.viewer.rds.actia.rdsmobileviewer.PlaceholderFragmentFactory;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.cards.CustomExpandCard;
import com.viewer.rds.actia.rdsmobileviewer.cards.HeaderCard;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRDSManager;

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
public class CRDSCardsFragment extends BaseFragment implements IFragmentNotification, Filterable{


    private List<CRDSDataCardWrapper> mLastRetrievedItems = new ArrayList<CRDSDataCardWrapper>();
    private List<CRDSDataCardWrapper> mLastFilteredItems = new ArrayList<CRDSDataCardWrapper>();
    CardArrayAdapter mCardArrayAdapter;
    int mTitleResourceId = R.string.crds_fragment_title;
    private String mLastFilter;
    private String mUniqueCustomerCode;


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
            DownloadRDSManager.DownloadRequestType type = DownloadRDSManager.DownloadRequestType.valueOf((String) getArguments().get(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
            if(mListener != null)
                mListener.onFirstFragmentVisualisation(this, type);
        }
    }

    @Override
    public String getUniqueCustomerCode() {
        return mUniqueCustomerCode;
    }

    @Override
    public boolean getIfHastToSetTitle() {
        return getArguments().getBoolean(PlaceholderFragmentFactory.ARG_SET_TITLE_ACTION_BAR);
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
                res = String.format(getString(R.string.customerCRDS), ((CRDSDataCardWrapper) mCardArrayAdapter.getItem(1)).getCustomerName());
        }
        return res;
    }

    @Override
    public void OnUpdateData(String UniqueCustomerCode, Object dataContentList, Class itemBaseType) {

        mUniqueCustomerCode = UniqueCustomerCode;
        if(getActivity() != null) {
            List<CRDSCustom> data = (List<CRDSCustom>) dataContentList;

            if (itemBaseType.equals(CRDSCustom.class)) {
                mCardArrayAdapter.clear();
                mCardArrayAdapter.addAll(this.BuildCardBaseData(getActivity(), data));
            }
            setTitle();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_crds, menu);
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
        return inflater.inflate(R.layout.fragment_list_base_crds, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVehicles();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void initVehicles() {

        if(mCardArrayAdapter == null) {
            ArrayList<Card> cards = new ArrayList<Card>();

            mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        }
        CardListView mListView = (CardListView) getActivity().findViewById(R.id.card_list_base_crds);

        if (mListView != null) {
            mListView.setAdapter(mCardArrayAdapter);
        }
    }

    public static CRDSCardsFragment newInstance(DownloadRDSManager.DownloadRequestType fragmentType,boolean setActionBarTitle) {

        CRDSCardsFragment fragment = new CRDSCardsFragment();
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
                    //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                    mLastFilteredItems = (List<CRDSDataCardWrapper>) results.values;
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
                List<CRDSDataCardWrapper> filteredResults = getFilteredResults(constraint,false);

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    private List<CRDSDataCardWrapper> getFilteredResults(CharSequence constraint, boolean IsRawSearch) {
        List<CRDSDataCardWrapper> values = new ArrayList<CRDSDataCardWrapper>();
        for(CRDSDataCardWrapper item: mLastRetrievedItems)
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
        Collections.sort(values, new Comparator<CRDSDataCardWrapper>() {
            @Override
            public int compare(CRDSDataCardWrapper lhs, CRDSDataCardWrapper rhs) {
                String mLeftField, mRightField;
                mLeftField = lhs.getCRDSGuid();
                mRightField = rhs.getCRDSGuid();
                return mLeftField.compareTo(mRightField);
            }
        });
        return  values;
    }

    public class CRDSDataCardWrapper extends Card {

        private final CRDSCustom mBaseCustomData;
        protected TextView mTextViewVRN;
        protected TextView mTextViewDiagnosticTime;

        protected String mCustomerName;
        protected String mTitleHeader;
        protected String mCRDSGuid;
        protected String mLastAliveSignal;
        protected ImageView mThumbnailMenuType;
        private ImageButton mButtonExpandCustom;

        public CRDSDataCardWrapper(Context context, CRDSCustom crdsData) {
            super(context, R.layout.card_rds_item_simple_inner_content);

            DownloadRDSManager.DownloadRequestType downloadRequestType = Enum.valueOf(DownloadRDSManager.DownloadRequestType.class,
                    CRDSCardsFragment.this.getArguments().getString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
            if(downloadRequestType.equals(DownloadRDSManager.DownloadRequestType.CRDS_NOT_TRUSTED))
                this.mTitleHeader =  crdsData.getAppName() + " (" + crdsData.getRagioneSociale() + ")";
            else
                this.mTitleHeader = crdsData.getAppName();
            this.mCRDSGuid =  crdsData.getCRDSId();
            this.mLastAliveSignal = crdsData.getLastLifeSignal();
            this.mCustomerName = crdsData.getRagioneSociale();
            this.mBaseCustomData = crdsData;
            init();
        }


        private void init() {

            //Create a CardHeader
            CardHeader header = new CardHeader(getContext());

            //Set the header title
            header.setTitle(mTitleHeader);

            //Add a popup menu. This method set OverFlow button to visible
            header.setPopupMenu(R.menu.rds_crds_popmenu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.action_getCRDSExtraInfo:
                            Toast.makeText(getContext(), "Implement " + item.getTitle(), Toast.LENGTH_SHORT).show();
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
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            //Retrieve elements
            mThumbnailMenuType = (ImageView) parent.findViewById(R.id.rds_menu_item_thumbnail_image);
            mTextViewVRN = (TextView) parent.findViewById(R.id.card_element_primary_info);
            mTextViewDiagnosticTime = (TextView) parent.findViewById(R.id.card_element_secondary_info);
            mButtonExpandCustom = (ImageButton)parent.findViewById(R.id.card_rds_expand_button_info);

            if (mThumbnailMenuType != null)
                mThumbnailMenuType.setBackgroundResource(R.drawable.ic_rds_crds);

            if (mTextViewVRN != null)
                mTextViewVRN.setText(mCRDSGuid);

            if (mTextViewDiagnosticTime != null)
                mTextViewDiagnosticTime.setText(mLastAliveSignal);

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

        public String getCRDSGuid() {
            return mCRDSGuid;
        }


        public String toRawString() {
            return this.mCustomerName + "\r\n"  + this.mTitleHeader
                    + "\r\n"  + this.mLastAliveSignal + "\r\n"  + this.mCRDSGuid;
        }

        public String getBaseDataToString()
        {
            return this.mBaseCustomData.toString();
        }

    }


    protected ArrayList<Card> BuildCardBaseData(Context context, List<CRDSCustom> crdsData) {

        ArrayList<Card> cards = new ArrayList<Card>();
        mLastRetrievedItems.clear();

        HeaderCard headerCard = HeaderCard.newInstance(this,
                crdsData.size(), getResources().getString(R.string.crds_fragment_title));
        cards.add(headerCard);

        for (CRDSCustom crds : crdsData)
        {
            CRDSDataCardWrapper card = new CRDSDataCardWrapper(context,crds);
            mLastRetrievedItems.add(card);
            cards.add(card);
        }
        return cards;
    }

    protected ArrayList<Card> BuildCardFromWrapper(List<CRDSDataCardWrapper> crdsData) {

        ArrayList<Card> cards = new ArrayList<Card>();

        HeaderCard headerCard = HeaderCard.newInstance(this,
                crdsData.size(), getResources().getString(R.string.crds_fragment_title));
        cards.add(headerCard);

        for (CRDSDataCardWrapper card : crdsData)
        {
            cards.add(card);
        }
        return cards;
    }

}
