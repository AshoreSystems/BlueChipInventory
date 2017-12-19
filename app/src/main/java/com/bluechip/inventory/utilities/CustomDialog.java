package com.bluechip.inventory.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;

/**
 * Created by aspl on 5/12/17.
 */

public class CustomDialog {


    public void dialog_ok_button(Context context, String msg) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        dialog.getButton(Dialog.BUTTON_NEGATIVE).setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });


    }
}
