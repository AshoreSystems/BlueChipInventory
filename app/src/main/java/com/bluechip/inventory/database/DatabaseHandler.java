package com.bluechip.inventory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aspl on 8/11/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "bluechip_inventory";

// temp string

    String AUDITOR_ID = "auditor_id";
    String JOB_ID = "job_id";
    String CUSTOMER_ID = "customer_id";

    // Table
    public static final String TABLE_AUDITOR_DETAILS = "table_bluechip_auditor";
    public static final String TABLE_CUSTOMER_DETAILS = "table_customer";
    public static final String TABLE_JOBS_DETAILS = "table_jobs";

    //Table Columns names

    public static final String KEY_ID = "key_id";


    //auditor_table
    public static final String KEY_AUDITOR_ID = "auditor_id";
    public static final String KEY_AUDITOR_EMAIL = "auditor_email";
    public static final String KEY_AUDITOR_ASSIGNED_JOB_ID = "job_id";


    //job_table
    public static final String KEY_JOB_ID = "job_id";
    public static final String KEY_JOB_AUDITOR_ID = "auditor_id";
    public static final String KEY_JOB_CUST_ID = "cust_id";
    public static final String KEY_JOB_ADDED_DATE = "job_added_date";
    public static final String KEY_JOB_ONE_AT = "job_one_at";
    public static final String KEY_AREA_ID = "area_id";
    public static final String KEY_AREA_NAME = "area_name";
    public static final String KEY_SUB_AREA_ID = "sub_area_id";
    public static final String KEY_SUB_AREA_NAME = "sub_area_name";
    public static final String KEY_SEC_ID = "sec_id";
    public static final String KEY_SEC_NAME = "sec_name";
    public static final String KEY_SUB_SECTION_ID = "sub_section_id";
    public static final String KEY_SUB_SECTION_NAME = "sub_section_name";
    public static final String KEY_LOCATION_ID = "location_id";
    public static final String KEY_LOCATION_ADDRESS = "location_address";
    public static final String KEY_FACILITY_NAME = "facility_name";

    //cutomer_table
    public static final String KEY_CUST_ID = "cust_id";
    public static final String KEY_CUST_UPDATED_DATE = "upload_date";


    //DYNAMIC TABLE
    //public static final StringKEY_CREATE IF NOT EXISTS="CREATE IF NOT EXISTS";

    //master_customer_id

    public static final String KEY_MASTER_PRD_ID = "prd_id";
    public static final String KEY_MASTER_PRD_SKU = "prd_SKU";
    public static final String KEY_MASTER_PRD_DESCRIPTION = "prd_description";
    public static final String KEY_MASTER_PRD_CATEGORY = "prd_category";
    public static final String KEY_MASTER_PRD_PRICE = "prd_price";


    //inventory_auditor_id_job_id

    public static final String KEY_PRD_ID = "prd_id";
    public static final String KEY_PRD_SKU = "prd_SKU";
    public static final String KEY_PRD_DESCRIPTION = "prd_description";
    public static final String KEY_PRD_CATEGORY = "prd_category";
    public static final String KEY_PRD_PRICE = "prd_price";
    public static final String KEY_PRD_QUANTITY = "prd_quantity";


    // Create Table
    String CREATE_AUDITOR_TABLE =
            "CREATE TABLE "
                    + TABLE_AUDITOR_DETAILS
                    + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_AUDITOR_ID + " INTEGER,"
                    + KEY_AUDITOR_EMAIL + " TEXT,"
                    + KEY_AUDITOR_ASSIGNED_JOB_ID + " TEXT"
                    + ")";

    String CREATE_CUSTOMER_TABLE =
            "CREATE TABLE "
                    + TABLE_CUSTOMER_DETAILS
                    + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_CUST_ID + " INTEGER,"
                    + KEY_CUST_UPDATED_DATE + " TEXT"
                    + ")";


    String CREATE_JOBS_TABLE =
            "CREATE TABLE "
                    + TABLE_JOBS_DETAILS
                    + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_JOB_ID + " INTEGER,"
                    + KEY_JOB_AUDITOR_ID + " INTEGER,"
                    + KEY_JOB_CUST_ID + " INTEGER,"
                    + KEY_JOB_ADDED_DATE + " TEXT,"
                    + KEY_JOB_ONE_AT + " TEXT,"
                    + KEY_AREA_ID + " TEXT,"
                    + KEY_AREA_NAME + " TEXT,"
                    + KEY_SUB_AREA_ID + " TEXT,"
                    + KEY_SUB_AREA_NAME + " TEXT,"
                    + KEY_SEC_ID + " TEXT,"
                    + KEY_SEC_NAME + " TEXT,"
                    + KEY_SUB_SECTION_ID + " TEXT,"
                    + KEY_SUB_SECTION_NAME + " TEXT,"
                    + KEY_LOCATION_ID + " TEXT,"
                    + KEY_LOCATION_ADDRESS + " TEXT,"
                    + KEY_FACILITY_NAME + " TEXT"
                    + ")";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_AUDITOR_TABLE);
        db.execSQL(CREATE_CUSTOMER_TABLE);
        db.execSQL(CREATE_JOBS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDITOR_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS_DETAILS);
        // Create tables again
        onCreate(db);

    }


    public SQLiteDatabase OpenWritable() {

        SQLiteDatabase db = this.getWritableDatabase();
        return db;
    }

    public SQLiteDatabase OpenReadable() {

        SQLiteDatabase db = this.getReadableDatabase();
        return db;
    }


    // Dynamic Table Creation
    public void createMasterTable(String table_name, SQLiteDatabase db) {

        // Master Table
        // SELECT name FROM sqlite_master WHERE type='table' AND name='table_name';


        String name = "table_master_costumer" + CUSTOMER_ID;


        String CREATE_TABLE_MASTER = "CREATE TABLE IF NOT EXISTS "
                + table_name
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_MASTER_PRD_ID + " INTEGER,"
                + KEY_MASTER_PRD_SKU + " TEXT,"
                + KEY_MASTER_PRD_DESCRIPTION + " TEXT,"
                + KEY_MASTER_PRD_CATEGORY + " TEXT,"
                + KEY_MASTER_PRD_PRICE + " TEXT"

                + ")";

        db.execSQL(CREATE_TABLE_MASTER);

        db.close();
    }

    public void createInventoryTable(String table_name, SQLiteDatabase db) {

        // Master Table

        // SELECT name FROM sqlite_master WHERE type='table' AND name='table_name';

        String name = "table_inventory_aud" + AUDITOR_ID + "_job" + JOB_ID;  // format


        String CREATE_TABLE_INVENTORY = "CREATE TABLE IF NOT EXISTS "
                + table_name
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_PRD_ID + " INTEGER,"
                + KEY_PRD_SKU + " TEXT,"
                + KEY_PRD_DESCRIPTION + " TEXT,"
                + KEY_PRD_CATEGORY + " TEXT,"
                + KEY_PRD_PRICE + " TEXT,"
                + KEY_PRD_QUANTITY + " INTEGER"

                + ")";
        db.execSQL(CREATE_TABLE_INVENTORY);
        db.close();

    }


}
