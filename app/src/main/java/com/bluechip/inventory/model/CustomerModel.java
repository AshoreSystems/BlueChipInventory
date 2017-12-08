package com.bluechip.inventory.model;

/**
 * Created by aspl on 5/12/17.
 */
public class CustomerModel {
    private int customer_id;
    private String customer_updated_date;

    public CustomerModel() {
    }


    public CustomerModel(int customer_id, String customer_updated_date) {
        this.customer_id = customer_id;
        this.customer_updated_date = customer_updated_date;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_updated_date() {
        return customer_updated_date;
    }

    public void setCustomer_updated_date(String customer_updated_date) {
        this.customer_updated_date = customer_updated_date;
    }
}
