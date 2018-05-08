package com.app.fakegps.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.fakegps.R;
import com.app.fakegps.model.FavLocation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    public static final String KEY_EVENT = "key_event";
    private List<FavLocation> Displayedplaces;
    private List<FavLocation> Originalplaces;
    private LayoutInflater inflater;
    @SuppressWarnings("unused")
    private Activity activity;
    private String call_from;

    private PopupWindow popupwindow_obj;

    private String alarm_fire_time;

    private int lastPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_sub_title;
        TextView tv_day;
        TextView tv_month;
        LinearLayout ll_body;
        LinearLayout ll_container;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
        }
    }


    public ReminderAdapter(Activity activity, List<FavLocation> attendees) {
        this.activity = activity;
        this.Displayedplaces = attendees;
        this.Originalplaces = attendees;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_reminder_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FavLocation event = Displayedplaces.get(position);
        holder.tv_title.setText(event.getLocationName());

    }



    @Override
    public int getItemCount() {
        return Displayedplaces.size();
    }



    private ProgressDialog progressDialog;
    public void showProgressDialog(String message, boolean isIntermidiate, boolean isCancelable) {
       /**/
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog.setIndeterminate(isIntermidiate);
        progressDialog.setCancelable(isCancelable);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog == null) {
            return;
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}