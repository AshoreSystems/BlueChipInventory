/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.bluechip.inventory.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends Activity {
    private String TAG = "Login:";
    // private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
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
    private static int MAX_DOWNLOAD = 0;

    public String JSON_AUDITOR = "auditor_details";
    public String JSON_JOBS = "jobs_details";
    public String JSON_INVENTORY = "inventory_details";
    public String JSON_INVENTORY_DATA = "inventory_data";

    private ProgressDialog progressDialog_status;
    public Handler progressBarHandler1 = new Handler();
    public static Thread mThread;
    private static int CURRENT_PROGRESS = 0;
    Context context = null;


    // private SQLiteHandler db;

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


        inputEmail = (AutoCompleteTextView) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.button_login);
        login_progress = (ProgressBar) findViewById(R.id.login_progress);

        // btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);


      /*  pDialog_spin = new ProgressDialog(this);
        pDialog_spin.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        pDialog_spin.setMessage("Login");*/


        // SQLite database handler
        //  db = new SQLiteHandler(getApplicationContext());


        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (isInternet()) {

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


                    new CustomDialog().dialog_enable_internet(LoginActivity.this, "Please enable Internet");
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
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        try {
            String status = response.getString("status");

            if (status.equalsIgnoreCase("1")) {

                login_progress.setVisibility(View.GONE);
                relLay_logo.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                setDbParsedJson(response);

            } else {
                login_progress.setVisibility(View.GONE);
                hideStatusDialog();
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
    public boolean isInternet() {
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
                        str_auditor_jobs = "" + jsonObject_job_.getInt("job_id") + "$";


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
                        String table_master = "table_master_costumer" + jsonObject.getInt("cust_id");

                        // remove indexing if exist----------------

                        // create dynamic master table -- table_master_costumer_cust_id

                        try {
                            DatabaseHandler DH = new DatabaseHandler(context);
                            SQLiteDatabase db = DH.OpenWritable();
                            DH.createMasterTable(table_master, db);
                            db.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            String str1 = "";
                        }

                        // update progress
                        CURRENT_PROGRESS++;
                        try {
                            Thread.sleep(20);
                            progressDialog_status.setProgress(CURRENT_PROGRESS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e("STATUS ", "" + CURRENT_PROGRESS);


                        // customer master inventory ------------------------------------------------
                        for (int j = 0; j < jsonArray.length(); j++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(j);

                            MasterInventoryModel masterInventory = new MasterInventoryModel();


                            masterInventory.setPrd_id( jsonObject1.getInt("prd_id"));
                            masterInventory.setPrd_category(jsonObject1.getString("prd_category"));
                            masterInventory.setPrd_sku(jsonObject1.getString("prd_SKU"));
                            masterInventory.setPrd_desc(jsonObject1.getString("prd_description"));

                            masterInventory.setPrd_price((int) (Float.parseFloat(jsonObject1.getString("prd_price"))*100));

                            inventoryDB.addMasterInventory(table_master, masterInventory, context);


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

                        // add indexing if not exist----------------
                    }
                    //  check Master List
                    InventoryDB inventoryDBchk = new InventoryDB();
                    String table_master = "table_master_costumer1";
                    List<MasterInventoryModel> masterList = new ArrayList<MasterInventoryModel>();
                    masterList = inventoryDBchk.getMasterList("table_master_costumer1", context);

                    progressBarHandler1
                            .post(new Runnable() {
                                public void run() {
                                    if (CURRENT_PROGRESS == MAX_DOWNLOAD) {
                                        progressDialog_status.setTitle("Done");

                                        try {
                                            Thread.sleep(500);
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


}
