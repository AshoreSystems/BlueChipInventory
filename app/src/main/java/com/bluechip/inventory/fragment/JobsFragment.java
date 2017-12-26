package com.bluechip.inventory.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bluechip.inventory.R;
import com.bluechip.inventory.activity.InventoryActivity;
import com.bluechip.inventory.activity.MainActivity;
import com.bluechip.inventory.adapter.CardViewInventoryAdapter;
import com.bluechip.inventory.adapter.CardViewJobAdapter;
import com.bluechip.inventory.database.InventoryDB;
import com.bluechip.inventory.database.JobsDB;
import com.bluechip.inventory.model.InventoryModel;
import com.bluechip.inventory.model.JobModel;
import com.bluechip.inventory.utilities.AppConstant;
import com.bluechip.inventory.utilities.CustomDialog;
import com.bluechip.inventory.utilities.SessionManager;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JobsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobsFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView;
    FloatingActionButton fab_upload, fab_add;

    public LinearLayout linearLay_job_list, linearLay_inventory_list;


    private RecyclerView recycler_view_job;
    private RecyclerView recycler_view_inventory;

    private RecyclerView.Adapter mAdapter_jobs;
    private RecyclerView.Adapter mAdapter_inventory;

    private List<JobModel> jobList;
    private List<InventoryModel> inventoryList;


    private TextView editText_one_at;
    private Switch switch_one_at;
    private SessionManager session;


    public JobsFragment() {
        // Required empty public constructor
    }

    Context context;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobsFragment newInstance(String param1, String param2) {
        JobsFragment fragment = new JobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_jobs, container, false);

        initializeView();

        return rootView;

    }

    private void initializeView() {

        session = new SessionManager(context);

        // UI
        editText_one_at = (TextView) rootView.findViewById(R.id.editText_one_at);
        switch_one_at = (Switch) rootView.findViewById(R.id.switch_one_at);

        linearLay_job_list = (LinearLayout) rootView.findViewById(R.id.linearLay_job_list);
        linearLay_inventory_list = (LinearLayout) rootView.findViewById(R.id.linearLay_inventory_list);

        recycler_view_job = (RecyclerView) rootView.findViewById(R.id.recycler_view_job);
        recycler_view_inventory = (RecyclerView) rootView.findViewById(R.id.recycler_view_inventory);

        // float button
        fab_upload = (FloatingActionButton) rootView.findViewById(R.id.fab_upload);
        fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(this);
        fab_upload.setOnClickListener(this);


        AppConstant.KEY_INVENTORY_LIST = "OFF";
        refreshJobList();
        refreshInventoryList();

        hideShowList();

        /*
        linearLay_job_list.setVisibility(View.VISIBLE);
        linearLay_inventory_list.setVisibility(View.GONE);
        fab_add.setVisibility(View.GONE);
        fab_upload.setVisibility(View.GONE);

*/
        if (AppConstant.KEY_ONE_AT) {
            switch_one_at.setChecked(true);
        } else {
            switch_one_at.setChecked(false);
        }

        switch_one_at.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    editText_one_at.setTextColor(getResources().getColor(R.color.switch_green));
                    AppConstant.KEY_ONE_AT = true;
                } else {
                    editText_one_at.setTextColor(getResources().getColor(R.color.color_grey_dark_text));
                    AppConstant.KEY_ONE_AT = false;
                }

            }
        });

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {

        this.context = context;

        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab_add:

                AppConstant.ADD_INVENTORY_STATUS = "new";

                startActivity(new Intent(getActivity(), InventoryActivity.class));
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;


            case R.id.fab_upload:

                try {

                    if (((MainActivity) getActivity()).isInternetOn()) {

                        ((MainActivity) getActivity()).uploadInventory();
                    } else {
                        new CustomDialog().dialog_ok_button(context, getResources().getString(R.string.msg_enable_internet));


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
               /* DatabaseHandler DH= new DatabaseHandler(context);
                SQLiteDatabase db = DH.OpenWritable();

                JobsDB jobsDB = new JobsDB();

                jobsDB.dropAllTables(db);
*/

                break;
        }

    }

    // viewInventoryForSelectedJob
    public void viewInventoryList(int job_id) {

        int job_id_temp = job_id;

        JobModel jobModel = new JobModel();
        JobsDB jobsDB = new JobsDB();
        jobModel = jobsDB.getJobDetails(context, job_id);

        // save job details
        AppConstant.KEY_JOB_ID = jobModel.getJob_id();
        AppConstant.KEY_JOB_CUST_ID = jobModel.getJob_cust_id();
        AppConstant.KEY_JOB_LOC_ID = jobModel.getLocation_id();

        AppConstant.KEY_JOB_AREA= jobModel.getArea_name();
        AppConstant.KEY_JOB_SUB_AREA= jobModel.getSub_area_name();
        AppConstant.KEY_JOB_SECTION= jobModel.getSec_name();
        AppConstant.KEY_JOB_SUB_SECTION= jobModel.getSub_section_name();

        // auditor_job_table
        String table_inventory_auditor_job = "table_inventory_aud"
                + session.getString(session.KEY_AUDITOR_ID).toString()
                + "_job"
                + job_id;


        //customer_master_inventory_table
        String table_inventory_master = "table_master_costumer" + jobModel.getJob_cust_id();

        session.putString(session.KEY_AUDITOR_JOB_TABLE_NAME, table_inventory_auditor_job);
        session.putString(session.KEY_MASTER_TABLE_NAME, table_inventory_master);

        String table_name = session.getString(session.KEY_AUDITOR_JOB_TABLE_NAME);
        String table_master = session.getString(session.KEY_MASTER_TABLE_NAME);

        AppConstant.KEY_INVENTORY_LIST = "ON";
        refreshInventoryList();
        hideShowList();
    }

    public void editInventory(String prd_desc, String prd_sku, int prd_quantity, int prd_price) {

        AppConstant.ADD_INVENTORY_STATUS = "edit";
        AppConstant.KEY_PRD_DESC = prd_desc;
        AppConstant.KEY_PRD_SKU = prd_sku;
        AppConstant.KEY_PRD_QUANTITY = prd_quantity;
        AppConstant.KEY_PRD_PRICE = prd_price;

        startActivity(new Intent(getActivity(), InventoryActivity.class));
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshJobAndInventoryList();
        hideShowList();
    }

    public void hideShowList() {


        if (AppConstant.KEY_INVENTORY_LIST.equalsIgnoreCase("ON")) {

            linearLay_job_list.setVisibility(View.GONE);
            linearLay_inventory_list.setVisibility(View.VISIBLE);
            fab_add.setVisibility(View.VISIBLE);
            fab_upload.setVisibility(View.VISIBLE);

        } else {
            linearLay_job_list.setVisibility(View.VISIBLE);
            linearLay_inventory_list.setVisibility(View.GONE);
            fab_add.setVisibility(View.GONE);
            fab_upload.setVisibility(View.GONE);
        }

        ((MainActivity) getActivity()).setToolbarTitle();
    }


    private void refreshJobAndInventoryList() {
        refreshJobList();
        refreshInventoryList();
    }


    // job list
    private void refreshJobList() {

        // job list
        JobsDB jobsDB = new JobsDB();

        if (jobList != null) {
            jobList = null;
        }

        jobList = jobsDB.getJobList(session.getString(session.KEY_AUDITOR_ID), context);

        recycler_view_job.setHasFixedSize(true);
        recycler_view_job.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mAdapter_jobs != null) {
            mAdapter_jobs = null;
        }
        try {
            // set the adapter object to the Recyclerview

            mAdapter_jobs = new CardViewJobAdapter(jobList, JobsFragment.this);
            mAdapter_jobs.notifyDataSetChanged();
            recycler_view_job.setAdapter(mAdapter_jobs);
        }catch (Exception e){e.printStackTrace();}
    }


    //   inventory list
    private void refreshInventoryList() {

        String table_name = session.getString(session.KEY_AUDITOR_JOB_TABLE_NAME);

        if (!table_name.isEmpty() || !table_name.equalsIgnoreCase("")) {
            InventoryDB inventoryDB = new InventoryDB();

            if (inventoryList != null) {
                inventoryList = null;
            }

            inventoryList = inventoryDB.getInventoryList(table_name, context);

            recycler_view_inventory.setHasFixedSize(true);
            recycler_view_inventory.setLayoutManager(new LinearLayoutManager(getActivity()));
            recycler_view_inventory.removeAllViewsInLayout();

            if (mAdapter_inventory != null) {
                mAdapter_inventory = null;


            }


            try {
                mAdapter_inventory = new CardViewInventoryAdapter(inventoryList, JobsFragment.this);
                mAdapter_inventory.notifyDataSetChanged();
                recycler_view_inventory.setAdapter(mAdapter_inventory);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
