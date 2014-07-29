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
        if(mRDSDBMapper.getContext() == null)
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
        Cursor cursor = query(RDSDBHelper.CUSTOMERS_TABLE,RDSDBHelper.columns,null, new String[]{},null);
        ArrayList<MainContractorData> res = new ArrayList<MainContractorData>();
        ContentValues values = new ContentValues();
        while (cursor.moveToNext()) {
            values.put(RDSDBHelper.ANCODICE, cursor.getString(cursor.getColumnIndex(RDSDBHelper.ANCODICE)));
            values.put(RDSDBHelper.AUTO_DRIVER, cursor.getString(cursor.getColumnIndex(RDSDBHelper.AUTO_DRIVER)));
            values.put(RDSDBHelper.EMAIL, cursor.getString(cursor.getColumnIndex(RDSDBHelper.EMAIL)));
            values.put(RDSDBHelper.EMAIL_FORWARD, cursor.getString(cursor.getColumnIndex(RDSDBHelper.EMAIL_FORWARD)));
            values.put(RDSDBHelper.FILE_PUSH, cursor.getString(cursor.getColumnIndex(RDSDBHelper.FILE_PUSH)));
            values.put(RDSDBHelper.ID_CUSTOMER, cursor.getString(cursor.getColumnIndex(RDSDBHelper.ID_CUSTOMER)));
            values.put(RDSDBHelper.INSERT_DATE, cursor.getString(cursor.getColumnIndex(RDSDBHelper.INSERT_DATE)));
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
    public synchronized long insertOrUpdateCustomerData(MainContractorData customer)
    {
        long res;
        res = -1;
        if(mDB.isOpen())
        {
            try {
                ContentValues values = MainContractorData.getCVFromCustomerData(customer);
                /*
                if(!this.isExistCustomer(customer.getAncodice()))
                    res = mDB.insertOrThrow(RDSDBHelper.CUSTOMERS_TABLE, null, values);
                else
                    res = mDB.update(RDSDBHelper.CUSTOMERS_TABLE,values,null,null);
                    */
                int id = (int) mDB.insertWithOnConflict(RDSDBHelper.CUSTOMERS_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                    mDB.update(RDSDBHelper.CUSTOMERS_TABLE, values, "AnCodice=?", new String[] {String.valueOf(customer.getAncodice())});
                }
                Log.i(LOG_TAG, "insertOrUpdateCustomerData new_index:" + res);
            }
            catch (SQLException e)
            {
                Log.e(LOG_TAG, "insertOrUpdateCustomerData failed::" + e.getMessage());
            }
        }
        else
        {
            Log.w(LOG_TAG, "db is not open");
        }
        return  res;
    }

    public boolean isExistCustomer(String customerAnCodice) {
        Cursor c = mDB.rawQuery("SELECT 1 FROM "+
                RDSDBHelper.CUSTOMERS_TABLE
                +" WHERE "+ RDSDBHelper.ANCODICE +"=?", new String[] {customerAnCodice});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public synchronized void close()
    {
        if((null != mDB)
            && mDB.isOpen())
            mDB.close();
    }


    public synchronized void deleteDatabase()
    {
        if((null != mDB)
                && mDB.isOpen())
            mDBHelper.deleteDataBase();
    }

    public synchronized void deleteAllCustomers()
    {
        if((null != mDB)
                && mDB.isOpen())
            mDB.delete(RDSDBHelper.CUSTOMERS_TABLE,null,null);
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

}
