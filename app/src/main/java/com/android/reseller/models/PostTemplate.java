package com.android.reseller.models;

import java.util.List;

/**
 * Created by Chairmo on 3/4/2018.
 */

public class PostTemplate {

    public String itemName,condition,description, owner, image;
    public long price;
    public List<String> category;

    public PostTemplate()
    {
    }
    public PostTemplate(String i,long p ,List<String> cat,String con,String d,String o,String img)
    {
        this.itemName = i;
        this.price = p;
        this.category = cat;
        this.condition = con;
        this.description = d;
        this.owner = o;
        this.image = img;
    }
}
