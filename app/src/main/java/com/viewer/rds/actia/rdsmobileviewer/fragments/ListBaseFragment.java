package com.viewer.rds.actia.rdsmobileviewer.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.viewer.rds.actia.rdsmobileviewer.R;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * List base example
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class ListBaseFragment extends BaseFragment implements IFragmentNotification{

    protected ScrollView mScrollView;
    private boolean mTitleSet = false;
    @Override
    public boolean getIfHastToSetTitle() {
        return mTitleSet;
    }

    @Override
    public int getTitleResourceId() {
        return R.string.carddemo_title_list_base;
    }

    @Override
    public String getUniqueCustomerCode() {
        return "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_fragment_list_base, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initCards();
    }

    @Override
    public String getCustomTitleText() {
        return null;
    }

    public static Fragment newInstance(int sectionNumber) {
        ListBaseFragment fragment = new ListBaseFragment();
        Bundle args = new Bundle();
        //args.putInt(PlaceholderFragmentFactory.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    CardArrayAdapter mCardArrayAdapter;
    private void initCards() {


        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i=0;i<200;i++){
            CardExample2 cardx = new CardExample2(this.getActivity());
            cardx.title="Application example "+i;
            cardx.secondaryTitle="A company inc..."+i;
            cardx.rating=(float)(Math.random()*(5.0));
            cardx.count=i;
            //cards.add(cardx);

            CardExample card = new CardExample(getActivity(),"My title "+i,"Inner text "+i);
            cards.add(card);
        }

        if(mCardArrayAdapter == null) {
            mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        }
        CardListView listView = (CardListView) getActivity().findViewById(R.id.carddemo_list_base1);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }
    }

    @Override
    public void OnUpdateData(String UniqueCode, Object dataContentList, Class itemBaseType) {
        if(getActivity() != null) {

        }
    }

    @Override
    public void OnFilterData(String filterPattern) {

    }

    public class CardExample extends Card{

        protected String mTitleHeader;
        protected String mTitleMain;

        public CardExample(Context context,String titleHeader,String titleMain) {
            super(context, R.layout.carddemo_main_example_inner_content);
            this.mTitleHeader=titleHeader;
            this.mTitleMain=titleMain;
            init();
        }

        private void init(){

            //Create a CardHeader
            CardHeader header = new CardHeader(getActivity());

            //Set the header title
            header.setTitle(mTitleHeader);

            //Add a popup menu. This method set OverFlow button to visible
            header.setPopupMenu(R.menu.rds_customers_popmenu, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    Toast.makeText(getActivity(), "Click on card menu" + mTitleHeader +" item=" +  item.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
            addCardHeader(header);

            //Add ClickListener
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getContext(), "Click Listener card=" + mTitleHeader, Toast.LENGTH_SHORT).show();
                }
            });

            //Set the card inner text
            setTitle(mTitleMain);

            setSwipeable(true);
        }


        @Override
        public int getType() {
            //Very important with different inner layouts
            return 0;
        }
    }


    public class CardExample2 extends Card{

        protected TextView mTitle;
        protected TextView mSecondaryTitle;
        protected RatingBar mRatingBar;
        protected int resourceIdThumbnail;
        protected int count;

        protected String title;
        protected String secondaryTitle;
        protected float rating;


        public CardExample2(Context context) {
            super(context, R.layout.carddemo_mycard_inner_content);
            init();
        }

        private void init(){

            //Create a CardHeader
            CardHeader header = new CardHeader(getActivity());
            addCardHeader(header);

            //Add ClickListener
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getContext(), "Click Listener card=" + getTitle(), Toast.LENGTH_SHORT).show();
                }
            });

            setSwipeable(true);
        }


        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            //Retrieve elements
            mTitle = (TextView) parent.findViewById(R.id.carddemo_myapps_main_inner_title);
            mSecondaryTitle = (TextView) parent.findViewById(R.id.carddemo_myapps_main_inner_secondaryTitle);
            mRatingBar = (RatingBar) parent.findViewById(R.id.carddemo_myapps_main_inner_ratingBar);

            if (mTitle != null)
                mTitle.setText(title);

            if (mSecondaryTitle != null)
                mSecondaryTitle.setText(secondaryTitle);

            if (mRatingBar != null) {
                mRatingBar.setNumStars(5);
                mRatingBar.setMax(5);
                mRatingBar.setStepSize(0.5f);
                mRatingBar.setRating(rating);
            }
        }

        @Override
        public int getType() {
            //Very important with different inner layouts
            return 1;
        }
    }


}

