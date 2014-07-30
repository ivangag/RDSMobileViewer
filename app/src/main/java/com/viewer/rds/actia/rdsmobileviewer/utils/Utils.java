package com.viewer.rds.actia.rdsmobileviewer.utils;

import android.content.Context;

import com.viewer.rds.actia.rdsmobileviewer.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPInputStream;

/**
 * Created by igaglioti on 09/07/2014.
 */
public class Utils {

    static WeakReference<Context> mContext = null;

    public final static Byte GZIP_MAGICAL = 0x1f;
    public final static String MAIN_MENU_KEY = "MAIN_MENU_KEY";

    public static enum MainMenuSectionItemType {
        CUSTOMERS,
        ITEMS_NOT_TRUSTED, //Warnings
        REMOTE_STATISTICS,
    }

    ;


    public final static int TAB_POSITION_VEHICLES = 0;
    public final static int TAB_POSITION_DRIVERS = 1;
    public final static int TAB_POSITION_CRDS = 2;
    public final static int MAX_TABS_ITEMS_NOT_TRUSTED_COUNT = 3;

    public static CharSequence TITLE_DRIVERS_NOT_TRUSTED;
    public static CharSequence TITLE_VEHICLES_NOT_TRUSTED = "";
    public static CharSequence TITLE_CRDS_NOT_TRUSTED = "";
    public static CharSequence TITLE_CUSTOMERS;
    public static CharSequence TITLE_CARDS_EXAMPLE;

    private static final long TICKS_AT_EPOCH = 621355968000000000L;
    private static final long TICKS_PER_MILLISECOND = 10000;
    private static final long TIME_HOURS = 3600;

    public static void Init(final Context context) {
        if(mContext == null) {
            mContext = new WeakReference<Context>(context);
            TITLE_VEHICLES_NOT_TRUSTED = mContext.get().getResources().getString(R.string.title_vehicleNotTrusted).toUpperCase();
            TITLE_CRDS_NOT_TRUSTED = mContext.get().getResources().getString(R.string.title_crdsNotTrusted).toUpperCase();
            TITLE_DRIVERS_NOT_TRUSTED = mContext.get().getResources().getString(R.string.title_driversNotTrusted).toUpperCase();
            TITLE_CUSTOMERS = mContext.get().getResources().getString(R.string.title_customers).toUpperCase();
            TITLE_CARDS_EXAMPLE = mContext.get().getResources().getString(R.string.title_cards_example).toUpperCase();
        }
    }

    public static String getDateTimeFromTicks(String jsonResponseFieldDateTime) {
        ///Date(1304413093337+0200)/
        //or
        // 20110329_124414
        int offsetTimeZone = 0;
        String result = jsonResponseFieldDateTime;
        String startTag = "Date(";
        String plusDeli = "+";
        String endTag = ")/";

        if (result.contains(startTag)) {
            String tmp = result;
            int idxStartTag = tmp.indexOf(startTag);
            int idxPlusTag = tmp.indexOf(plusDeli);
            int idxEntTag = tmp.indexOf(endTag);
            result = tmp.substring(
                    idxStartTag + startTag.length(), idxPlusTag);


            String sOffsetTimeZone = "0";
            try {
                sOffsetTimeZone = tmp.substring(idxPlusTag + 1, idxEntTag).substring(0, 2);
                offsetTimeZone = Integer.valueOf(sOffsetTimeZone);
            } catch (Exception exc) {
                offsetTimeZone = 0;
            }
            // Add 5 hour to go to GMT+1 time (server-side storing time)
            try {
                long ticks = Long.valueOf(result) + TIME_HOURS * 1000 * (4 + offsetTimeZone);
                Date date = new Date(ticks);
                result = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);

            } catch (NumberFormatException exc) {
                result = "";
            }
        } else if (result.contains("_")) {
            try {
                Date date = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(jsonResponseFieldDateTime);
                result = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static byte[] convertToByteArray(byte[] list) throws IOException {
        // write to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        for (Byte element : list) {
            out.writeByte(element);
        }
        return baos.toByteArray();
    }
}