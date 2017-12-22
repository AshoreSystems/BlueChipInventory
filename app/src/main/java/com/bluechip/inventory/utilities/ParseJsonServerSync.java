package com.bluechip.inventory.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.bluechip.inventory.activity.LoginActivity;
import com.bluechip.inventory.database.AuditorDB;
import com.bluechip.inventory.database.DatabaseHandler;
import com.bluechip.inventory.database.InventoryDB;
import com.bluechip.inventory.database.JobsDB;
import com.bluechip.inventory.model.AuditorModel;
import com.bluechip.inventory.model.CustomerModel;
import com.bluechip.inventory.model.JobModel;
import com.bluechip.inventory.model.MasterInventoryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ParseJsonServerSync {
    private static int MAX_DOWNLOAD = 0;
    private String formattedDatewithTime;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;
    private String ota_check_for_user_if_updated;
    String lastSyncDate;

    public String JSON_AUDITOR = "auditor_details";
    public String JSON_JOBS = "jobs_details";
    public String JSON_INVENTORY = "inventory_details";
    public String JSON_INVENTORY_DATA = "inventory_data";

    private ProgressDialog progressDialog_status;

    public static int DIALOG_DOWNLOAD_TOTAL = 0;
    public static int DIALOG_PROGRESS = 0;
    public Handler progressBarHandler1 = new Handler();
    public static Thread mThread;
    Context context = null;
    private SessionManager session;
    private static int CURRENT_PROGRESS = 0;
    LoginActivity loginActivity;


    public void setDbParsedJson(final Context context, final JSONObject data, ProgressDialog dialog_status) {

        this.progressDialog_status = dialog_status;
        this.context = context;

        session = new SessionManager(context);


      //  progressDialog_spin.show();


       // progressDialog_spin.dismiss();

        try{
            JSONArray jsonObject_job_details = data.getJSONArray(JSON_JOBS);
            JSONArray jsonObject_inventory_details = data.getJSONArray(JSON_INVENTORY);


            int master_length = 0;
            for (int i = 0; i < jsonObject_inventory_details.length(); i++) {

                JSONObject jsonObject = jsonObject_inventory_details.getJSONObject(i);
                JSONArray jsonArray = jsonObject.getJSONArray("inventory_data");

                master_length = master_length + jsonArray.length();


            }

            // total length
            CURRENT_PROGRESS = 1;
            MAX_DOWNLOAD = 1;

            // MAX_DOWNLOAD = MAX_DOWNLOAD + job_length + master_total_length;
            MAX_DOWNLOAD = MAX_DOWNLOAD + jsonObject_job_details.length() + master_length;


            progressDialog_status.setMax(MAX_DOWNLOAD);
            showDialog();
        }catch (Exception e){
            e.printStackTrace();
        }





        if (mThread != null) {
            mThread = null;
        }

        mThread = new Thread() {

            public void run() {

                CURRENT_PROGRESS = 1;
                String str12=""+CURRENT_PROGRESS;

                progressBarHandler1
                        .post(new Runnable() {
                            public void run() {

                                progressDialog_status.setProgress(CURRENT_PROGRESS);
                            }
                        });



        try {


            //  Thread.sleep(5000);

          //  Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show();


            JSONObject jsonObject_auditor_details = data.getJSONObject(JSON_AUDITOR);
            JSONArray jsonObject_job_details = data.getJSONArray(JSON_JOBS);
            JSONArray jsonObject_inventory_details = data.getJSONArray(JSON_INVENTORY);




            String str_auditor_jobs = "";

            // jobs ------------------------------------------------------------------
            for (int i = 0; i < jsonObject_job_details.length(); i++) {

                JobModel jobModel = new JobModel();

                JSONObject jsonObject_job_ = jsonObject_job_details.getJSONObject(i);

                str_auditor_jobs = "" + jsonObject_job_.getInt("job_id") + "$";
                jobModel.setJob_id(jsonObject_job_.getInt("job_id"));
                jobModel.setJob_cust_id(jsonObject_job_.getInt("job_cust_id"));
                jobModel.setJob_added_date(jsonObject_job_.getString("job_added_date"));
                jobModel.setJob_one_at(jsonObject_job_.getString("job_one_at"));


                try {
                    if (jsonObject_job_.getJSONObject("area") != null) {

                        jobModel.setArea_id(jsonObject_job_.getJSONObject("area").getString("area_id"));
                        jobModel.setArea_name(jsonObject_job_.getJSONObject("area").getString("area_name"));

                    }
                } catch (Exception e) {
                    jobModel.setArea_id("");
                    jobModel.setArea_name("");
                }

                try {
                    if (jsonObject_job_.getJSONObject("sub_area") != null) {
                        jobModel.setSub_area_id(jsonObject_job_.getJSONObject("sub_area").getString("sub_area_id"));
                        jobModel.setSub_area_name(jsonObject_job_.getJSONObject("sub_area").getString("sub_area_name"));
                    }
                } catch (Exception e) {
                    jobModel.setSub_area_id("");
                    jobModel.setSub_area_name("");
                }


                try {
                    if (jsonObject_job_.getJSONObject("section") != null) {

                        jobModel.setSec_id(jsonObject_job_.getJSONObject("section").getString("sec_id"));
                        jobModel.setSec_name(jsonObject_job_.getJSONObject("section").getString("sec_name"));

                    }
                } catch (Exception e) {
                    jobModel.setSec_id("");
                    jobModel.setSec_name("");
                }

                try {
                    if (jsonObject_job_.getJSONObject("sub_section") != null) {
                        jobModel.setSub_section_id(jsonObject_job_.getJSONObject("sub_section").getString("sub_section_id"));
                        jobModel.setSub_section_name(jsonObject_job_.getJSONObject("sub_section").getString("sub_section_name"));

                    }
                } catch (Exception e) {
                    jobModel.setSub_section_id("");
                    jobModel.setSub_section_name("");
                }


                jobModel.setLocation_id(jsonObject_job_.getString("location_id"));
                jobModel.setLocation_address(jsonObject_job_.getString("location_address"));

                jobModel.setFacility_name(jsonObject_job_.getString("facility_name"));


                JobsDB jobsdb = new JobsDB();

                jobsdb.addJobs(jobModel, context);


                // update progress
                CURRENT_PROGRESS ++;
                try {
                    Thread.sleep(20);
                    progressDialog_status.setProgress(CURRENT_PROGRESS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("STATUS ", "" + CURRENT_PROGRESS);
            }

            // auditor details ------------------------------------------------------------------


            AuditorModel auditorModel = new AuditorModel();
            auditorModel.setAuditor_id(jsonObject_auditor_details.getInt("user_id"));
            auditorModel.setAuditor_email(jsonObject_auditor_details.getString("user_email"));
            auditorModel.setAuditor_assigned_jobs(str_auditor_jobs);

            AuditorDB auditorDB = new AuditorDB();
            auditorDB.addAuditor(auditorModel, context);


            // save login session
            String str = jsonObject_auditor_details.getString("user_email");

            session.putString(session.KEY_USER_ID, jsonObject_auditor_details.getString("user_email"));
            session.setLogin(true);


            // inventory ------------------------------------------------------------------

            for (int i = 0; i < jsonObject_inventory_details.length(); i++) {


                CustomerModel customerModel = new CustomerModel();
                InventoryDB inventoryDB = new InventoryDB();

                JSONObject jsonObject = jsonObject_inventory_details.getJSONObject(i);


                // customer
                customerModel.setCustomer_id(jsonObject.getInt("cust_id"));
                customerModel.setCustomer_updated_date(jsonObject.getString("upload_date"));

                try {
                    inventoryDB.addCustomer(customerModel, context);
                } catch (Exception e) {

                }


                JSONArray jsonArray = jsonObject.getJSONArray("inventory_data");

                String table_master = "table_master_costumer" + jsonObject.getInt("cust_id");

                // create master table

                try {
                    DatabaseHandler DH = new DatabaseHandler(context);
                    SQLiteDatabase db = DH.OpenWritable();

                    DH.createMasterTable(table_master, db);

                    db.close();

                } catch (Exception e) {

                    String str1 = "";

                }

                // update progress
                CURRENT_PROGRESS ++;
                try {
                    Thread.sleep(20);
                    progressDialog_status.setProgress(CURRENT_PROGRESS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("STATUS ", "" + CURRENT_PROGRESS);


                // customer inventory
                for (int j = 0; j < jsonArray.length(); j++) {

                    MasterInventoryModel masterInventory = new MasterInventoryModel();
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                    masterInventory.setPrd_id(jsonObject1.getInt("prd_id"));
                    masterInventory.setPrd_category(jsonObject1.getString("prd_category"));
                    masterInventory.setPrd_sku(jsonObject1.getString("prd_SKU"));
                    masterInventory.setPrd_desc(jsonObject1.getString("prd_description"));
                    masterInventory.setPrd_price(jsonObject1.getString("prd_price"));


                    inventoryDB.addMasterInventory(table_master, masterInventory, context);



                    // update progress
                    CURRENT_PROGRESS ++;
                    try {
                        Thread.sleep(20);
                        progressDialog_status.setProgress(CURRENT_PROGRESS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("STATUS ", "" + CURRENT_PROGRESS);


                }


            }


          //  context.openDashBoard();
            InventoryDB inventoryDBchk = new InventoryDB();

            String table_master = "table_master_costumer1";

            List<MasterInventoryModel> masterList = new ArrayList<MasterInventoryModel>();


            masterList = inventoryDBchk.getMasterList("table_master_costumer1", context);


        } catch (Exception e) {

String str ="temp";
            e.printStackTrace();
        }

            }
        };
        mThread.start();


    }

    private void hideDialog() {

        if (progressDialog_status.isShowing()) {
            progressDialog_status.dismiss();
        }


    }

    private void showDialog() {

        if (progressDialog_status.isShowing()) {
            progressDialog_status.dismiss();
        }
    }


}
