package com.bluechip.inventory.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bluechip.inventory.R;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by aspl on 14/12/17.
 */

public class ForgetPasswordActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.forget_pwd_dialog);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        //initialiseView();
    }
}
