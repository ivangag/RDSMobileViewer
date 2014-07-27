package com.viewer.rds.actia.rdsmobileviewer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by igaglioti on 03/04/14.
 */
public class RDSDBHelper extends SQLiteOpenHelper {

    final String LOG_TAG = RDSDBHelper.class.getCanonicalName();

    Context mContext;
    //* Columns Name *//
    public final static String ID = "_id";
    public final static String NAME = "FriendlyName";
    public final static String ANCODICE = "AnCodice";
    public final static String ID_CUSTOMER = "IdCustomer";
    public final static String INSERT_DATE = "InsertDate";
    public final static String EMAIL = "Email";
    public final static String AUTO_DRIVER = "AutoDriverEnabled";
    public final static String EMAIL_FORWARD = "EmailForwardEnabled";
    public final static String SUPER_CRDS = "SuperCRDSEnabled";
    public final static String FILE_PUSH = "FilePushingEnabled";


    final static String CUSTOMERS_TABLE = "tb_customers";
    final static String[] columns = { ID, NAME, ANCODICE,ID_CUSTOMER,INSERT_DATE,EMAIL,AUTO_DRIVER,EMAIL_FORWARD,SUPER_CRDS,FILE_PUSH};

    private static final String DATABASE_CREATE_CUSTOMERS = "create table "
            + CUSTOMERS_TABLE + " (" // start table
            + ID + " integer primary key autoincrement, " // setup
            // auto-inc.
            + NAME + " TEXT ," //
            + ANCODICE + " TEXT ," //
            + ID_CUSTOMER + " TEXT ," //
            + AUTO_DRIVER + " TEXT ," //
            + EMAIL_FORWARD + " TEXT ," //
            + SUPER_CRDS + " TEXT ," //
            + FILE_PUSH + " TEXT ," //
            + EMAIL + " TEXT ," //
            + INSERT_DATE + " TEXT " //
            + " );"; // end table


    final static String DATABASE_NAME = "rds_mobileviewer_db";
    final static  int DATABASE_VERSION = 1;

    public RDSDBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_CUSTOMERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Log version upgrade.
        Log.w(LOG_TAG + "DBHelper", "Upgrading from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");

        // **** Upgrade DB ****
        // drop old DB

        // ST:dropTableIfExists:start
        db.execSQL("DROP TABLE IF EXISTS " + CUSTOMERS_TABLE);
        //db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_TAGS);
        // ST:dropTableIfExists:finish

        // Create a new one.
        onCreate(db);
    }

    protected boolean deleteDataBase()
    {
       return mContext.deleteDatabase(DATABASE_NAME);
    }
}
