package com.viewer.rds.actia.rdsmobileviewer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.viewer.rds.actia.rdsmobileviewer.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.MainContractorData;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;
import com.viewer.rds.actia.rdsmobileviewer.utils.Utils;

import java.util.ArrayList;
import java.util.UUID;

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

    public synchronized String saveDownloadToRepository(DownloadRequestSchema downloadRequest, String jsonStream){
        String uuid_res = "";
        int res = -1;
        if(mDB.isOpen()){
                try {
                    String TABLE = RDSDBHelper.REPOSITORY_DOWNLOAD_TRUSTED_TABLE;
                    String uuid = UUID.randomUUID().toString();
                    ContentValues values = new ContentValues();
                    values.put(RDSDBHelper.CONTENT_DWNLD,jsonStream.getBytes());
                    values.put(RDSDBHelper.UUID_DWNLD,uuid);
                    values.put(RDSDBHelper.CONTENT_DWNLD_TYPE,downloadRequest.getDownloadRequestType().toString());
                    values.put(RDSDBHelper.ANCODICE,downloadRequest.getUniqueCustomerCode());
                    values.put(RDSDBHelper.VEHICLED_ID,downloadRequest.getVehicleVIN());
                    values.put(RDSDBHelper.DRIVER_ID,"TBD");
                    values.put(RDSDBHelper.CRDS_ID,"TBD");

                    int isAlreadyPrimaryIdExistDownload = checkDownloadDataRepoPresence(downloadRequest);

                    if(isAlreadyPrimaryIdExistDownload == -1) {
                        res = (int) mDB.insertWithOnConflict(TABLE,
                                null, values, SQLiteDatabase.CONFLICT_IGNORE);
                    }else{
                        final String whereClause = makeClauseWhereUniqueDownloadRepo(downloadRequest,false);
                        ContentValues cvUpdate = new ContentValues();
                        cvUpdate.put(RDSDBHelper.CONTENT_DWNLD,jsonStream.getBytes());
                        res = mDB.update(TABLE,cvUpdate,whereClause,null);
                        //res = updateDownloadRepo(downloadRequest, jsonStream.getBytes());
                    }
                    if(res != -1)
                        uuid_res = uuid;

                    Log.i(LOG_TAG, "saveDownloadToRepository new_index:" + res + "uuid_res:" + uuid_res);
                } catch (SQLException e) {
                    Log.e(LOG_TAG, "saveDownloadToRepository failed::" + e.getMessage());
                }
        }else {
            Log.w(LOG_TAG, "db is not open");
        }
        return  uuid_res;
    }

    public int updateDownloadRepo(DownloadRequestSchema downloadRequest, byte[] jsonStream) {
        int res = -1;
        final String whereClause = makeClauseWhereUniqueDownloadRepo(downloadRequest,true);
        final String update_query = "UPDATE "
                + RDSDBHelper.REPOSITORY_DOWNLOAD_TRUSTED_TABLE
                + " SET " + RDSDBHelper.CONTENT_DWNLD + " = " + jsonStream
                + " " + whereClause;
        Cursor cursor = mDB.rawQuery(update_query,null);
        if(cursor.moveToFirst())
        {
            res = cursor.getInt(cursor.getColumnIndex(RDSDBHelper.PRIMARY_ID));
        }
        return res;
    }

    private int checkDownloadDataRepoPresence(DownloadRequestSchema downloadRequest) {

        int res = - 1;
        String whereUniqueClause = makeClauseWhereUniqueDownloadRepo(downloadRequest,true);

        Cursor cursor = mDB.rawQuery("SELECT * FROM "+
                RDSDBHelper.REPOSITORY_DOWNLOAD_TRUSTED_TABLE + " " + whereUniqueClause,null);

        if(cursor.moveToFirst())
        {
            res = cursor.getInt(cursor.getColumnIndex(RDSDBHelper.PRIMARY_ID));
        }
        return res;
    }

    private String makeClauseWhereUniqueDownloadRepo(DownloadRequestSchema downloadRequest,boolean insertWhereKey) {
        String whereUniqueClause = "";
        switch (downloadRequest.getDownloadRequestType()){
            case VEHICLE_NOT_TRUSTED:
            case CRDS_NOT_TRUSTED:
            case DRIVERS_NOT_TRUSTED:
            case CUSTOMERS_LIST:
                whereUniqueClause = (insertWhereKey ? "WHERE " : " ") + RDSDBHelper.CONTENT_DWNLD_TYPE + " = '" + downloadRequest.getDownloadRequestType().toString() + "'";
                break;
            case VEHICLES_OWNED:
            case DRIVERS_OWNED:
            case CRDS_OWNED:
                whereUniqueClause = (insertWhereKey ? "WHERE " : " ") + RDSDBHelper.CONTENT_DWNLD_TYPE + " = '" +  downloadRequest.getDownloadRequestType().toString() + "'"
                        + " AND " + RDSDBHelper.ANCODICE + " = '" + downloadRequest.getUniqueCustomerCode() + "'";
                break;
            case VEHICLE_DIAGNOSTIC:
                whereUniqueClause = (insertWhereKey ? "WHERE " : " ") + RDSDBHelper.CONTENT_DWNLD_TYPE + " = '" + downloadRequest.getDownloadRequestType().toString() + "'"
                        + " AND " + RDSDBHelper.VEHICLED_ID + " = '" + downloadRequest.getVehicleVIN() + "'";
                break;
        }
        return whereUniqueClause;
    }

    private synchronized boolean isExistCustomer(String customerAnCodice) {
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
    public String getDownloadRepository(String uuid) {
        int res = -1;
        byte[] streamData = new byte[]{0x00};
        Cursor cursor = mDB.rawQuery("SELECT * FROM "+
                RDSDBHelper.REPOSITORY_DOWNLOAD_NOT_TRUSTED_TABLE
                +" WHERE "+ RDSDBHelper.UUID_DWNLD +"=?", new String[] {uuid});
        if(cursor.moveToFirst())
        {
            streamData = cursor.getBlob(cursor.getColumnIndex(RDSDBHelper.CONTENT_DWNLD));
        }
        cursor.close();
        String content = new String(streamData);
        return content;
    }

    /*
    * Checks if customer exists
    * returns unique primary key id
     */
    private int getExistingCustomer(String customerAnCodice) {
        int res = -1;
        Cursor cursor = mDB.rawQuery("SELECT * FROM "+
                RDSDBHelper.CUSTOMERS_TABLE
                +" WHERE "+ RDSDBHelper.ANCODICE +"=?", new String[] {customerAnCodice});
        if(cursor.moveToFirst())
        {
            res = cursor.getInt(cursor.getColumnIndex(RDSDBHelper.PRIMARY_ID));
        }
        cursor.close();
        return res;
    }


    public String getDownloadRepoTable(DownloadRequestSchema downloadRequest) {
        String TABLE_TO_USE = "";
        switch (downloadRequest.getDownloadRequestType()){

            case VEHICLE_NOT_TRUSTED:
            case CRDS_NOT_TRUSTED:
            case DRIVERS_NOT_TRUSTED:
            case CUSTOMERS_LIST:
                TABLE_TO_USE = RDSDBHelper.REPOSITORY_DOWNLOAD_NOT_TRUSTED_TABLE;
                break;
            case VEHICLES_OWNED:
            case DRIVERS_OWNED:
            case CRDS_OWNED:
            case VEHICLE_DIAGNOSTIC:
                TABLE_TO_USE = RDSDBHelper.REPOSITORY_DOWNLOAD_TRUSTED_TABLE;
                break;
        }
        return TABLE_TO_USE;
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
