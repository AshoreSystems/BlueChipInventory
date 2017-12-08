package com.bluechip.inventory.model;

public class AuditorModel {


    public int auditor_id;
    public String auditor_assigned_jobs;
    private String auditor_email;

    public AuditorModel() {
    }


    public AuditorModel(int auditor_id, String auditor_assigned_jobs, String auditor_email) {
        this.auditor_id = auditor_id;
        this.auditor_assigned_jobs = auditor_assigned_jobs;
        this.auditor_email = auditor_email;
    }


    public int getAuditor_id() {
        return auditor_id;
    }

    public void setAuditor_id(int auditor_id) {
        this.auditor_id = auditor_id;
    }

    public String getAuditor_assigned_jobs() {
        return auditor_assigned_jobs;
    }

    public void setAuditor_assigned_jobs(String auditor_assigned_jobs) {
        this.auditor_assigned_jobs = auditor_assigned_jobs;
    }

    public String getAuditor_email() {
        return auditor_email;
    }

    public void setAuditor_email(String auditor_email) {
        this.auditor_email = auditor_email;
    }
}
