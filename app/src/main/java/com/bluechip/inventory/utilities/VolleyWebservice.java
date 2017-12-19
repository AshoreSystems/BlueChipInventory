package com.bluechip.inventory.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bluechip.inventory.activity.LoginActivity;
import com.bluechip.inventory.activity.MainActivity;

import org.json.JSONObject;


public class VolleyWebservice {
    String WhichWebservice, DialogStr, URL;
    JSONObject object_request;
    Context mContext;
    String TAG = "VolleyWebservice";
    ProgressDialog pDialog;
    LoginActivity loginActivity;
    MainActivity mainActivity;

    Activity activity;
    Handler handler;
    String announcment = "";

    public VolleyWebservice(LoginActivity loginActivity, String WhichWebservice, String DialogStr, String URL, JSONObject object_request) {
        mContext = loginActivity;
        this.WhichWebservice = WhichWebservice;
        this.loginActivity = loginActivity;
        activity = loginActivity;
        this.DialogStr = DialogStr;
        this.URL = URL;
        this.object_request = object_request;

        callWebService();
    }

    public VolleyWebservice(MainActivity mainActivity, String WhichWebservice, String DialogStr, String URL, JSONObject object_request) {
        mContext = mainActivity;
        this.mainActivity = mainActivity;
        this.WhichWebservice = WhichWebservice;
        this.DialogStr = DialogStr;
        this.URL = URL;
        this.object_request = object_request;

        callWebService();
    }

    public VolleyWebservice(Context context, String WhichWebservice, String DialogStr, String URL, JSONObject object_request) {
        mContext = context;
        this.WhichWebservice = WhichWebservice;
        this.DialogStr = DialogStr;
        this.URL = URL;
        this.object_request = object_request;

        callWebService();
    }


    private void callWebService() {

        RequestQueue queue = Volley.newRequestQueue(mContext);

        final JSONObject response = new JSONObject();

         /*   pDialog = new ProgressDialog(mContext);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("please wait 2 ..");
            pDialog.setCancelable(false);
        if(!WhichWebservice.equalsIgnoreCase("ServerSync")) {
            pDialog.show();
        }*/


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, object_request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // mPostCommentResponse.requestCompleted();
                        try {
                            Log.e(TAG, "Service--o/p-" + response);
                            if (WhichWebservice.equals("LoginActivity")) {
                                loginActivity.getLoginResposeFromVolley(response);
                            } else if (WhichWebservice.equals("ForgetPassword")) {
                                loginActivity.getForgetPasswordResposeFromVolley(response);
                            }else if (WhichWebservice.equals("JobFragment")) {
                                mainActivity.getJobUploadResponseFromVolley(response);
                            }


                            //   pDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                try {

                    pDialog.dismiss();
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e("TAG", "Service--i/p-" + error);
                    String ErrorMessage = "";
                    String ErrorTitle = "";
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        ErrorMessage = "Connection Error. Please check your connection and try again.";
                        ErrorTitle = "Connection Error";
                    } else if (error instanceof AuthFailureError) {
                        ErrorMessage = "Auth Failure Error. Please try again later.";
                        ErrorTitle = "AuthFailureError";
                    } else if (error instanceof ServerError) {
                        ErrorMessage = "Server Error. Please try again later.";
                        ErrorTitle = "Server Error";
                    } else if (error instanceof NetworkError) {
                        ErrorMessage = "Network Error. Please check your Network and try again.";
                        ErrorTitle = "Network Error";
                    } else if (error instanceof ParseError) {
                        ErrorMessage = "Parse Error. Please try again.";
                        ErrorTitle = "Parse Error";
                    }

                    //   pDialog.dismiss();
                    // AndroidUtils.showNoNetworkConnectionDialog(mContext);
                    if (activity != null) {

                       /* try {
                            mfragmentDialog = new MessageOkFragmentDialog(ErrorMessage, ErrorTitle);
                            mfragmentDialog.show(activity.getFragmentManager(), "dialog");
                            mfragmentDialog.setCancelable(false);
                        } catch (Exception e) {
                        }*/

                    }
                } catch (Exception e) {


                }

            }
        });

      /* jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        queue.add(jsonObjReq);

    }


}
