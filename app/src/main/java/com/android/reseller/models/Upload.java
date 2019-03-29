package com.android.reseller.models;

import java.util.List;

public class Upload {
    public String itemName;
    public List<String> category;
    public String image, owner,key;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() {
    }

    public Upload(String itemName, String image, List<String> category) {
        this.itemName = itemName;
        this.image = image;
        this.category = category;
    }

    public Upload(String itemName, String image, String owner) {
        this.itemName = itemName;
        this.image= image;
        this.owner = owner;
    }

    public String getName() {
        return itemName;
    }
    public String getUrl() {
        return image;
    }
    public List<String> getCategory() { return category; }
    public String getOwner(){return  owner;}
}

