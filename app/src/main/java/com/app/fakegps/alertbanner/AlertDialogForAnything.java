package com.app.fakegps.alertbanner;

/**
 * Created by jubayer on 5/9/2017.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.fakegps.R;


public class AlertDialogForAnything {

    public AlertDialogForAnything(){

    }

    public static void showAlertDialogWhenComplte(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        alertDialog.show();
    }

    public static void showAlertDialogWithoutTitle(Context context, String message, Boolean status) {
        if(context !=null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage(message);
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
            alertDialog.show();
        }
    }

    public static void showAlertDialogForceUpdate(final Context context, String titleText, String msg, String buttonText, final String appURL) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(titleText);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (appURL.length() > 0) {
                    final String appPackageName = context.getPackageName();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            }
        });
        alertDialog.show();
    }

    public static void showAlertDialogForceUpdateFromDropBox(final Context context, String titleText, String msg, String buttonText, final String appURL) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(titleText);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

               //give the google playstore link here to make user update the APP
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        alertDialog.show();
    }


    public static final int  ALERT_TYPE_SUCCESS = 0;
    public static final int  ALERT_TYPE_ERROR = 1;


    public static void showNotifyDialog(Context context,int alert_type,String message){
        final Dialog dialog_start = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog_start.setCancelable(true);
        dialog_start.setContentView(R.layout.dialog_notify);
        LinearLayout ll_container = (LinearLayout) dialog_start.findViewById(R.id.ll_container);
        TextView tv_msg = (TextView) dialog_start.findViewById(R.id.tv_dialog_alert);

        switch (alert_type){
            case ALERT_TYPE_SUCCESS:
                ll_container.setBackgroundResource(R.drawable.background_rounded_green);
                tv_msg.setText(message);
                break;
            case ALERT_TYPE_ERROR:
                ll_container.setBackgroundResource(R.drawable.background_rounded_red);
                tv_msg.setText(message);
                break;
        }

        dialog_start.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog_start.dismiss();
            }
        }, 2000);
    }


}