package com.bluechip.inventory.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluechip.inventory.R;
import com.bluechip.inventory.database.DatabaseHandler;
import com.bluechip.inventory.database.InventoryDB;
import com.bluechip.inventory.model.InventoryModel;
import com.bluechip.inventory.model.MasterInventoryModel;
import com.bluechip.inventory.utilities.AppConstant;
import com.bluechip.inventory.utilities.ConnectionDetector;
import com.bluechip.inventory.utilities.CustomDialog;
import com.bluechip.inventory.utilities.SessionManager;
import com.bluechip.inventory.utilities.Tools;

import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;


public class InventoryActivity extends FragmentActivity implements View.OnClickListener {

    private View mActionCustomView;
    LinearLayout linearLay_btn_back, linearLay_btn_share;

    TextView textView_emp_id, textView_area, textView_sub_area, textView_section, textView_sub_section;
    EditText editText_sku, editText_sku_test, editText_price, editText_quantity, editText_desc, editText_test;

    Button button_reset, button_save;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int REQUEST_RECORD_AUDIO = 0;
    private String STRING_SEPERATOR = "\\+";

    // internet
    public ConnectionDetector connectionDetector;

    public static int PERMISSION_COUNT = 0;
    private SharedPreferences permissionStatus;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SessionManager session;

    // inventory details
    MasterInventoryModel masterInventoryModel;
    InventoryDB inventoryDB;

    DatabaseHandler DH;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        final ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        mActionCustomView = mInflater.inflate(R.layout.custom_actionbar, null);

        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(mActionCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        linearLay_btn_back = (LinearLayout) mActionCustomView.findViewById(R.id.linearLay_btn_back);
        linearLay_btn_share = (LinearLayout) mActionCustomView.findViewById(R.id.linearLay_btn_share);


        linearLay_btn_back.setOnClickListener(this);

        initializeView();


        if (!AppConstant.KEY_ONE_AT) {
            requestPermissionDialog();
        }

        try {
            Tools.hideSoftKeyboard(InventoryActivity.this);
        } catch (Exception e) {
        }
    }

    private void initializeView() {

        session = new SessionManager(InventoryActivity.this);

        //textView
        textView_emp_id = (TextView) findViewById(R.id.textView_emp_id);

        textView_area = (TextView) findViewById(R.id.textView_area);
        textView_sub_area = (TextView) findViewById(R.id.textView_sub_area);
        textView_section = (TextView) findViewById(R.id.textView_section);
        textView_sub_section = (TextView) findViewById(R.id.textView_sub_section);

        textView_emp_id.setText(session.getString(session.KEY_AUDITOR_ID));
        textView_area.setText(AppConstant.KEY_JOB_AREA);
        textView_sub_area.setText(AppConstant.KEY_JOB_SUB_AREA);
        textView_section.setText(AppConstant.KEY_JOB_SECTION);
        textView_sub_section.setText(AppConstant.KEY_JOB_SUB_SECTION);

        //editText
        editText_sku = (EditText) findViewById(R.id.editText_sku);
        editText_sku_test = (EditText) findViewById(R.id.editText_sku_test);
        editText_sku_test.setFocusable(false);

        editText_price = (EditText) findViewById(R.id.editText_price);
        editText_quantity = (EditText) findViewById(R.id.editText_quantity);
        editText_desc = (EditText) findViewById(R.id.editText_desc);
        editText_test = (EditText) findViewById(R.id.editText_test);

        button_reset = (Button) findViewById(R.id.button_reset);
        button_save = (Button) findViewById(R.id.button_save);

        button_reset.setOnClickListener(this);
        button_save.setOnClickListener(this);


        /*editText_sku.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                int count= s.length();


chkKey();





            }
        });
*/
        editText_sku.setImeOptions(EditorInfo.IME_ACTION_GO);
        editText_sku.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //Log.d(TAG, "onEditorAction() called");

                if (event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    saveInventory();

                } else if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    saveInventory();

                } else if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (actionId == KeyEvent.KEYCODE_ENTER)) {

                    saveInventory();
                } else if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (actionId == KeyEvent.KEYCODE_DPAD_CENTER)) {

                    saveInventory();
                } else if (actionId == KeyEvent.KEYCODE_ENTER) {

                    saveInventory();
                } else if (actionId == KeyEvent.KEYCODE_DPAD_CENTER) {

                    saveInventory();
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {

                    saveInventory();
                } else {

                    String str = "temp";
                }

                String str1;

                str1 = "temp";


                return false;
            }
        });
        resetForm();
        editText_price.setText("");

        if (AppConstant.ADD_INVENTORY_STATUS.equalsIgnoreCase("edit")) {
            editText_desc.setText("" + AppConstant.KEY_PRD_DESC);
            editText_sku_test.setText("" + AppConstant.KEY_PRD_SKU);
            editText_quantity.setText("" + AppConstant.KEY_PRD_QUANTITY);
            editText_price.setText("" + AppConstant.KEY_PRD_PRICE);
        } /*else {

            // new inventory
        }
*/
        // inventory details
        masterInventoryModel = new MasterInventoryModel();
        inventoryDB = new InventoryDB();
        DH = new DatabaseHandler(InventoryActivity.this);


    }


    private void saveInventory() {

        if (editText_sku.getText().toString().equalsIgnoreCase("")) {
        } else {
            //  Toast.makeText(getApplicationContext(), "" + editText_sku.getText().toString().replaceAll("\\s", ""), Toast.LENGTH_SHORT).show();
            openNewInventoryForm();
        }

    }

    private void openNewInventoryForm() {
        // get info from db
        // set info in form
        int quantity = 0;
        String temp = "" + editText_sku.getText().toString().replaceAll("\\s", "");
        if ((editText_sku_test.getText().toString()).equalsIgnoreCase(editText_sku.getText().toString().replaceAll("\\s", ""))) {
            if (!editText_quantity.getText().toString().isEmpty()) {
                try {
                    quantity = Integer.parseInt(editText_quantity.getText().toString().trim());
                } catch (Exception e) {
                    quantity = 0;
                }
            }

            if (AppConstant.KEY_ONE_AT) {
                quantity = quantity + 1;
            } else {
                try {
                    quantity = Integer.parseInt(editText_quantity.getText().toString().trim());
                } catch (Exception e) {
                    quantity = 0;
                }
            }
        } else {
            AppConstant.ADD_INVENTORY_STATUS = "new";
          /*  if (AppConstant.KEY_ONE_AT) {
                quantity = quantity + 1;
            } else {
                quantity = 0;
            }
*/


            // get inventory details from master
            if (AppConstant.ADD_INVENTORY_STATUS.equalsIgnoreCase("new")) {

                editText_desc.setText("");
                editText_price.setText("");

                String product_sku = editText_sku.getText().toString().replaceAll("\\s", "");
                String table_master = session.getString(session.KEY_MASTER_TABLE_NAME);
                String table_inventory = session.getString(session.KEY_AUDITOR_JOB_TABLE_NAME);

                sqLiteDatabase = DH.OpenWritable();
                int inventory_count = inventoryDB.getInventoryCount(table_inventory, sqLiteDatabase, product_sku);
                sqLiteDatabase.close();

                if (inventory_count > 0) {

                    InventoryModel inventoryModel = inventoryDB.getInventoryDetails(table_inventory, InventoryActivity.this, product_sku);

                    quantity = inventoryModel.getPrd_quantity();
                    editText_price.setText("" + inventoryModel.getPrd_price());
                    editText_desc.setText("" + inventoryModel.getPrd_desc());


                } else {
                    quantity = 0;

                    try {
                        masterInventoryModel = inventoryDB.getMasterInventoryDetails(table_master, InventoryActivity.this, product_sku);
                        editText_price.setText("" + masterInventoryModel.getPrd_price());
                        editText_desc.setText("" + masterInventoryModel.getPrd_desc());
                    } catch (Exception e) {
                        e.printStackTrace();


                    }


                }

                // if inventory present in auditor table

                // else new entry

                String temp1 = "kamlesh";

                quantity = quantity + 1;
            }
        }
        editText_quantity.setText("" + quantity);
        editText_sku_test.getText().clear();
        editText_sku_test.setText(editText_sku.getText().toString().replaceAll("\\s", ""));
        String str = editText_sku.getText().toString().replaceAll("\\s", "");

        //editText_desc.setText((editText_sku.getText().toString()).replaceAll("\\s", ""));


        if (!AppConstant.KEY_ONE_AT) {

            if (isInternet()) {

                if (hasPermission()) {
                    promptSpeechInput();
                } else {
                    requestPermissionDialog();

                }
            } else {
                noVoiceOrInternet();
                new CustomDialog().dialog_ok_button(InventoryActivity.this, " Please enable Internet for working of \"Voice to Text\" ");
            }

        } else {
            saveInventoryToDB();
        }
        editText_sku.getText().clear();
        editText_sku.requestFocus();
    }

    private void saveInventoryToDB() {

        String job_table_name = session.getString(session.KEY_AUDITOR_JOB_TABLE_NAME);

        InventoryModel inventoryModel = new InventoryModel();
        inventoryModel.setPrd_sku(editText_sku_test.getText().toString());

        if (editText_price.getText().toString().equalsIgnoreCase("") || editText_price.getText().toString().isEmpty()) {
            inventoryModel.setPrd_price(0);
        } else {
            inventoryModel.setPrd_price(Integer.parseInt(editText_price.getText().toString()));
        }

        inventoryModel.setPrd_quantity(Integer.parseInt(editText_quantity.getText().toString()));
        inventoryModel.setPrd_desc(editText_desc.getText().toString());

        try {
            InventoryDB inventoryDB = new InventoryDB();
            inventoryDB.addInventory(job_table_name, inventoryModel, InventoryActivity.this);
            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.linearLay_btn_back:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.button_reset:
                resetForm();
                break;

            case R.id.button_save:


                // resetForm();
                if (editText_sku_test.getText().toString().isEmpty() || editText_sku_test.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please Scan the SKU", Toast.LENGTH_SHORT).show();
                } else if (!editText_sku.getText().toString().isEmpty() && !editText_quantity.getText().toString().isEmpty()) {
                    saveInventoryToDB();
                } else {
                    saveInventoryToDB();
                }
                break;
        }
    }

    private void resetForm() {
        editText_sku.getText().clear();
        editText_sku_test.getText().clear();
        editText_price.getText().clear();
        editText_quantity.getText().clear();
        editText_desc.getText().clear();
        editText_test.getText().clear();
        editText_sku.requestFocus();

        try {
            Tools.hideSoftKeyboard(InventoryActivity.this);
        } catch (Exception e) {
        }
    }


    // Voice to Text


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 4);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,500);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();

            noVoiceOrInternet();
        }
    }

    private void noVoiceOrInternet() {
        int quantity = 0;
        if ((editText_sku_test.getText().toString()).equalsIgnoreCase(editText_sku.getText().toString().replaceAll("\\s", ""))) {

            if (!editText_quantity.getText().toString().isEmpty()) {
                try {
                    quantity = Integer.parseInt(editText_quantity.getText().toString().trim());
                } catch (Exception e) {
                    quantity = 0;
                }
            }
        } else {
            quantity = 0;
        }
        editText_quantity.setText("" + (quantity + 1));
        saveInventoryToDB();
    }


    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText_test.setText(result.get(0));
                    setQuantity(result.get(0));
                }
                break;
            }

        }
    }

    // calculating speech to text quantity
    private void setQuantity(String result) {

        String text[] = derialize(result);

        int quantity = 0;

        if (!editText_quantity.getText().toString().isEmpty()) {

            try {
                quantity = Integer.parseInt(editText_quantity.getText().toString().trim());
            } catch (Exception e) {
                quantity = 0;
            }
        }


        for (int i = 0; i < text.length; i++) {
            try {
                quantity = quantity + Integer.parseInt(text[i].trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        editText_quantity.setText("" + quantity);

    }

    // parsing string
    public String[] derialize(String content) {
        return content.split(STRING_SEPERATOR);
    }

    private void requestPermission() {

        String str;

       /* boolean status = myRequestRecord();

        if (status) {

            str = "temp";
        } else {

            str = "temp1";

        }
*/
// requestForPermisson();


    }

    private boolean hasPermission() {
        boolean permission = true;
        if (ActivityCompat.checkSelfPermission(InventoryActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permission = false;
        }
        return permission;
    }

    private void requestPermissionDialog() {

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);


        if (ActivityCompat.checkSelfPermission(InventoryActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(InventoryActivity.this, Manifest.permission.RECORD_AUDIO)) {
                //Show Information about why you need the permission
                //  showDialog(InventoryActivity.this,"ok");

                AlertDialog.Builder builder = new AlertDialog.Builder(InventoryActivity.this);
                builder.setInverseBackgroundForced(true);
                builder.setTitle(" Record Audio Permission");

                builder.setMessage("This app needs Record permission.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(InventoryActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.RECORD_AUDIO, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission


                // showDialog(InventoryActivity.this,"settings");
                AlertDialog.Builder builder = new AlertDialog.Builder(InventoryActivity.this);
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
                ActivityCompat.requestPermissions(InventoryActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.RECORD_AUDIO, true);
            editor.commit();

        }
    }

    private void requestForPermisson() {

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

            boolean hasFilePermission = (ContextCompat.checkSelfPermission(InventoryActivity.this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);

            if (!hasFilePermission) {
                if (PERMISSION_COUNT > 1) {
                    dialog_allow_permission(InventoryActivity.this, " Record Audio permission is needed");
                }

                ActivityCompat.requestPermissions(InventoryActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO
                        },
                        10);
                String str = "temp" + PERMISSION_COUNT;
                PERMISSION_COUNT++;
            }

            boolean hasPermissionDenied = (ContextCompat.checkSelfPermission(InventoryActivity.this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED);

            if (hasPermissionDenied) {
            }
        }
    }

    private boolean myRequestRecord() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }/*else{*/

        if (checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
           /* } else {
                requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
            }*/

        }

        if (shouldShowRequestPermissionRationale(RECORD_AUDIO)) {
           /* Snackbar snackbar = Snackbar
                    .make(editText_sku, "Message is deleted", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {


                        public void onClick(View view) {*/
            requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_RECORD_AUDIO);


            //    new CustomDialog().dialog_ok_button(InventoryActivity.this, "Needed Permission For Recorded Audio");


                     /*   }
                    });

            snackbar.show();*/
        } else {
            requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
        return false;
    }

    public void dialog_allow_permission(Context context, String msg) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                        // FIRE ZE MISSILES!
                    }
                })

        ;
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialog.getButton(Dialog.BUTTON_POSITIVE).setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        dialog.getButton(Dialog.BUTTON_NEGATIVE).setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {


                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }
        });


    }

    public boolean isInternet() {
        connectionDetector = new ConnectionDetector(this);
        //isInternetPresent = connectionDetector.isConnectingToInternet();
        return connectionDetector.haveNetworkConnection();

    }


    public void showDialog(Activity activity, String status) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custome_dialog);

        Button button_cancel = (Button) dialog.findViewById(R.id.button_cancel);
        Button button_ok = (Button) dialog.findViewById(R.id.button_ok);
        Button button_settings = (Button) dialog.findViewById(R.id.button_settings);


        if (status.equalsIgnoreCase("ok")) {

            button_ok.setVisibility(View.VISIBLE);
            button_settings.setVisibility(View.GONE);
        } else {

            button_ok.setVisibility(View.GONE);
            button_settings.setVisibility(View.VISIBLE);
        }


// cancel
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // ok
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(InventoryActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);

            }
        });


        // settings
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                sentToSettings = true;
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                Toast.makeText(getBaseContext(), "Go to Permissions to allow Record Audio ", Toast.LENGTH_LONG).show();

            }
        });

        dialog.show();

    }

}
