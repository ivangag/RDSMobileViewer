/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package com.viewer.rds.actia.rdsmobileviewer.cards;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewer.rds.actia.rdsmobileviewer.PlaceholderFragmentFactory;
import com.viewer.rds.actia.rdsmobileviewer.R;
import com.viewer.rds.actia.rdsmobileviewer.fragments.BaseFragment;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRDSManager;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;

/**
 * Simple Colored card
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class HeaderCard extends Card {

    public final static String PARENT_FRAGMENT_TYPE = "PARENT_FRAGMENT_TYPE";
    public final static String CUSTOMER_CODE = "CUSTOMER_CODE";
    private String mCustomerCode;
    private DownloadRDSManager.DownloadRequestType mDownloadRequestType;
    private BaseFragment.IFragmentsInteractionListener mListener;
    private String mFragmentTag;
    protected String mTitle;
    protected int count;

    public HeaderCard(Context context, BaseFragment.IFragmentsInteractionListener listener, Bundle extraData) {
        this(context, R.layout.card_rds_extras_color_inner_base_main);
        mDownloadRequestType = Enum.valueOf(DownloadRDSManager.DownloadRequestType.class,extraData.getString(PARENT_FRAGMENT_TYPE));
        mCustomerCode =  extraData.getString(CUSTOMER_CODE);
        mListener = listener;
    }

    public HeaderCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }


    private void init() {

        //Create a CardHeader
        CardHeader header = new CardHeader(getContext());

        //Set the header title
        header.setTitle(mTitle);

        //Add a popup menu. This method set OverFlow button to visible
        header.setPopupMenu(R.menu.rds_popmenu_header_card, new CardHeader.OnClickCardHeaderPopupMenuListener() {

            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_download_data:
                        switch (mDownloadRequestType) {
                            case CRDS_OWNED:
                            case CRDS_NOT_TRUSTED:
                                mListener.onCustomerCRDSDataRequiredSelected(mDownloadRequestType,mCustomerCode,false);
                                break;
                            case CUSTOMERS_LIST:
                                break;
                            case VEHICLES_OWNED:
                            case VEHICLE_NOT_TRUSTED:
                                mListener.onCustomerVehiclesDataRequiredSelected(mDownloadRequestType,mCustomerCode,false);
                                break;
                            case DRIVERS_NOT_TRUSTED:
                            case DRIVERS_OWNED:
                                mListener.onCustomerDrivesDataRequiredSelected(mDownloadRequestType, mCustomerCode,false);
                                break;
                            case MAIN_MENU:
                                break;
                            case VEHICLE_DIAGNOSTIC:
                                break;
                        }
                        break;
                }
            }
        });

        addCardHeader(header);

        //Add ClickListener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
               // Toast.makeText(getContext(), "Click Listener card=" + count, Toast.LENGTH_SHORT).show();
            }
        });

        setShadow(true);

    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        this.getCardView().findViewById(R.id.card_main_content_layout).setMinimumHeight(24);

        //Retrieve elements
        TextView title = (TextView) parent.findViewById(R.id.card_rds_color_inner_simple_title);



        if (title != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            else
                title.setGravity(Gravity.CENTER);
            title.setText("");
        }


       // getCardHeader().getInnerView(getContext(),parent).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {

        mTitle = title;
        if(getCardHeader() != null)
        {
            getCardHeader().setTitle(mTitle);
        }
    }

    public String getFragmentTag() {
        return mFragmentTag;
    }

    public void setFragmentTag(String mFragmentTag) {
        this.mFragmentTag = mFragmentTag;
    }



    public static HeaderCard newInstance(BaseFragment parentFragment,int elementCount, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(HeaderCard.PARENT_FRAGMENT_TYPE,
                parentFragment.getArguments().getString(PlaceholderFragmentFactory.ARG_FRAGMENT_TYPE));
        bundle.putString(HeaderCard.CUSTOMER_CODE, parentFragment.getUniqueCustomerCode());
        HeaderCard headerCard = new HeaderCard(parentFragment.getActivity(),(BaseFragment.IFragmentsInteractionListener)parentFragment.getActivity(), bundle);
        headerCard.setTitle(String.format("%d " + title ,elementCount));
        headerCard.setBackgroundResourceId(R.drawable.card_background_color_blue);
        return headerCard;
    }
}
