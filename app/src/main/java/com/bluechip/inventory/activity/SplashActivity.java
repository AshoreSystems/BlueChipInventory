package com.bluechip.inventory.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluechip.inventory.R;
import com.bluechip.inventory.utilities.MyApplication;
import com.bluechip.inventory.utilities.SessionManager;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class SplashActivity extends AppCompatActivity implements View.OnClickListener {


    LinearLayout lin_lay_main;
    TextView textView_version;
    private static int SPLASH_TIME_OUT = 2000;
    private SessionManager session;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        initialiseView();
    }


    private void initialiseView() {


        session = new SessionManager(SplashActivity.this);

        //TextView textView_title = (TextView) findViewById(textView_title);
      /*  Typeface font = Typeface.createFromAsset(getAssets(), "fonts/CenturyGothic.ttf");
        textView_title.setTypeface(font);

        setFont();*/

        lin_lay_main = (LinearLayout) findViewById(R.id.lin_lay_main);
        textView_version = (TextView) findViewById(R.id.textView_version);
        lin_lay_main.setOnClickListener(this);


        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            textView_version.setText("version : " + version);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        new Handler().postDelayed(new Runnable() {

	            /*
                 * Showing splash screen with a timer. This will be useful when you
	             * want to show case your app logo
	             */

            @Override
            public void run() {


                checkLogin();

            }
        }, SPLASH_TIME_OUT);
    }

    private void checkLogin() {


        // Check if user is already logged in or not
        if (session.isLoggedIn() && !session.getString(session.KEY_USER_ID).isEmpty()) {
            // User is already logged in. Take him to main activity
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {

            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


    private void setFont() {
        MyApplication mApplication = new MyApplication();

        mApplication.setFont(getApplicationContext());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lin_lay_main:

                /*finish();
                startActivity(new Intent(SplashActivity.this, LoginActivity1.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

*/
                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }


}
