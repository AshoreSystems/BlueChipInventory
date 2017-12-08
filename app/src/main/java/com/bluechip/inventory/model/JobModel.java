package com.bluechip.inventory.model;

public class JobModel {


    public int job_id;
    public int job_auditor_id;
    public int job_cust_id;
    public String job_added_date;
    public String job_one_at;

    //area
    public String area_id;
    public String area_name;
    //sub_area
    public String sub_area_id;
    public String sub_area_name;
    //section
    public String sec_id;
    public String sec_name;
    //sub_section
    public String sub_section_id;
    public String sub_section_name;
    //location
    public String location_id;
    public String location_address;
    public String facility_name;


    public JobModel() {

    }


    public JobModel(int job_id, int job_cust_id, String job_added_date, String job_one_at, String area_id, String area_name, String sub_area_id, String sub_area_name, String sec_id, String sec_name, String sub_section_id, String sub_section_name, String location_id, String location_address, String facility_name) {
        this.job_id = job_id;
        this.job_cust_id = job_cust_id;
        this.job_added_date = job_added_date;
        this.job_one_at = job_one_at;
        this.area_id = area_id;
        this.area_name = area_name;
        this.sub_area_id = sub_area_id;
        this.sub_area_name = sub_area_name;
        this.sec_id = sec_id;
        this.sec_name = sec_name;
        this.sub_section_id = sub_section_id;
        this.sub_section_name = sub_section_name;
        this.location_id = location_id;
        this.location_address = location_address;
        this.facility_name = facility_name;
    }

    public JobModel(String location_address, String area_name) {
        this.location_address = location_address;
        this.area_name = area_name;

    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public int getJob_auditor_id() {
        return job_auditor_id;
    }

    public void setJob_auditor_id(int job_auditor_id) {
        this.job_auditor_id = job_auditor_id;
    }

    public int getJob_cust_id() {
        return job_cust_id;
    }

    public void setJob_cust_id(int job_cust_id) {
        this.job_cust_id = job_cust_id;
    }

    public String getJob_added_date() {
        return job_added_date;
    }

    public void setJob_added_date(String job_added_date) {
        this.job_added_date = job_added_date;
    }

    public String getJob_one_at() {
        return job_one_at;
    }

    public void setJob_one_at(String job_one_at) {
        this.job_one_at = job_one_at;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getSub_area_id() {
        return sub_area_id;
    }

    public void setSub_area_id(String sub_area_id) {
        this.sub_area_id = sub_area_id;
    }

    public String getSub_area_name() {
        return sub_area_name;
    }

    public void setSub_area_name(String sub_area_name) {
        this.sub_area_name = sub_area_name;
    }

    public String getSec_id() {
        return sec_id;
    }

    public void setSec_id(String sec_id) {
        this.sec_id = sec_id;
    }

    public String getSec_name() {
        return sec_name;
    }

    public void setSec_name(String sec_name) {
        this.sec_name = sec_name;
    }

    public String getSub_section_id() {
        return sub_section_id;
    }

    public void setSub_section_id(String sub_section_id) {
        this.sub_section_id = sub_section_id;
    }

    public String getSub_section_name() {
        return sub_section_name;
    }

    public void setSub_section_name(String sub_section_name) {
        this.sub_section_name = sub_section_name;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getLocation_address() {
        return location_address;
    }

    public void setLocation_address(String location_address) {
        this.location_address = location_address;
    }

    public String getFacility_name() {
        return facility_name;
    }

    public void setFacility_name(String facility_name) {
        this.facility_name = facility_name;
    }
}
