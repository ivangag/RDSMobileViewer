package com.viewer.rds.actia.rdsmobileviewer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;

import java.util.ArrayList;

/**
 * Created by igaglioti on 03/04/14.
 */
public class RDSDBMapper {

    final String LOG_TAG = RDSDBMapper.class.getCanonicalName();
    Context mContext;
    private static  RDSDBMapper mRDSDBMapper = new RDSDBMapper();

    public static RDSDBMapper getInstance(Context context) {
        if(context == null)
            mRDSDBMapper.setContext(context);
        return mRDSDBMapper;
    }
    private RDSDBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public synchronized RDSDBMapper open()  throws SQLException
    {
        if(null == mDBHelper) {
            try {
                Log.i(LOG_TAG, "db open");
                mDBHelper = new RDSDBHelper(mContext, null);
                mDB = mDBHelper.getWritableDatabase();

            } catch (SQLiteException e) {
                Log.w(LOG_TAG, "db open in readable mode::" + e.getMessage());
                mDB = mDBHelper.getReadableDatabase();
            }
        }
        else
        {
            if(!mDB.isOpen())
                mDB = mDBHelper.getWritableDatabase();
        }
        return this;
    }

    public ArrayList<MainContractorData> queryAllCustomers()
    {
        Cursor cursor = query(RDSDBHelper.STORY_TABLE_NAME,RDSDBHelper.columns,null, new String[]{},null);
        ArrayList<MainContractorData> res = new ArrayList<MainContractorData>();
        ContentValues values = new ContentValues();
        while (cursor.moveToNext()) {
            values.put(RDSDBHelper.ANCODICE, cursor.getString(cursor.getColumnIndex(RDSDBHelper.ANCODICE)));
            values.put(RDSDBHelper.AUTO_DRIVER, cursor.getString(cursor.getColumnIndex(RDSDBHelper.AUTO_DRIVER)));
            values.put(RDSDBHelper.EMAIL, cursor.getString(cursor.getColumnIndex(RDSDBHelper.EMAIL)));
            values.put(RDSDBHelper.EMAIL_FORWARD, cursor.getString(cursor.getColumnIndex(RDSDBHelper.EMAIL_FORWARD)));
            values.put(RDSDBHelper.FILE_PUSH, cursor.getString(cursor.getColumnIndex(RDSDBHelper.FILE_PUSH)));
            values.put(RDSDBHelper.ID_CUSTOMER, cursor.getString(cursor.getColumnIndex(RDSDBHelper.ID_CUSTOMER)));
            values.put(RDSDBHelper.INSERT_DATE, cursor.getDouble(cursor.getColumnIndex(RDSDBHelper.INSERT_DATE)));
            values.put(RDSDBHelper.SUPER_CRDS, cursor.getString(cursor.getColumnIndex(RDSDBHelper.SUPER_CRDS)));
            values.put(RDSDBHelper.NAME, cursor.getString(cursor.getColumnIndex(RDSDBHelper.NAME)));
            res.add(MainContractorData.getCustomerDataFromValues(values));
            values.clear();
        }
        return  res;
    }

    public synchronized Cursor query(final String table, final String[] columns,
                        final String selection, final String[] selectionArgs,
                        final String sortOrder) {
        Cursor cursor = null;
        if(null != mDB)
            cursor = mDB.query(table,columns,selection,selectionArgs,null,null,sortOrder);
        else
            Log.w(LOG_TAG, "query-> database is null");

        return cursor;
    }
    public synchronized long insertCustomerData(MainContractorData storyData)
    {
        long res;
        res = -1;
        if(mDB.isOpen())
        {
            try {
                ContentValues values = MainContractorData.getCVFromCustomerData(storyData);
                res = mDB.insertOrThrow(RDSDBHelper.STORY_TABLE_NAME,null,values);
                Log.i(LOG_TAG, "insertCustomerData new_index:" + res);
            }
            catch (SQLException e)
            {
                Log.e(LOG_TAG, "insertCustomerData failed::" + e.getMessage());
            }
        }
        else
        {
            Log.w(LOG_TAG, "db is not open");
        }
        return  res;
    }

    public synchronized void close()
    {
        if((null != mDB)
            && mDB.isOpen())
            mDB.close();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

}