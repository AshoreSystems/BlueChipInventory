package com.bluechip.inventory.adapter;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluechip.inventory.R;
import com.bluechip.inventory.fragment.JobsFragment;
import com.bluechip.inventory.model.InventoryModel;

import java.util.List;

public class CardViewInventoryAdapter extends
        RecyclerView.Adapter<CardViewInventoryAdapter.ViewHolder> {

    private List<InventoryModel> inventoryList;
    public JobsFragment jobsFragment;

    public CardViewInventoryAdapter(List<InventoryModel> inventoryList, JobsFragment jobsFragment) {
        this.inventoryList = inventoryList;
        this.jobsFragment = jobsFragment;
    }



    // Create new views
    @Override
    public CardViewInventoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_item_inventory, parent, false);

/*
        LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_listitem, parent, false);*/

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int pos = position;


        viewHolder.textView_inventory_title.setText(inventoryList.get(position).getPrd_desc());
        viewHolder.textView_quantity.setText(""+inventoryList.get(position).getPrd_quantity());
        viewHolder.textView_price.setText(" $ "+ inventoryList.get(position).getPrd_price());

        viewHolder.linearlay_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jobsFragment.editInventory(""+inventoryList.get(pos).getPrd_id(),
                        inventoryList.get(pos).getPrd_sku(),
                        inventoryList.get(pos).getPrd_quantity(),
                        inventoryList.get(pos).getPrd_price());

            }
        });

        if(inventoryList.get(position).getPrd_desc().equalsIgnoreCase("")){

            viewHolder.cardView_layout.setVisibility(View.INVISIBLE);



        }else{

            viewHolder.cardView_layout.setVisibility(View.VISIBLE);

        }


    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView_inventory_title;
        public TextView textView_price;
        public TextView textView_quantity;
        public LinearLayout linearlay_edit;

        public ImageView imageView_price,imageView_quantity;
        public View view_line;

        public CardView cardView_layout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            textView_inventory_title = (TextView) itemLayoutView.findViewById(R.id.textView_inventory_title);

            textView_price = (TextView) itemLayoutView.findViewById(R.id.textView_price);
            textView_quantity = (TextView) itemLayoutView.findViewById(R.id.textView_quantity);
            linearlay_edit= (LinearLayout) itemLayoutView.findViewById(R.id.linearlay_edit);
             cardView_layout= (CardView) itemLayoutView.findViewById(R.id.cardView_layout);


        }

    }

    // method to access in activity after updating selection
    public List<InventoryModel> getStudentist() {
        return inventoryList;
    }

}