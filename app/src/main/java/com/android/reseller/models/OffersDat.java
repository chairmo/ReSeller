package com.android.reseller.models;

/**
 * Created by Chairmo on 3/4/2018.
 */

public class OffersDat
{
    public String offerby, offerfor,itemid;
    public long offerprice;

    public OffersDat()
    {

    }

    public OffersDat(String ob, String of, String id, long op)
    {
        this.offerby = ob;
        this.offerprice = op;
        this.itemid = id;
        this.offerfor = of;
    }

}