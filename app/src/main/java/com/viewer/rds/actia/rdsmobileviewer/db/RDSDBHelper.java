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

    public final static String PRIMARY_CUSTOMER_ID = "_id";
    public final static String PRIMARY_VEHCILE_ID = "_id";

    Context mContext;
    //* MainContractor Columns Name *//
    public final static String NAME = "FriendlyName";
    public final static String ANCODICE = "AnCodice";
    public final static String ID_CUSTOMER = "IdCustomer";
    public final static String INSERT_DATE = "InsertDate";
    public final static String EMAIL = "Email";
    public final static String AUTO_DRIVER = "AutoDriverEnabled";
    public final static String EMAIL_FORWARD = "EmailForwardEnabled";
    public final static String SUPER_CRDS = "SuperCRDSEnabled";
    public final static String FILE_PUSH = "FilePushingEnabled";

    //* Vehicle Columns Name *//
    public final static String CUSTOMER_NAME = "CUSTOMER_NAME";
    public final static String DIAG_TIME = "DIAG_TIME";
    public final static String FILE_CONTENT = "FILE_CONTENT";
    public final static String IMEI = "IMEI";
    public final static String ID_DEVICE = "ID_DEVICE";
    public final static String JOURNEY_ENBL_DATE = "JOURNEY_ENBL_DATE";
    public final static String PHONE_NUMBER = "PHONE_NUMBER";
    public final static String START_DATE = "START_DATE";
    public final static String STATUS = "STATUS";
    public final static String VIN = "VIN";
    public final static String VRN = "VRN";
    public final static String SW_VERSION = "SW_VERSION";
    public final static String SW_NAME = "SW_NAME";
    public final static String FOREIGN_CUSTOMER_ID = "FOREIGN_CUSTOMER_ID";


    final static String CUSTOMERS_TABLE = "tb_customers";
    final static String VEHICLES_ASSOCIATED_TABLE = "tb_vehicles_associated";
    final static String[] columnsMainContractor = {PRIMARY_CUSTOMER_ID, NAME, ANCODICE,ID_CUSTOMER,INSERT_DATE,EMAIL,AUTO_DRIVER,EMAIL_FORWARD,SUPER_CRDS,FILE_PUSH};
    final static String[] columnsVehicleAssociated = {PRIMARY_CUSTOMER_ID, DIAG_TIME, FILE_CONTENT,IMEI,ID_DEVICE,JOURNEY_ENBL_DATE,
            PHONE_NUMBER,START_DATE,STATUS,VIN,VRN,SW_VERSION,SW_NAME,FOREIGN_CUSTOMER_ID};

    private static final String DATABASE_CREATE_CUSTOMERS = "create table "
            + CUSTOMERS_TABLE + " (" // start table
            + PRIMARY_CUSTOMER_ID + " integer primary key autoincrement, " // setup
            // auto-inc.
            + NAME + " TEXT ," //
            + ANCODICE + " TEXT unique," //
            + ID_CUSTOMER + " TEXT ," //
            + AUTO_DRIVER + " TEXT ," //
            + EMAIL_FORWARD + " TEXT ," //
            + SUPER_CRDS + " TEXT ," //
            + FILE_PUSH + " TEXT ," //
            + EMAIL + " TEXT ," //
            + INSERT_DATE + " TEXT " //
            + " );"; // end table

    private static final String DATABASE_CREATE_VEHICLES_ASSOCIATED = "create table "
            + VEHICLES_ASSOCIATED_TABLE + " (" // start table
            + PRIMARY_VEHCILE_ID + " integer primary key autoincrement, " // setup
            // auto-inc.
            + CUSTOMER_NAME + " TEXT ," //
            + VIN + " TEXT unique," //
            + VRN + " TEXT ," //
            + DIAG_TIME + " TEXT ," //
            + FILE_CONTENT + " TEXT ," //
            + IMEI + " TEXT ," //
            + ID_DEVICE + " TEXT ," //
            + EMAIL + " TEXT ," //
            + JOURNEY_ENBL_DATE + " TEXT ," //
            + PHONE_NUMBER + " TEXT ," //
            + START_DATE + " TEXT ," //
            + STATUS + " TEXT ," //
            + SW_VERSION + " TEXT ," //
            + SW_NAME + " TEXT ," //
            + FOREIGN_CUSTOMER_ID + " integer ," //
            + "FOREIGN KEY(" + PRIMARY_VEHCILE_ID + ")" + " REFERENCES " + CUSTOMERS_TABLE + "(" + PRIMARY_CUSTOMER_ID + ")"
            + " );"; // end table


    final static String DATABASE_NAME = "rds_mobileviewer_db";
    final static  int DATABASE_VERSION = 2;

    public RDSDBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "db onCreate");
        db.execSQL(DATABASE_CREATE_CUSTOMERS);
        db.execSQL(DATABASE_CREATE_VEHICLES_ASSOCIATED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Log version upgrade.
        Log.i(LOG_TAG + "DBHelper", "Upgrading from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");

        // **** Upgrade DB ****
        // drop old DB

        // ST:dropTableIfExists:start
        db.execSQL("DROP TABLE IF EXISTS " + CUSTOMERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VEHICLES_ASSOCIATED_TABLE);
        //db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_TAGS);
        // ST:dropTableIfExists:finish

        // Create a new one.
        onCreate(db);
    }

    public boolean deleteDataBase()
    {
       return mContext.deleteDatabase(DATABASE_NAME);
    }
}
