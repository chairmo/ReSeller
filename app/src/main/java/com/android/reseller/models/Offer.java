package com.android.reseller.models;

public class Offer {

    private String offerPrice;
    private String message;
    private String buyerId;
    private String sellerId;

    public Offer() {
        offerPrice = "";
        message = "";
        buyerId = "";
        sellerId = "";
    }

    public Offer(String offerPrice, String message, String buyerId, String sellerId) {
        this.offerPrice = offerPrice;
        this.message = message;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
    }

    public Offer(double offerPrice, String message, String buyerId, String sellerId) {
        this.offerPrice = String.valueOf(offerPrice);
        this.message = message;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
    }


    public String getOfferPrice() {
        return offerPrice;
    }

    public String getMessage() {
        return message;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
