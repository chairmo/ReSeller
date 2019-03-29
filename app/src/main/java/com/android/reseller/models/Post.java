package com.android.reseller.models;


import java.util.List;

public class Post {

    private String description;
    private String itemName;
    private List<String> category;
    private List<String> messageThreads;
    private String condition;
    private double price;
    private List<Offer> offers;
    private String image;
    private String owner;

    //default constructor
    public Post() { }

    public Post(String description, String itemName, List<String> category, String condition, double price,
                String image, String owner, List<String> messageThreads, List<Offer> offers) {
        this.description = description;
        this.itemName = itemName;
        this.category = category;
        this.condition = condition;
        this.price = price;
        this.image = image;
    }

    //Getters
    public String getDescription() {
        return description;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getCategory() {
        return category;
    }

    public List<String> getMessageThreads() {
        return messageThreads;
    }

    public String getCondition() {
        return condition;
    }

    public double getPrice() {
        return price;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public String getImage() {
        return image;
    }

    public String getOwner() {
        return owner;
    }

    //Setters
    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public void setItemName(String newItemName) {
        itemName = newItemName;
    }

    public void setCategory(List<String> newCategory) {
        category = newCategory;
    }

    public void setMessageThreads(List<String> messageThreads) {
        this.messageThreads = messageThreads;
    }

    public void setCondition(String newCondition) {
        condition = newCondition;
    }

    public void setPrice(double newPrice) {
        price = newPrice;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public void addOffer(Offer newOffer) {
        offers.add(newOffer);
    }

    public void setImage(String newImage) {
        image = newImage;
    }

    public void setOwner(String newOwner) {
        owner = newOwner;
    }
}