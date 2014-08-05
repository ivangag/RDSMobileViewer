package com.viewer.rds.actia.rdsmobileviewer.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.PlaceholderFragmentFactory;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by igaglioti on 15/07/2014.
 */
/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link MainMenuCardsFragment.OnMainMenuInteractionListener}
 * interface.
 */
public class MainMenuCardsFragment extends BaseFragment implements IFragmentNotification  {

    private static String ITEM_CUSTOMER_LIST_NAME       = "Customers";
    private static String ITEM_NOT_TRUSTED_LIST_NAME    = "Not Trusted Items";
    private static String ITEM_STATS_LIST_NAME          = "Remote Statistics";

    private CardArrayAdapter mCardArrayAdapter;

    private OnMainMenuInteractionListener mListener;


    @Override
    public boolean getIfHastToSetTitle() {
        return getArguments().getBoolean(PlaceholderFragmentFactory.ARG_SET_TITLE_ACTION_BAR);
    }

    public interface OnMainMenuInteractionListener {

        public void onMenuSectionSelected(Utils.MainMenuSectionItemType subMenuSelected);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            mListener = (OnMainMenuInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public int getTitleResourceId() {
        return R.string.main_menu_title;
    }

    @Override
    public String getUniqueCustomerCode() {
        return "";
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_base_main_menu, container, false);
    }


    @Override
    public void OnUpdateData(String UniqueCode, Object dataContentList, Class itemBaseType) {

        if(getActivity() != null) {

        }
    }

    @Override
    public void OnFilterData(String filterPattern) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initMainMenu();
    }

    @Override
    public String getCustomTitleText() {
        return "";
    }


    private ArrayList<Card> BuildCardBaseData(List<MainMenuDataCardItem> MainMenuList) {

        ArrayList<Card> cards = new ArrayList<Card>();
        for (MainMenuDataCardItem itemMenu : MainMenuList)
        {
            cards.add(itemMenu);
        }
        return cards;
    }

    private void initMainMenu() {
        if(mCardArrayAdapter == null) {
            ArrayList<Card> cards = new ArrayList<Card>();

            mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

            List<MainMenuDataCardItem> items = new ArrayList<MainMenuDataCardItem>();
            MainMenuDataCardItem item = new MainMenuDataCardItem(getActivity(),ITEM_CUSTOMER_LIST_NAME,"", Utils.MainMenuSectionItemType.CUSTOMERS);
            items.add(item);
            item = new MainMenuDataCardItem(getActivity(),ITEM_NOT_TRUSTED_LIST_NAME, "", Utils.MainMenuSectionItemType.ITEMS_NOT_TRUSTED);
            items.add(item);
            item = new MainMenuDataCardItem(getActivity(),ITEM_STATS_LIST_NAME, "", Utils.MainMenuSectionItemType.REMOTE_STATISTICS);
            items.add(item);
            mCardArrayAdapter.addAll(BuildCardBaseData(items));
        }


        CardListView mListView = (CardListView) getActivity().findViewById(R.id.carddemo_list_base_main_menu);
        if (mListView != null) {
            mListView.setAdapter(mCardArrayAdapter);
        }
    }

     public static MainMenuCardsFragment newInstance(DownloadManager.DownloadRequestType fragmentType, boolean setActionBarTitle) {
        MainMenuCardsFragment fragment = new MainMenuCardsFragment();
        Bundle args = new Bundle();
        args.putString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE, fragmentType.toString());
        args.putBoolean(PlaceholderFragmentFactory.ARG_SET_TITLE_ACTION_BAR, setActionBarTitle);
        fragment.setArguments(args);
        return fragment;
    }

    public class MainMenuDataCardItem extends Card{

        protected ImageView mThumbnailMenuType;
        protected TextView mTextViewTitleHeader;
        protected TextView mTextViewTitleMain;

        protected String mTitleHeader;
        protected String mTitleMain;
        protected Utils.MainMenuSectionItemType mItemMenuType;

        public MainMenuDataCardItem(Context context, String titleMain, Utils.MainMenuSectionItemType itemMenuType) {
            super(context, R.layout.card_rds_main_menu_inner_content);
            this.mTitleMain = titleMain;
            this.mItemMenuType = itemMenuType;
            init();
        }

        public MainMenuDataCardItem(Context context,String titleHeader, String titleMain, Utils.MainMenuSectionItemType itemMenuType) {
            super(context, R.layout.card_rds_main_menu_inner_content);
            this.mTitleMain = titleMain;
            this.mTitleHeader = titleHeader;
            this.mItemMenuType = itemMenuType;
            this.setId(itemMenuType.name());
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
                    //Toast.makeText(getActivity(), "Click on card menu" + mCustomerName + " item=" + item.getTitle(), Toast.LENGTH_SHORT).show();

                }
            });
            addCardHeader(header);

            //Add ClickListener
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    //Toast.makeText(getContext(), "Click Listener card=" + mCustomerName, Toast.LENGTH_SHORT).show();
                    mListener.onMenuSectionSelected(mItemMenuType);
                }
            });

            //Set the card inner text
            setTitle(mTitleMain);

            //setSwipeable(true);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            mThumbnailMenuType = (ImageView) parent.findViewById(R.id.rds_menu_item_thumbnail_image);
            switch (mItemMenuType)
            {
                case CUSTOMERS:
                    mThumbnailMenuType.setBackgroundResource(R.drawable.ic_rds_customer);
                    break;
                case ITEMS_NOT_TRUSTED:
                    mThumbnailMenuType.setBackgroundResource(R.drawable.ic_rds_alert);
                    break;
                case REMOTE_STATISTICS:
                    mThumbnailMenuType.setBackgroundResource(R.drawable.ic_rds_stats);
                    break;
            }
        }
    }
}
