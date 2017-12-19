/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.bluechip.inventory.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bluechip.inventory.R;
import com.bluechip.inventory.database.AuditorDB;
import com.bluechip.inventory.database.DatabaseHandler;
import com.bluechip.inventory.database.InventoryDB;
import com.bluechip.inventory.database.JobsDB;
import com.bluechip.inventory.model.AuditorModel;
import com.bluechip.inventory.model.CustomerModel;
import com.bluechip.inventory.model.JobModel;
import com.bluechip.inventory.model.MasterInventoryModel;
import com.bluechip.inventory.utilities.AppConfig;
import com.bluechip.inventory.utilities.ConnectionDetector;
import com.bluechip.inventory.utilities.CustomDialog;
import com.bluechip.inventory.utilities.SessionManager;
import com.bluechip.inventory.utilities.Tools;
import com.bluechip.inventory.utilities.VolleyWebservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static com.bluechip.inventory.R.id.email;


public class LoginActivity extends Activity {
    private String TAG = "Login:";
    // private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin, button_forget_pwd;
    private Button btnLinkToRegister;
    private AutoCompleteTextView inputEmail;
    private EditText inputPassword;
    ProgressBar login_progress;
    private SessionManager session;
    private static final int REQUEST_READ_CONTACTS = 0;

// relLay_logo

    RelativeLayout relLay_logo;


    // internet
    public ConnectionDetector connectionDetector;


    // parsing

    public String JSON_AUDITOR = "auditor_details";
    public String JSON_JOBS = "jobs_details";
    public String JSON_INVENTORY = "inventory_details";
    public String JSON_INVENTORY_DATA = "inventory_data";

    // progress
    private ProgressDialog progressDialog_status;
    public Handler progressBarHandler1 = new Handler();
    public static Thread mThread;
    private static int MAX_DOWNLOAD = 0;
    private static int CURRENT_PROGRESS = 0;
    Context context = null;


    //forget password
    public AlertDialog dialog;
    LinearLayout progress_forget_pwd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeView();

        populateAutoComplete();

        try {
            Tools.hideSoftKeyboard(LoginActivity.this);
        } catch (Exception e) {
        }
    }

    private void initializeView() {

        // Session manager
        session = new SessionManager(getApplicationContext());


        relLay_logo = (RelativeLayout) findViewById(R.id.relLay_logo);

        relLay_logo.setVisibility(View.GONE);


        inputEmail = (AutoCompleteTextView) findViewById(email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.button_login);
        button_forget_pwd = (Button) findViewById(R.id.button_forget_pwd);
        login_progress = (ProgressBar) findViewById(R.id.login_progress);


        // btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);


      /*  pDialog_spin = new ProgressDialog(this);
        pDialog_spin.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        pDialog_spin.setMessage("Login");*/


        // SQLite database handler
        //  db = new SQLiteHandler(getApplicationContext());
        button_forget_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(LoginActivity.this);

                //startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));

            }
        });


        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (isInternetOn()) {

                    // fabric test report
                    //  forceCrash(view);


                    // Check for empty data in the form
                    if (!email.isEmpty() && !password.isEmpty()) {
                        // login user
                        //  showLoadDialog();
                        checkLogin(email, password);
                    } else {
                        // Prompt user to enter credentials
                        Toast.makeText(getApplicationContext(),
                                "Please enter the credentials!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    new CustomDialog().dialog_ok_button(LoginActivity.this, "Please enable Internet");
                }


            }

        });

    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        //      showStatusDialog();
        login_progress.setVisibility(View.VISIBLE);


        JSONObject obj = null;
        obj = new JSONObject();
        try {
            obj.put("user_email", email);
            obj.put("user_password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*VolleyWebservice volleyWebservice = new VolleyWebservice(LoginActivity.this, "LoginActivity", "Please wait", "http://192.168.1.90/pillowsuggestor/api_1_0/sync_retailer", obj);
            */
        VolleyWebservice volleyWebservice = new VolleyWebservice(LoginActivity.this, "LoginActivity", "Please wait", AppConfig.URL_LOGIN, obj);
        //volleyWebservice.callWebService();

    }


    private void showStatusDialog() {
        if (!progressDialog_status.isShowing())
            progressDialog_status.show();
    }

    private void hideStatusDialog() {
        if (progressDialog_status.isShowing())
            progressDialog_status.dismiss();
    }


    public void getLoginResposeFromVolley(JSONObject response) {
        Log.e(TAG, "" + response.toString());

        try {
            int status = response.getInt("status");

            if (status == 1) {

                login_progress.setVisibility(View.GONE);
                relLay_logo.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                setDbParsedJson(response);

            } else {
                login_progress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "email or password is wrong", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }

    }

    public void openDashBoard() {
        finish();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    // permissions

    private void populateAutoComplete() {
        if (!myRequestContacts()) {
            return;
        }

        // getLoaderManager().initLoader(0, null, this);
    }


    private boolean myRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    // internet
    public boolean isInternetOn() {
        connectionDetector = new ConnectionDetector(this);
        //isInternetPresent = connectionDetector.isConnectingToInternet();
        return connectionDetector.haveNetworkConnection();

    }

    // fabric test
    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }


    // parsing the response
    public void setDbParsedJson(final JSONObject data) {

        // total length
        CURRENT_PROGRESS = 1;
        MAX_DOWNLOAD = 1;
        this.context = getApplicationContext();
        //  session = new SessionManager(context);

        progressDialog_status = new ProgressDialog(LoginActivity.this);
        progressDialog_status.setCancelable(false);
        //progressDialog_status.setTitle("Please Wait");
        progressDialog_status.setMessage("Fetching Data....");
        progressDialog_status.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // progressDialog_status.setProgressNumberFormat(null);
        progressDialog_status.setProgress(0);

        try {
            JSONArray jsonObject_job_details = data.getJSONArray(JSON_JOBS);
            JSONArray jsonObject_inventory_details = data.getJSONArray(JSON_INVENTORY);

            int master_length = 0;
            for (int i = 0; i < jsonObject_inventory_details.length(); i++) {
                JSONObject jsonObject = jsonObject_inventory_details.getJSONObject(i);
                JSONArray jsonArray = jsonObject.getJSONArray("inventory_data");
                master_length = master_length + jsonArray.length();
            }
            // MAX_DOWNLOAD = MAX_DOWNLOAD + job_length + master_total_length;
            MAX_DOWNLOAD = MAX_DOWNLOAD + jsonObject_job_details.length() + master_length;

        } catch (Exception e) {
            e.printStackTrace();
        }


        progressDialog_status.setMax(MAX_DOWNLOAD);

        if (MAX_DOWNLOAD == 0) {
        } else {
            showStatusDialog();
        }

        if (mThread != null) {
            mThread = null;
        }

        mThread = new Thread() {


            public void run() {
                CURRENT_PROGRESS = 1;
                progressBarHandler1
                        .post(new Runnable() {
                            public void run() {
                                progressDialog_status.setProgress(CURRENT_PROGRESS);
                            }
                        });

                // parsing the response
                try {

                    JSONObject jsonObject_auditor_details = data.getJSONObject(JSON_AUDITOR);
                    JSONArray jsonObject_job_details = data.getJSONArray(JSON_JOBS);
                    JSONArray jsonObject_inventory_details = data.getJSONArray(JSON_INVENTORY);

                    String str_auditor_jobs = "";

                    // jobs ------------------------------------------------------------------
                    for (int i = 0; i < jsonObject_job_details.length(); i++) {

                        JSONObject jsonObject_job_ = jsonObject_job_details.getJSONObject(i);
                        str_auditor_jobs = str_auditor_jobs + "" + jsonObject_job_.getInt("job_id") + "$";

                        String table_inventory_auditor_job = "table_inventory_aud"
                                + jsonObject_auditor_details.getString("user_id")
                                + "_job"
                                + jsonObject_job_.getInt("job_id");  // format

                        // create dynamic auditor job table -- table_inventory_audId_jobId

                        try {
                            DatabaseHandler DH = new DatabaseHandler(context);
                            SQLiteDatabase db = DH.OpenWritable();
                            DH.createInventoryTable(table_inventory_auditor_job, db);
                            db.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            String str1 = "";
                        }

                        JobModel jobModel = new JobModel();
                        jobModel.setJob_id(jsonObject_job_.getInt("job_id"));
                        jobModel.setJob_auditor_id(jsonObject_auditor_details.getInt("user_id"));
                        jobModel.setJob_cust_id(jsonObject_job_.getInt("job_cust_id"));
                        jobModel.setJob_added_date(jsonObject_job_.getString("job_added_date"));
                        jobModel.setJob_one_at(jsonObject_job_.getString("job_one_at"));

                        try { // area
                            if (jsonObject_job_.getJSONObject("area") != null) {
                                jobModel.setArea_id(jsonObject_job_.getJSONObject("area").getString("area_id"));
                                jobModel.setArea_name(jsonObject_job_.getJSONObject("area").getString("area_name"));
                            }
                        } catch (Exception e) {
                            jobModel.setArea_id("");
                            jobModel.setArea_name("");
                        }

                        try { //sub-area
                            if (jsonObject_job_.getJSONObject("sub_area") != null) {
                                jobModel.setSub_area_id(jsonObject_job_.getJSONObject("sub_area").getString("sub_area_id"));
                                jobModel.setSub_area_name(jsonObject_job_.getJSONObject("sub_area").getString("sub_area_name"));
                            }
                        } catch (Exception e) {
                            jobModel.setSub_area_id("");
                            jobModel.setSub_area_name("");
                        }

                        try { // section
                            if (jsonObject_job_.getJSONObject("section") != null) {
                                jobModel.setSec_id(jsonObject_job_.getJSONObject("section").getString("sec_id"));
                                jobModel.setSec_name(jsonObject_job_.getJSONObject("section").getString("sec_name"));
                            }
                        } catch (Exception e) {
                            jobModel.setSec_id("");
                            jobModel.setSec_name("");
                        }

                        try { // sub-section
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
                        CURRENT_PROGRESS++;
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
                    session.putString(session.KEY_USER_EMAIL, jsonObject_auditor_details.getString("user_email"));
                    session.putString(session.KEY_USER_ID, jsonObject_auditor_details.getString("user_id"));
                    session.setLogin(true);


                    // inventory ------------------------------------------------------------------

                    for (int i = 0; i < jsonObject_inventory_details.length(); i++) {

                        InventoryDB inventoryDB = new InventoryDB();
                        JSONObject jsonObject = jsonObject_inventory_details.getJSONObject(i);

                        // customer
                        CustomerModel customerModel = new CustomerModel();
                        customerModel.setCustomer_id(jsonObject.getInt("cust_id"));
                        customerModel.setCustomer_updated_date(jsonObject.getString("upload_date"));

                        try {
                            inventoryDB.addCustomer(customerModel, context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray("inventory_data");
                        String table_master_name = "table_master_costumer" + jsonObject.getInt("cust_id");
                        String temp_upload_date = jsonObject.getString("upload_date");

                        // check table is present
                        boolean is_table_present = inventoryDB.isMasterTablePresent(table_master_name, context);

                        // update progress
                        CURRENT_PROGRESS++;
                        try {
                            Thread.sleep(20);
                            progressDialog_status.setProgress(CURRENT_PROGRESS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e("STATUS ", "" + CURRENT_PROGRESS);


                        /*if(table_present){
                            if(date is different){
                                save inventory
                            }
                        }else{
                            create table
                            save inventory
                        }
                        */


                        // remove indexing if exist----------------
                        // create dynamic master table -- table_master_costumer_cust_id
                        // add indexing if not exist----------------


                        if (is_table_present) { // table is present

                            boolean is_master_updated = inventoryDB.isMasterUpdated(jsonObject.getInt("cust_id"), temp_upload_date, context);

                            if (is_master_updated) {

                                // delete rows and create master table
                                try {
                                    DatabaseHandler DH = new DatabaseHandler(context);
                                    DH.removeMasterTableInventories(table_master_name);

                                    createMasterTable(table_master_name);
                                } catch (Exception e) {

                                }


                                // add inventory

                                for (int j = 0; j < jsonArray.length(); j++) {

                                    JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                                    MasterInventoryModel masterInventory = new MasterInventoryModel();

                                    masterInventory.setPrd_id(jsonObject1.getInt("prd_id"));
                                    masterInventory.setPrd_category(jsonObject1.getString("prd_category"));
                                    masterInventory.setPrd_sku(jsonObject1.getString("prd_SKU"));
                                    masterInventory.setPrd_desc(jsonObject1.getString("prd_description"));
                                    masterInventory.setPrd_price((int) (Float.parseFloat(jsonObject1.getString("prd_price")) * 100));

                                    inventoryDB.addMasterInventory(table_master_name, masterInventory, context);

                                    // update progress
                                    CURRENT_PROGRESS++;
                                    try {
                                        Thread.sleep(20);
                                        progressDialog_status.setProgress(CURRENT_PROGRESS);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("STATUS ", "" + CURRENT_PROGRESS);
                                }
                            }
                        } else { // is table is not present

                            // create master table
                            try {
                                createMasterTable(table_master_name);
                            } catch (Exception e) {

                            }

                            // add inventory

                            for (int j = 0; j < jsonArray.length(); j++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                                MasterInventoryModel masterInventory = new MasterInventoryModel();

                                masterInventory.setPrd_id(jsonObject1.getInt("prd_id"));
                                masterInventory.setPrd_category(jsonObject1.getString("prd_category"));
                                masterInventory.setPrd_sku(jsonObject1.getString("prd_SKU"));
                                masterInventory.setPrd_desc(jsonObject1.getString("prd_description"));
                                masterInventory.setPrd_price((int) (Float.parseFloat(jsonObject1.getString("prd_price")) * 100));

                                inventoryDB.addMasterInventory(table_master_name, masterInventory, context);

                                // update progress
                                CURRENT_PROGRESS++;
                                try {
                                    Thread.sleep(20);
                                    progressDialog_status.setProgress(CURRENT_PROGRESS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Log.e("STATUS ", "" + CURRENT_PROGRESS);
                            }

                        }


                    }


                    //  checking Master List
                    InventoryDB inventoryDBchk = new InventoryDB();
                    String table_master = "table_master_costumer1";
                    List<MasterInventoryModel> masterList = new ArrayList<MasterInventoryModel>();
                    masterList = inventoryDBchk.getMasterList("table_master_costumer1", context);

                    progressBarHandler1
                            .post(new Runnable() {
                                public void run() {
                                    if (CURRENT_PROGRESS >= MAX_DOWNLOAD) {
                                        progressDialog_status.setTitle("Done");

                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    try {
                                        hideStatusDialog();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    openDashBoard();
                                }
                            });
                } catch (JSONException e) {
                    String str = "json";
                    e.printStackTrace();
                } catch (Exception e) {
                    String str = "temp";
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

    private void createMasterTable(String table_master_name) {


        // create table
        try {
            DatabaseHandler DH = new DatabaseHandler(context);
            SQLiteDatabase db = DH.OpenWritable();
            DH.createMasterTable(table_master_name, db);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            String str1 = "";
        }
    }

    public void showDialog(Context context) {
       /* final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.forget_pwd_dialog);*/


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.forget_pwd_dialog, null);
        alertDialog.setView(convertView);

        final EditText editText_email = (EditText) convertView.findViewById(R.id.editText_email);
        final Button button_reset_pwd = (Button) convertView.findViewById(R.id.button_reset_pwd);
        Button button_back = (Button) convertView.findViewById(R.id.button_back);
        progress_forget_pwd = (LinearLayout) convertView.findViewById(R.id.progress_forget_pwd);

        progress_forget_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog = alertDialog.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        editText_email.setText(inputEmail.getText().toString());

        button_reset_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText_email.getText().toString().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();


                } else if (!isInternetOn()) {

                    new CustomDialog().dialog_ok_button(LoginActivity.this, "Please enable Internet");

                } else {

                    resetPassword(editText_email.getText().toString());

                }

            }
        });


        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });




        /*Button button_cancel = (Button) dialog.findViewById(R.id.button_cancel);
        Button button_ok = (Button) dialog.findViewById(R.id.button_ok);*/

    }

    private void resetPassword(String email) {

        progress_forget_pwd.setVisibility(View.VISIBLE);


        JSONObject obj = null;
        obj = new JSONObject();
        try {
            obj.put("user_email", email);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        VolleyWebservice volleyWebservice = new VolleyWebservice(LoginActivity.this, "ForgetPassword", "Please wait", AppConfig.URL_FORGET_PWD, obj);


    }


    public void getForgetPasswordResposeFromVolley(JSONObject response) {
        try {
            int status = response.getInt("status");

            if (status == 1) {

                progress_forget_pwd.setVisibility(View.GONE);
                dialog.dismiss();

                new CustomDialog().dialog_ok_button(LoginActivity.this, "Please check email for new password");

                // Toast.makeText(getApplicationContext(), "Please check email for reset password", Toast.LENGTH_SHORT).show();


            } else {
                progress_forget_pwd.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Please enter the register email", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }
    }
}
