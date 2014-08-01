package com.viewer.rds.actia.rdsmobileviewer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;

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
        Cursor cursor = query(RDSDBHelper.CUSTOMERS_TABLE,RDSDBHelper.columnsMainContractor,null, new String[]{},null);
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
                res = (int) mDB.insertWithOnConflict(RDSDBHelper.CUSTOMERS_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (res == -1) {
                    res = mDB.update(RDSDBHelper.CUSTOMERS_TABLE, values, "AnCodice=?", new String[] {String.valueOf(customer.getAncodice())});
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

    public synchronized long insertOrUpdateVehicleData(VehicleCustom vehicle, String CustomerAnCodice, boolean isAssociated)
    {
        long res;
        res = -1;
        String TABLE = "";
        int primary_customer_key = -1;
        if(mDB.isOpen())
        {

            if(isAssociated)
                TABLE = RDSDBHelper.VEHICLES_ASSOCIATED_TABLE;

            if((primary_customer_key = getExistingCustomer(CustomerAnCodice)) != -1) {
                try {
                    ContentValues values = VehicleCustom.getCVFromData(vehicle);
                    values.put(RDSDBHelper.FOREIGN_CUSTOMER_ID,primary_customer_key);
                    res = (int) mDB.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                    if (res == -1) {
                        res = mDB.update(TABLE, values, "VIN=?", new String[]{String.valueOf(vehicle.getVIN())});
                    }
                    Log.i(LOG_TAG, "insertOrUpdateVehicleData new_index:" + res);
                } catch (SQLException e) {
                    Log.e(LOG_TAG, "insertOrUpdateVehicleData failed::" + e.getMessage());
                }
            }
        }
        else
        {
            Log.w(LOG_TAG, "db is not open");
        }
        return  res;
    }

    public synchronized boolean isExistCustomer(String customerAnCodice) {
        Cursor c = mDB.rawQuery("SELECT 1 FROM "+
                RDSDBHelper.CUSTOMERS_TABLE
                +" WHERE "+ RDSDBHelper.ANCODICE +"=?", new String[] {customerAnCodice});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    /*
    * Checks if customer exists
    * returns unique primary key id
     */
    public int getExistingCustomer(String customerAnCodice) {
        int res = -1;
        Cursor cursor = mDB.rawQuery("SELECT * FROM "+
                RDSDBHelper.CUSTOMERS_TABLE
                +" WHERE "+ RDSDBHelper.ANCODICE +"=?", new String[] {customerAnCodice});
        if(cursor.moveToFirst())
        {
            res = cursor.getInt(cursor.getColumnIndex(RDSDBHelper.PRIMARY_CUSTOMER_ID));
        }
        cursor.close();
        return res;
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

    public synchronized void deleteTable(String tableName)
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
