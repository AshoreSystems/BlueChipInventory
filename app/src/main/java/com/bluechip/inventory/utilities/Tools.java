package com.bluechip.inventory.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

	
	public static void hideSoftKeyboard(Activity activity) {
	    try {
			InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getHeightPixels(Activity activity)
	{
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width=dm.widthPixels;
		int height=dm.heightPixels;
		
		return height;
	}
	
	public static String passwordUpperLowerValidation="^(?=(?:\\D*\\d){1})(?=(?:[^a-z]*[a-z]){1})(?=(?:[^A-Z]*[A-Z]){1}).{20,}$";
	
	public static boolean IsMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
          return false;
        }  
	}

	public static  boolean isLegalPassword(String pass) {

	     if (!pass.matches(".*[A-Z].*")) return false;

	     if (!pass.matches(".*[a-z].*")) return false;

	     if (!pass.matches(".*\\d.*")) return false;

	     return true;
	}
	
	
	public static String getDeviceChipSet(String chipSet)
	{
		String pedometerDeviceWhichChip="ti";
		if(chipSet!=null && !chipSet.isEmpty())
		{
			if(chipSet.equalsIgnoreCase("01"))
			{
				pedometerDeviceWhichChip="nordic";
			}
			else
			{			
				pedometerDeviceWhichChip="ti";
			}
		}
		return pedometerDeviceWhichChip;
		
	}

	public static boolean appGoogleFitInstalledOrNot(Context context) {
		PackageManager pm = context.getPackageManager();
		boolean app_installed;
		try {
			pm.getPackageInfo("com.google.android.apps.fitness", PackageManager.GET_ACTIVITIES);
			app_installed = true;
		}
		catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}


}
