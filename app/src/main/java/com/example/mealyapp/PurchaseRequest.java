package com.example.mealyapp;

public class PurchaseRequest {

    String meal;
    String status;

    public PurchaseRequest() {
    }

    public PurchaseRequest(String mealName) {

        this.meal=mealName;
        this.status= "pending";
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getStatus() {

        return status;
    }

    public String getMeal() {
        return meal;
    }

    public void setStatus(String status) {

        this.status = status;
    }
}
