package com.example.alex.moneymanager.entities;

public class CategoryUi {

    private String type;

    private String name;

    private String sum;

    public CategoryUi() {
    }

    public CategoryUi(String name, String sum) {
        this.name = name;
        this.sum = sum;
    }

    public CategoryUi(String type, String name, String sum) {
        this.type = type;
        this.name = name;
        this.sum = sum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}