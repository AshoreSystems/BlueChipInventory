package com.bluechip.inventory.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluechip.inventory.R;
import com.bluechip.inventory.database.DatabaseHandler;
import com.bluechip.inventory.database.InventoryDB;
import com.bluechip.inventory.fragment.DashboardFragment;
import com.bluechip.inventory.fragment.JobsFragment;
import com.bluechip.inventory.fragment.SettingsFragment;
import com.bluechip.inventory.fragment.ViewReportFragment;
import com.bluechip.inventory.model.InventoryModel;
import com.bluechip.inventory.utilities.AppConfig;
import com.bluechip.inventory.utilities.AppConstant;
import com.bluechip.inventory.utilities.JsonConstant;
import com.bluechip.inventory.utilities.SessionManager;
import com.bluechip.inventory.utilities.Tools;
import com.bluechip.inventory.utilities.VolleyWebservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://images.all-free-download.com/images/graphiclarge/digital_sound_background_310844.jpg";
    private static final String urlProfileImg = "https://www.pocoyo.com/_site_v6/assets/images/html/specials/mundial/pocoyo-football.png";

    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static int selectedItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_DASH_BOARD = "dashboard";
    private static final String TAG_JOBS = "jobs";
    private static final String TAG_VIEW_REPORT = "viewreport";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_DASH_BOARD;
    public static String SELECTED_CURRENT_TAG = TAG_DASH_BOARD;
    public static int backButtonCount = 0;


    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    // Fragment
    JobsFragment jobsFragment;
    DashboardFragment dashboardFragment;
    ViewReportFragment viewReportFragment;
    SettingsFragment settingsFragment;
    private SessionManager session;


    private SharedPreferences permissionStatus;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        session = new SessionManager(MainActivity.this);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.user_name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_DASH_BOARD;
            loadHomeFragment();
        }


        backButtonCount = 0;
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText("User name");
        txtName.setVisibility(View.INVISIBLE);
        txtWebsite.setText(session.getString(session.KEY_USER_ID).toString());

        imgProfile.setImageResource(R.drawable.bluechip_log);

        // loading header background image
       /* Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);*/

        // Loading profile image
        /*Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
*/
        // showing dot next to notifications label
        navigationView.getMenu().getItem(1).setActionView(R.layout.menu_dot);


    }


    private Fragment getHomeFragment() {


        switch (navItemIndex) {
            case 0:
                // home

                AppConstant.KEY_INVENTORY_LIST = "OFF";

                CURRENT_TAG = TAG_DASH_BOARD;

                dashboardFragment = new DashboardFragment();
                return dashboardFragment;

            case 1:

                CURRENT_TAG = TAG_JOBS;
                jobsFragment = new JobsFragment();
                return jobsFragment;


            case 2:

                AppConstant.KEY_INVENTORY_LIST = "OFF";

                CURRENT_TAG = TAG_VIEW_REPORT;
                viewReportFragment = new ViewReportFragment();
                return viewReportFragment;
            case 3:

                // settings fragment
                AppConstant.KEY_INVENTORY_LIST = "OFF";

                CURRENT_TAG = TAG_SETTINGS;
                settingsFragment = new SettingsFragment();
                return settingsFragment;


            default:
                return new DashboardFragment();

        }
    }

    public void setToolbarTitle() {
        String[] activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        if (CURRENT_TAG.equalsIgnoreCase(TAG_JOBS)) {

            if (AppConstant.KEY_INVENTORY_LIST.equalsIgnoreCase("ON")) {
                getSupportActionBar().setTitle("Inventory");

            } else {
                getSupportActionBar().setTitle(activityTitles[navItemIndex]);
            }

        } else {
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        }


    }

    private void selectNavMenu() {

        navigationView.getMenu().getItem(navItemIndex).setChecked(true);


    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_dashboard:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_DASH_BOARD;

                        selectedItemIndex = navItemIndex;
                        SELECTED_CURRENT_TAG = CURRENT_TAG;

                        break;
                    case R.id.nav_jobs:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_JOBS;

                        selectedItemIndex = navItemIndex;
                        SELECTED_CURRENT_TAG = CURRENT_TAG;
                        break;
                    case R.id.nav_viewreport:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_VIEW_REPORT;

                        selectedItemIndex = navItemIndex;
                        SELECTED_CURRENT_TAG = CURRENT_TAG;
                        break;

                    case R.id.nav_settings:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_SETTINGS;

                        selectedItemIndex = navItemIndex;
                        SELECTED_CURRENT_TAG = CURRENT_TAG;
                        break;

                    case R.id.nav_logout:

                        navItemIndex = selectedItemIndex;
                        CURRENT_TAG = SELECTED_CURRENT_TAG;

                        dialog_logout();

                        break;
                   /* case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;*/
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                }/* else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);*/


                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    public void loadHomeFragment() {
        // selecting appropriate nav menu item

        // set toolbar title


        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
      /*  if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
           // toggleFab();
            return;
        }
*/

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
    /*    Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {*/
        // update the main content by replacing fragments
        FragmentManager fm = getFragmentManager();

        Fragment fragment = getHomeFragment();

        setToolbarTitle();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
        fragmentTransaction.commit();


        // save the changes

         /*   }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }*/

        // show or hide the fab button
        // toggleFab();

        //Closing drawer on item click

        selectNavMenu();

        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }


    private void loadLastOpenedFragment() {
        navItemIndex = selectedItemIndex;
        CURRENT_TAG = SELECTED_CURRENT_TAG;

        if (!CURRENT_TAG.equalsIgnoreCase(TAG_JOBS)) {

            AppConstant.KEY_INVENTORY_LIST = "OFF";

        }

        loadHomeFragment();
    }


    private void loadDashBoardFragment() {
        backButtonCount = 0;

        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_DASH_BOARD;
            loadHomeFragment();
            return;
        }
    }


    private void dialog_logout() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure want to Logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        logout_user();

                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();


                        // User cancelled the dialog
                    }
                })
        ;
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();

        dialog.setCancelable(true);
        dialog.show();

        dialog.getButton(Dialog.BUTTON_POSITIVE).setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        dialog.getButton(Dialog.BUTTON_NEGATIVE).setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                loadLastOpenedFragment();


            }
        });


    }

    private void logout_user() {

        // clear session
        //open login screen / splash screen


        session.clearSession();


        startActivity(new Intent(MainActivity.this, SplashActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();



    }

    @Override
    public void onBackPressed() {


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        } else if (CURRENT_TAG.equalsIgnoreCase(TAG_DASH_BOARD)) {


            if (backButtonCount >= 1) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }

        } else if (CURRENT_TAG.equalsIgnoreCase(TAG_JOBS)) {


            if (AppConstant.KEY_INVENTORY_LIST.equalsIgnoreCase("ON")) {
                AppConstant.KEY_INVENTORY_LIST = "OFF";
                jobsFragment.hideShowList();
            } else {
                // return to DashBoard
                loadDashBoardFragment();
            }


            setToolbarTitle();
        } else {
            loadDashBoardFragment();
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        /*if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_DASH_BOARD;
                loadHomeFragment();
                return;
            }
        }*/


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
      /*  if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }*/

        // when fragment is notifications, load the menu created for notifications
       /* if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
       /* if (navItemIndex == 0)
            fab.show();
        else
            fab.show();*/
    }




    private void requestPermissionDialog() {

        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);


        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECORD_AUDIO)) {
                //Show Information about why you need the permission
                //  showDialog(InventoryActivity.this,"ok");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setInverseBackgroundForced(true);
                builder.setTitle(" Record Audio Permission");

                builder.setMessage("This app needs Record permission.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.RECORD_AUDIO,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission


                // showDialog(InventoryActivity.this,"settings");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setInverseBackgroundForced(true);

                builder.setTitle(" Record Audio Permission");
                builder.setMessage("This app needs Record permission.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to allow Record Audio ", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.RECORD_AUDIO,true);
            editor.commit();


        }
    }


    // upload inventory


    public void uploadInventory() {


        JSONObject request_object = new JSONObject();
        JSONObject auditor_detail = new JSONObject();
        JSONObject job_detail = new JSONObject();
        JSONArray inventory_detail_array = new JSONArray();
         List<InventoryModel> inventoryList = new ArrayList<InventoryModel>();


        try {

            // auditor
            auditor_detail.put(JsonConstant.KEY_user_id, session.getString(session.KEY_USER_ID));
            auditor_detail.put(JsonConstant.KEY_user_email, session.getString(session.KEY_USER_EMAIL));


            // job_details
            job_detail.put(JsonConstant.KEY_job_id, "" + AppConstant.KEY_JOB_ID);
            job_detail.put(JsonConstant.KEY_job_cust_id, "" + AppConstant.KEY_JOB_CUST_ID);
            String data = Tools.formattedDatewithTime();
            job_detail.put(JsonConstant.KEY_job_upload_date, Tools.formattedDatewithTime());
            job_detail.put(JsonConstant.KEY_job_location_id, "" + AppConstant.KEY_JOB_LOC_ID);


            // inventory details for the current job

            String table_name = session.getString(session.KEY_AUDITOR_JOB_TABLE_NAME);


            if (!table_name.isEmpty() || !table_name.equalsIgnoreCase("")) {

                InventoryDB inventoryDB = new InventoryDB();

                if (inventoryList != null) {
                    inventoryList = null;
                }
                inventoryList = inventoryDB.getInventoryList(table_name, MainActivity.this);


                for (InventoryModel inventoryModel : inventoryList) {

                    JSONObject sub_inventory = new JSONObject();

                    sub_inventory.put(JsonConstant.KEY_prd_id, "" + inventoryModel.getPrd_id());
                    sub_inventory.put(JsonConstant.KEY_prd_description, "" + inventoryModel.getPrd_desc());
                    sub_inventory.put(JsonConstant.KEY_prd_sku, "" + inventoryModel.getPrd_sku());
                    sub_inventory.put(JsonConstant.KEY_prd_price, "" + inventoryModel.getPrd_price());
                    sub_inventory.put(JsonConstant.KEY_prd_category, "" + inventoryModel.getPrd_category());
                    sub_inventory.put(JsonConstant.KEY_prd_quantity, "" + inventoryModel.getPrd_quantity());

                    inventory_detail_array.put(sub_inventory);

                }

            }

            // main request
            request_object.put(JsonConstant.KEY_auditor_details, auditor_detail);
            request_object.put(JsonConstant.KEY_jobs_details, job_detail);
            request_object.put(JsonConstant.KEY_inventory_details, inventory_detail_array);


            String str1 = "kamlesh";


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            VolleyWebservice volleyWebservice = new VolleyWebservice( MainActivity.this, "JobFragment", "Please wait", AppConfig.URL_UPLOAD, request_object);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    //{"status":"1","msg":"Inventory saved successfully.","data":"0"}

    public void getJobUploadResponseFromVolley(JSONObject response) {
        Log.e(TAG, "" + response.toString());
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        try {
            String status = response.getString("status");

            if (status.equalsIgnoreCase("1")) {


                Toast.makeText(getApplicationContext(), "Upload Complete", Toast.LENGTH_SHORT).show();


            } else {
              //  hideStatusDialog();
                Toast.makeText(getApplicationContext(), "Please try After Sometime", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }

    }

}
