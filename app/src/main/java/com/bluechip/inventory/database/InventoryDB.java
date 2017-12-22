package com.bluechip.inventory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bluechip.inventory.model.CustomerModel;
import com.bluechip.inventory.model.InventoryModel;
import com.bluechip.inventory.model.MasterInventoryModel;
import com.bluechip.inventory.utilities.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aspl on 17/11/17.
 */

public class InventoryDB {


    /////////////////////////////////////////////////////////////
    /////////////////// Customer Details ////////////////////////
    /////////////////////////////////////////////////////////////

    public void addCustomer(CustomerModel customer, Context context) {

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        int id = customer.getCustomer_id();

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_CUST_ID, customer.getCustomer_id());
        values.put(DatabaseHandler.KEY_CUST_UPDATED_DATE, customer.getCustomer_updated_date());

        if (getCustomerCount(db, id) < 1) {

            // Insert Row
            db.insert(DatabaseHandler.TABLE_CUSTOMER_DETAILS, null, values);
            int check_insert = getCustomerCount(db, customer.getCustomer_id());

        } else {
            int getCustomerCount = db.update(DatabaseHandler.TABLE_CUSTOMER_DETAILS, values,
                    (DatabaseHandler.KEY_CUST_ID + " = ? "), new String[]{String.valueOf(customer.getCustomer_id())});

        }

        db.close(); // Closing database connection
    }

    private int getCustomerCount(SQLiteDatabase db, int customer_id) {

        String select_query = "SELECT * FROM " + DatabaseHandler.TABLE_CUSTOMER_DETAILS
                + " WHERE "
                + DatabaseHandler.KEY_CUST_ID + " ='" + customer_id
                + "';";
        Cursor cursor = db.rawQuery(select_query, null);

        return cursor.getCount();
    }


    /////////////////////////////////////////////////////////////
    /////////////////// Master Inventory ////////////////////////
    /////////////////////////////////////////////////////////////

    // add inventory in master
    public void addMasterInventory(String TABLE_NAME_MASTER, MasterInventoryModel inventory, Context context) {


        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();


        int id = inventory.getPrd_id();


        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_MASTER_PRD_ID, inventory.getPrd_id());
        values.put(DatabaseHandler.KEY_MASTER_PRD_CATEGORY, inventory.getPrd_category());
        values.put(DatabaseHandler.KEY_MASTER_PRD_SKU, inventory.getPrd_sku());
        values.put(DatabaseHandler.KEY_MASTER_PRD_DESCRIPTION, inventory.getPrd_desc());
        values.put(DatabaseHandler.KEY_MASTER_PRD_PRICE, inventory.getPrd_price());

        String price = inventory.getPrd_price();

        // Insert Row
        db.insert(TABLE_NAME_MASTER, null, values);

        int check_insert = getMasterCount(TABLE_NAME_MASTER, db, inventory.getPrd_id());

        db.close(); // Closing database connection

    }


    //Get  Count
    public int getMasterCount(String TABLE_NAME_MASTER, SQLiteDatabase db, int product_id) {


        String countQuery = "SELECT * FROM " + TABLE_NAME_MASTER
                + " WHERE "
                + DatabaseHandler.KEY_PRD_ID + " ='" + product_id + "';";

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // get quantity

        return count;


    }

    // Master inventory details
    public MasterInventoryModel getMasterInventoryDetails(String TABLE_NAME_MASTER, Context context, String product_sku) {









        MasterInventoryModel inventory = new MasterInventoryModel();
        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + TABLE_NAME_MASTER
                + " WHERE "
                + DatabaseHandler.KEY_MASTER_PRD_SKU + " ='" + product_sku + "' ;";

        Cursor cursor = db.rawQuery(detailsQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        inventory.setPrd_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_ID)));
        inventory.setPrd_sku(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_SKU)));
        inventory.setPrd_desc(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_DESCRIPTION)));
        inventory.setPrd_category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_CATEGORY)));
        inventory.setPrd_price(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_PRICE)));


        return inventory;

    }


    // Master inventory details
    public List<MasterInventoryModel> getMasterList(String TABLE_NAME_MASTER, Context context) {

        List<MasterInventoryModel> inventoryList = new ArrayList<MasterInventoryModel>();

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + TABLE_NAME_MASTER
                + " ORDER BY "
                + DatabaseHandler.KEY_MASTER_PRD_CATEGORY
                + " COLLATE NOCASE ASC "
                + " ;";

        Cursor cursor = db.rawQuery(detailsQuery, null);

        if (cursor.moveToFirst()) {

            do {

                MasterInventoryModel inventory = new MasterInventoryModel();

                inventory.setPrd_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_ID)));
                inventory.setPrd_sku(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_SKU)));
                inventory.setPrd_desc(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_DESCRIPTION)));
                inventory.setPrd_category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_CATEGORY)));
                inventory.setPrd_price(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_PRICE)));

                inventoryList.add(inventory);
            } while (cursor.moveToNext());

        }
        return inventoryList;

    }


    /////////////////////////////////////////////////////////
    ///////////////LOCAL INVENTORY //////////////////////////
    /////////////////////////////////////////////////////////


    public void addInventory(String TABLE_NAME_INVENTORY, InventoryModel inventory, Context context) {


        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();


        String id = inventory.getPrd_sku();
        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_PRD_ID, inventory.getPrd_id());
        values.put(DatabaseHandler.KEY_PRD_CATEGORY, inventory.getPrd_category());
        values.put(DatabaseHandler.KEY_PRD_SKU, inventory.getPrd_sku());
        values.put(DatabaseHandler.KEY_PRD_DESCRIPTION, inventory.getPrd_desc());
        values.put(DatabaseHandler.KEY_PRD_PRICE, inventory.getPrd_price());
        values.put(DatabaseHandler.KEY_PRD_QUANTITY, inventory.getPrd_quantity());

        if (getInventoryCount(TABLE_NAME_INVENTORY, db, id) < 1) {

            // Insert Row
            db.insert(TABLE_NAME_INVENTORY, null, values);
            int check_insert = getInventoryCount(TABLE_NAME_INVENTORY, db, inventory.getPrd_sku());
            db.close(); // Closing database connection
        } else {
            int check_insert = db.update(TABLE_NAME_INVENTORY, values,
                    (DatabaseHandler.KEY_PRD_SKU + " = ? "), new String[]{String.valueOf(inventory.getPrd_sku())});
            db.close();


        }


    }

    //Get  Count
    public int getInventoryCount(String TABLE_NAME_INVENTORY, SQLiteDatabase db, String product_sku) {


        String countQuery = "SELECT * FROM " + TABLE_NAME_INVENTORY
                + " WHERE "
                + DatabaseHandler.KEY_PRD_SKU + " ='" + product_sku + "';";

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // get quantity

        return count;


    } //Get Total Count of inventory

    public int getTotalInventoryCount(String TABLE_NAME_INVENTORY, Context context) {


        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();


        String countQuery = "SELECT * FROM " + TABLE_NAME_INVENTORY
                + " ;";


        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // get quantity

        db.close();
        return count;


    }

    // inventory details
    public InventoryModel getInventoryDetails(String TABLE_NAME_INVENTORY, Context context, String product_sku) {

        InventoryModel inventory = new InventoryModel();
        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + TABLE_NAME_INVENTORY
                + " WHERE "
                + DatabaseHandler.KEY_PRD_SKU + " ='" + product_sku+ "' ;";

        Cursor cursor = db.rawQuery(detailsQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        inventory.setPrd_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_ID)));
        inventory.setPrd_sku(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_SKU)));
        inventory.setPrd_desc(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_DESCRIPTION)));
        inventory.setPrd_category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_CATEGORY)));
        inventory.setPrd_price(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_PRICE)));
        inventory.setPrd_quantity(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_QUANTITY)));


        return inventory;

    }


    // display inventory for job
    public List<InventoryModel> getInventoryList(String TABLE_NAME_INVENTORY, Context context) {

        List<InventoryModel> inventoryList = new ArrayList<InventoryModel>();


        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + TABLE_NAME_INVENTORY
                + " ORDER BY "
                + DatabaseHandler.KEY_PRD_DESCRIPTION
                + " COLLATE NOCASE ASC "
                + " ;";

        Cursor cursor = db.rawQuery(detailsQuery, null);

        if (cursor.moveToFirst()) {

            do {

                InventoryModel inventory = new InventoryModel();

                inventory.setPrd_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_ID)));
                inventory.setPrd_sku(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_SKU)));
                inventory.setPrd_desc(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_DESCRIPTION)));
                inventory.setPrd_category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_CATEGORY)));
                inventory.setPrd_price(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_PRICE)));
                inventory.setPrd_quantity(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PRD_QUANTITY)));

                inventoryList.add(inventory);
            } while (cursor.moveToNext());

        }
        db.close();
        return inventoryList;

    }


    // Check Table
    public boolean isMasterTablePresent(String table_name, Context context) {

        boolean table_present = false;
        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();


        String check_query = "SELECT name FROM sqlite_master WHERE type='table' AND name='"
                + table_name
                + "';";
        Cursor cursor = db.rawQuery(check_query, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        if (count > 0) {
            table_present = true;
        }
        db.close();
        return table_present;

    }

    public boolean isMasterUpdated(int customer_id, String temp_upload_date, Context context) {

        boolean is_updated = false;
        CustomerModel customerModel = new CustomerModel();

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String select_query = "SELECT * FROM " + DatabaseHandler.TABLE_CUSTOMER_DETAILS
                + " WHERE "
                + DatabaseHandler.KEY_CUST_ID + " ='" + customer_id
                + "';";
        Cursor cursor = db.rawQuery(select_query, null);


        if (cursor != null)
            cursor.moveToFirst();

        customerModel.setCustomer_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_CUST_ID)));
        customerModel.setCustomer_updated_date(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CUST_UPDATED_DATE)));


        is_updated = new Tools().isGreaterOrEqualDate(customerModel.getCustomer_updated_date(), temp_upload_date);

        cursor.close();
        db.close();

        return is_updated;
    }
}
