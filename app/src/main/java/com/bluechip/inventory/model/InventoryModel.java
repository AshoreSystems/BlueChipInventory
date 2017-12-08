package com.bluechip.inventory.model;

public class InventoryModel  {



    public int prd_id;
    public String prd_sku;
    private String prd_desc;
    private String prd_category;
    private int prd_price;
    private int prd_quantity;


    public InventoryModel() {
    }

    public InventoryModel(int prd_id, String prd_sku, String prd_desc, String prd_category, int prd_price, int prd_quantity) {
        this.prd_id = prd_id;
        this.prd_sku = prd_sku;
        this.prd_desc = prd_desc;
        this.prd_category = prd_category;
        this.prd_price = prd_price;
        this.prd_quantity = prd_quantity;
    }

    public InventoryModel(String prd_desc, String prd_price, String prd_quantity) {

        this.prd_desc = prd_desc;
        this.prd_price = Integer.parseInt(prd_price);
        this.prd_quantity = Integer.parseInt(prd_quantity);


    }


    public int getPrd_id() {
        return prd_id;
    }

    public void setPrd_id(int prd_id) {
        this.prd_id = prd_id;
    }

    public String getPrd_sku() {
        return prd_sku;
    }

    public void setPrd_sku(String prd_sku) {
        this.prd_sku = prd_sku;
    }

    public String getPrd_desc() {
        return prd_desc;
    }

    public void setPrd_desc(String prd_desc) {
        this.prd_desc = prd_desc;
    }

    public String getPrd_category() {
        return prd_category;
    }

    public void setPrd_category(String prd_category) {
        this.prd_category = prd_category;
    }

    public int getPrd_price() {
        return prd_price;
    }

    public void setPrd_price(int prd_price) {
        this.prd_price = prd_price;
    }

    public int getPrd_quantity() {
        return prd_quantity;
    }

    public void setPrd_quantity(int prd_quantity) {
        this.prd_quantity = prd_quantity;
    }
}
