package com.bluechip.inventory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bluechip.inventory.model.AuditorModel;

/**
 * Created by aspl on 7/12/17.
 */

public class AuditorDB {



    public void addAuditor(AuditorModel auditorModel, Context context) {

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        int id = auditorModel.getAuditor_id();

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_AUDITOR_ID, auditorModel.getAuditor_id());
        values.put(DatabaseHandler.KEY_AUDITOR_EMAIL, auditorModel.getAuditor_email());
        values.put(DatabaseHandler.KEY_AUDITOR_ASSIGNED_JOB_ID, auditorModel.getAuditor_assigned_jobs());


        if (getAuditorCount(db, id) < 1) {

            // Insert Row
            db.insert(DatabaseHandler.TABLE_AUDITOR_DETAILS, null, values);
            int check_insert = getAuditorCount( db, auditorModel.getAuditor_id());
            db.close(); // Closing database connection
        } else {
              int getCustomerCount = db.update(DatabaseHandler.TABLE_AUDITOR_DETAILS, values,
                    (DatabaseHandler.KEY_AUDITOR_ID+ " = ? "), new String[]{String.valueOf(auditorModel.getAuditor_id())});
            db.close();
        }
    }



    private int getAuditorCount(SQLiteDatabase db, int auditor_id) {

        String select_query = "SELECT * FROM " + DatabaseHandler.TABLE_AUDITOR_DETAILS
                + " WHERE "
                + DatabaseHandler.KEY_AUDITOR_ID+ " = " + auditor_id
                + ";";
        Cursor cursor = db.rawQuery(select_query, null);

        int count= cursor.getCount();

        return count;
    }





    //  auditor details
    public AuditorModel getAuditorDetails( Context context, String audito_id) {

        AuditorModel auditor = new AuditorModel();
        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + DatabaseHandler.TABLE_AUDITOR_DETAILS
                + " WHERE "
                + DatabaseHandler.KEY_AUDITOR_ID+ " ='" + audito_id + "' ;";

        Cursor cursor = db.rawQuery(detailsQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        auditor.setAuditor_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_AUDITOR_ID)));
        auditor.setAuditor_email(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AUDITOR_EMAIL)));
        auditor.setAuditor_assigned_jobs(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AUDITOR_ASSIGNED_JOB_ID)));


        db.close();

        return auditor;

    }
}
