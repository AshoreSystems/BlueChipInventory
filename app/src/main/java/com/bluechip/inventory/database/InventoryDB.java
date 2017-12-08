package com.bluechip.inventory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bluechip.inventory.model.CustomerModel;
import com.bluechip.inventory.model.InventoryModel;
import com.bluechip.inventory.model.MasterInventoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aspl on 17/11/17.
 */

public class InventoryDB {

    String TABLE_NAME_INVENTORY;


    /////////////////////////////////////////////////////////////
    /////////////////// Customer Details ////////////////////////
    /////////////////////////////////////////////////////////////

    public void addCustomer(CustomerModel customer, Context context) {

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        int id = customer.getCustomer_id();

        ContentValues values = new ContentValues();


        if (getCustomerCount(db, id) < 1) {

            values.put(DatabaseHandler.KEY_CUST_ID, customer.getCustomer_id());
            values.put(DatabaseHandler.KEY_CUST_UPDATED_DATE, customer.getCustomer_updated_date());

            // Insert Row
            db.insert(DatabaseHandler.TABLE_CUSTOMER_DETAILS, null, values);

            int check_insert = getCustomerCount( db, customer.getCustomer_id());

            db.close(); // Closing database connection
        } else {


            values.put(DatabaseHandler.KEY_CUST_ID, customer.getCustomer_id());
            values.put(DatabaseHandler.KEY_CUST_UPDATED_DATE, customer.getCustomer_updated_date());


            int getCustomerCount = db.update(DatabaseHandler.TABLE_CUSTOMER_DETAILS, values,
                    (DatabaseHandler.KEY_CUST_ID + " = ? "), new String[]{String.valueOf(customer.getCustomer_id())});
            db.close();


        }


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
    public void addMasterInventory(String table_name, MasterInventoryModel inventory, Context context) {

        this.TABLE_NAME_INVENTORY = table_name;

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();


        int id = inventory.getPrd_id();


        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_MASTER_PRD_ID, inventory.getPrd_id());
        values.put(DatabaseHandler.KEY_MASTER_PRD_CATEGORY, inventory.getPrd_category());
        values.put(DatabaseHandler.KEY_MASTER_PRD_SKU, inventory.getPrd_sku());
        values.put(DatabaseHandler.KEY_MASTER_PRD_DESCRIPTION, inventory.getPrd_desc());
        values.put(DatabaseHandler.KEY_MASTER_PRD_PRICE, inventory.getPrd_price());


        // Insert Row
        db.insert(TABLE_NAME_INVENTORY, null, values);

        int check_insert = getMasterCount(TABLE_NAME_INVENTORY, db, inventory.getPrd_id());

        db.close(); // Closing database connection


    }


    //Get Total Count of Que for a Event
    public int getMasterCount(String TABLE_NAME_INVENTORY, SQLiteDatabase db, int  product_id) {


        String countQuery = "SELECT * FROM " + TABLE_NAME_INVENTORY
                + " WHERE "
                + DatabaseHandler.KEY_PRD_SKU + " ='" + product_id + "';";

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // get quantity

        return count;


    }

    // Master inventory details
    public InventoryModel getMasterInventoryDetails(String TABLE_NAME_INVENTORY, Context context, String product_id) {

        InventoryModel inventory = new InventoryModel();
        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + TABLE_NAME_INVENTORY
                + " WHERE "
                + DatabaseHandler.KEY_PRD_ID + " ='" + product_id + "' ;";

        Cursor cursor = db.rawQuery(detailsQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        inventory.setPrd_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_ID)));
        inventory.setPrd_sku(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_SKU)));
        inventory.setPrd_desc(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_DESCRIPTION)));
        inventory.setPrd_category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_CATEGORY)));
        inventory.setPrd_price(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_PRICE)));


        return inventory;

    }


    // Master inventory details
    public List<MasterInventoryModel> getMasterList(String TABLE_NAME_MASTER, Context context) {

        List<MasterInventoryModel> inventoryList = new ArrayList<MasterInventoryModel>();

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + TABLE_NAME_MASTER+ " ;";

        Cursor cursor = db.rawQuery(detailsQuery, null);

        if (cursor.moveToFirst()) {

            do {

                MasterInventoryModel inventory = new MasterInventoryModel();

                inventory.setPrd_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_ID)));
                inventory.setPrd_sku(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_SKU)));
                inventory.setPrd_desc(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_DESCRIPTION)));
                inventory.setPrd_category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_CATEGORY)));
                inventory.setPrd_price(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_MASTER_PRD_PRICE)));

                inventoryList.add(inventory);
            } while (cursor.moveToNext());

        }
        return inventoryList;

    }


    /////////////////////////////////////////////////////////
    ///////////////LOCAL INVENTORY //////////////////////////
    /////////////////////////////////////////////////////////


    public void addInventory(String table_name, InventoryModel inventory, Context context) {

        this.TABLE_NAME_INVENTORY = table_name;

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();


        String  id = inventory.getPrd_sku();
        ContentValues values = new ContentValues();

        if (getInventoryCount(TABLE_NAME_INVENTORY, db, id) < 1) {




            values.put(DatabaseHandler.KEY_PRD_ID, inventory.getPrd_id());
            values.put(DatabaseHandler.KEY_PRD_CATEGORY, inventory.getPrd_category());
            values.put(DatabaseHandler.KEY_PRD_SKU, inventory.getPrd_sku());
            values.put(DatabaseHandler.KEY_PRD_DESCRIPTION, inventory.getPrd_desc());
            values.put(DatabaseHandler.KEY_PRD_PRICE, inventory.getPrd_price());
            values.put(DatabaseHandler.KEY_PRD_QUANTITY, inventory.getPrd_quantity());


            // Insert Row
            db.insert(TABLE_NAME_INVENTORY, null, values);

            int check_insert = getInventoryCount(TABLE_NAME_INVENTORY, db, inventory.getPrd_sku());

            db.close(); // Closing database connection
        } else {




            values.put(DatabaseHandler.KEY_PRD_ID, inventory.getPrd_id());
            values.put(DatabaseHandler.KEY_PRD_SKU, inventory.getPrd_sku());
            values.put(DatabaseHandler.KEY_PRD_DESCRIPTION, inventory.getPrd_desc());
            values.put(DatabaseHandler.KEY_PRD_CATEGORY, inventory.getPrd_category());
            values.put(DatabaseHandler.KEY_PRD_PRICE, inventory.getPrd_price());
            values.put(DatabaseHandler.KEY_PRD_QUANTITY, inventory.getPrd_quantity());


            int check_insert = db.update(TABLE_NAME_INVENTORY, values,
                    (DatabaseHandler.KEY_PRD_SKU + " = ? "), new String[]{String.valueOf(inventory.getPrd_sku())});
            db.close();


        }


    }

    //Get Total Count of Que for a Event
    public int getInventoryCount(String TABLE_NAME_INVENTORY, SQLiteDatabase db, String  product_id) {


        String countQuery = "SELECT * FROM " + TABLE_NAME_INVENTORY
                + " WHERE "
                + DatabaseHandler.KEY_PRD_SKU + " ='" + product_id + "';";

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // get quantity

        return count;


    }

    // inventory details
    public InventoryModel getInventoryDetails(String TABLE_NAME_INVENTORY, Context context, String product_id) {

        InventoryModel inventory = new InventoryModel();
        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + TABLE_NAME_INVENTORY
                + " WHERE "
                + DatabaseHandler.KEY_PRD_ID + " ='" + product_id + "' ;";

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

        List<InventoryModel> inventoryList = new ArrayList<InventoryModel>();;

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + TABLE_NAME_INVENTORY + " ;";

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
        return inventoryList;

    }

}