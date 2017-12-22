package com.bluechip.inventory.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluechip.inventory.R;
import com.bluechip.inventory.fragment.JobsFragment;
import com.bluechip.inventory.model.JobModel;
import com.bluechip.inventory.utilities.Tools;

import java.util.List;

public class CardViewJobAdapter extends
        RecyclerView.Adapter<CardViewJobAdapter.ViewHolder> {

    private List<JobModel> stList;
    public JobsFragment jobsFragment;


    String area, sub_area, section, sub_section;

    public CardViewJobAdapter(List<JobModel> jobModels, JobsFragment jobsFragment) {
        this.stList = jobModels;
        this.jobsFragment = jobsFragment;
    }

    public CardViewJobAdapter(List<JobModel> jobModels) {
        this.stList = jobModels;

    }

    // Create new views
    @Override
    public CardViewJobAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_item_job, parent, false);

/*
        LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_listitem, parent, false);*/

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.textView_job_location.setText(stList.get(position).getLocation_address());
        viewHolder.textView_facility_name.setText(stList.get(position).getFacility_name());
        //viewHolder.textView_area.setText(stList.get(position).getArea_name());

        area = stList.get(position).getArea_name();
        sub_area = stList.get(position).getSub_area_name();
        section= stList.get(position).getSec_name();
        sub_section = stList.get(position).getSub_section_name();


        if (!sub_area .equalsIgnoreCase("")) {

            sub_area = "-" + stList.get(position).getSub_area_name();
        }
        if (!section.equalsIgnoreCase("")) {

            section = "-" + stList.get(position).getSec_name();
        }
        if (!sub_section.equalsIgnoreCase("")) {

            sub_section = "-" + stList.get(position).getSub_section_name();
        }

        String area_details = "" + area + sub_area + section + sub_section;

        viewHolder.textView_area.setText(area_details);
        String str = new Tools().stringToDate(stList.get(position).getJob_added_date());
        //viewHolder.textView_assign_date.setText(stList.get(position).getJob_added_date());
        viewHolder.textView_assign_date.setText(new Tools().stringToDate(stList.get(position).getJob_added_date()));

        viewHolder.relLay_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int job_id = stList.get(pos).getJob_id();
                jobsFragment.viewInventoryList(job_id);
            }
        });
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return stList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView_job_location;
        public TextView textView_facility_name;
        public TextView textView_assign_date;
        public TextView textView_area;
        public LinearLayout relLay_item;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            textView_job_location = (TextView) itemLayoutView.findViewById(R.id.textView_job_location);
            textView_facility_name = (TextView) itemLayoutView.findViewById(R.id.textView_facility_name);
            textView_assign_date = (TextView) itemLayoutView.findViewById(R.id.textView_assign_date);
            textView_area = (TextView) itemLayoutView.findViewById(R.id.textView_area);
            relLay_item = (LinearLayout) itemLayoutView.findViewById(R.id.relLay_item);

        }

    }

    // method to access in activity after updating selection
    public List<JobModel> getStudentist() {
        return stList;
    }

}