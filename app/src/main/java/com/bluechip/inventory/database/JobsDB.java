package com.bluechip.inventory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bluechip.inventory.model.JobModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aspl on 8/11/17.
 */

public class JobsDB {


    public void addJobs(JobModel jobs, Context context) {

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();


        int id = jobs.getJob_id();
        ContentValues values = new ContentValues();

        if (getJobCount(db, id) < 1) {




            values.put(DatabaseHandler.KEY_JOB_ID, jobs.getJob_id());
            values.put(DatabaseHandler.KEY_JOB_AUDITOR_ID, jobs.getJob_auditor_id());
            values.put(DatabaseHandler.KEY_JOB_CUST_ID, jobs.getJob_cust_id());
            values.put(DatabaseHandler.KEY_JOB_ADDED_DATE, jobs.getJob_added_date());
            values.put(DatabaseHandler.KEY_JOB_ONE_AT, jobs.getJob_one_at());
            values.put(DatabaseHandler.KEY_AREA_ID, jobs.getArea_id());
            values.put(DatabaseHandler.KEY_AREA_NAME, jobs.getArea_name());
            values.put(DatabaseHandler.KEY_SUB_AREA_ID, jobs.getSub_area_id());
            values.put(DatabaseHandler.KEY_SUB_AREA_NAME, jobs.getSub_area_name());
            values.put(DatabaseHandler.KEY_SEC_ID, jobs.getSec_id());
            values.put(DatabaseHandler.KEY_SEC_NAME, jobs.getSec_name());
            values.put(DatabaseHandler.KEY_SUB_SECTION_ID, jobs.getSub_section_id());
            values.put(DatabaseHandler.KEY_SUB_SECTION_NAME, jobs.getSub_section_name());
            values.put(DatabaseHandler.KEY_LOCATION_ID, jobs.getLocation_id());
            values.put(DatabaseHandler.KEY_LOCATION_ADDRESS, jobs.getLocation_address());
            values.put(DatabaseHandler.KEY_FACILITY_NAME, jobs.getFacility_name());


            // Insert Row
            db.insert(DatabaseHandler.TABLE_JOBS_DETAILS, null, values);

            int checkinsert = getJobCount(db, jobs.getJob_id());

            db.close(); // Closing database connection
        } else {




            values.put(DatabaseHandler.KEY_JOB_ID, jobs.getJob_id());
            values.put(DatabaseHandler.KEY_JOB_AUDITOR_ID, jobs.getJob_auditor_id());
            values.put(DatabaseHandler.KEY_JOB_CUST_ID, jobs.getJob_cust_id());
            values.put(DatabaseHandler.KEY_JOB_ADDED_DATE, jobs.getJob_added_date());
            values.put(DatabaseHandler.KEY_JOB_ONE_AT, jobs.getJob_one_at());

            values.put(DatabaseHandler.KEY_AREA_ID, jobs.getArea_id());
            values.put(DatabaseHandler.KEY_AREA_NAME, jobs.getArea_name());

            values.put(DatabaseHandler.KEY_SUB_AREA_ID, jobs.getSub_area_id());
            values.put(DatabaseHandler.KEY_SUB_AREA_NAME, jobs.getSub_area_name());

            values.put(DatabaseHandler.KEY_SEC_ID, jobs.getSec_id());
            values.put(DatabaseHandler.KEY_SEC_NAME, jobs.getSec_name());

            values.put(DatabaseHandler.KEY_SUB_SECTION_ID, jobs.getSub_section_id());
            values.put(DatabaseHandler.KEY_SUB_SECTION_NAME, jobs.getSub_section_name());

            values.put(DatabaseHandler.KEY_LOCATION_ID, jobs.getLocation_id());
            values.put(DatabaseHandler.KEY_LOCATION_ADDRESS, jobs.getLocation_address());
            values.put(DatabaseHandler.KEY_FACILITY_NAME, jobs.getFacility_name());

            int checkinsert = db.update(DatabaseHandler.TABLE_JOBS_DETAILS, values,
                    (DatabaseHandler.KEY_JOB_ID + " = ? "), new String[]{String.valueOf(jobs.getJob_id())});
            db.close();


        }


    }

    //Get Total Count of Que for a Event
    public int getJobCount(SQLiteDatabase db, int job_id) {


        String countQuery = "SELECT * FROM " + DatabaseHandler.TABLE_JOBS_DETAILS
                + " WHERE "
                + DatabaseHandler.KEY_JOB_ID + "='" + job_id + "';";

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;


    }


    public JobModel getJobDetails(Context context, String job_id) {

        JobModel jobModel = new JobModel();
        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM" + DatabaseHandler.TABLE_JOBS_DETAILS
                + " WHERE "
                + DatabaseHandler.KEY_JOB_ID + " ='" + job_id + "' ;";

        Cursor cursor = db.rawQuery(detailsQuery, null);

        if (cursor != null)
            cursor.moveToFirst();


        jobModel.setJob_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_ID)));
        jobModel.setJob_auditor_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_AUDITOR_ID)));
        jobModel.setJob_cust_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_CUST_ID)));
        jobModel.setJob_added_date(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_ADDED_DATE)));
        jobModel.setJob_one_at(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_ONE_AT)));

        jobModel.setArea_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AREA_ID)));
        jobModel.setArea_name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AREA_NAME)));

        jobModel.setSub_area_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUB_AREA_ID)));
        jobModel.setSub_area_name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUB_AREA_NAME)));

        jobModel.setSec_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SEC_ID)));
        jobModel.setSec_name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SEC_NAME)));

        jobModel.setSub_section_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUB_SECTION_ID)));
        jobModel.setSub_section_name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUB_SECTION_NAME)));

        jobModel.setLocation_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_LOCATION_ID)));
        jobModel.setLocation_address(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_LOCATION_ADDRESS)));
        jobModel.setFacility_name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FACILITY_NAME)));

        return jobModel;

    }


    public List<JobModel> getJobList(Context context) {

        List<JobModel> jobModelList = new ArrayList<JobModel>();

        DatabaseHandler DH = new DatabaseHandler(context);
        SQLiteDatabase db = DH.OpenWritable();

        String detailsQuery = "SELECT * FROM " + DatabaseHandler.TABLE_JOBS_DETAILS

                + " ;";

        Cursor cursor = db.rawQuery(detailsQuery, null);

        if (cursor.moveToFirst()) {

            do {

                JobModel jobModel = new JobModel();


                jobModel.setJob_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_ID)));
                jobModel.setJob_auditor_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_AUDITOR_ID)));
                jobModel.setJob_cust_id(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_CUST_ID)));
                jobModel.setJob_added_date(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_ADDED_DATE)));
                jobModel.setJob_one_at(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_JOB_ONE_AT)));

                jobModel.setArea_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AREA_ID)));
                jobModel.setArea_name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AREA_NAME)));

                jobModel.setSub_area_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUB_AREA_ID)));
                jobModel.setSub_area_name("-"+cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUB_AREA_NAME)));

                jobModel.setSec_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SEC_ID)));
                jobModel.setSec_name("-"+cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SEC_NAME)));

                jobModel.setSub_section_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUB_SECTION_ID)));
                jobModel.setSub_section_name("-"+cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SUB_SECTION_NAME)));

                jobModel.setLocation_id(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_LOCATION_ID)));
                jobModel.setLocation_address(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_LOCATION_ADDRESS)));
                jobModel.setFacility_name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FACILITY_NAME)));

                jobModelList.add(jobModel);
            } while (cursor.moveToNext());
        }
        return jobModelList;

    }


    public void dropAllTables(SQLiteDatabase db) {

        try {

            String drop_query = "select 'drop table ' || name || ';' from sqlite_master where type = 'table';";
            Cursor cursor = db.rawQuery(drop_query, null);
            ;

            String str = "temp";
        } catch (Exception e) {
            Log.e("Drop Table", "Failed");

            e.printStackTrace();

        }


    }

}
