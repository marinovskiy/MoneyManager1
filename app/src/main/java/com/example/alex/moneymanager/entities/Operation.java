package com.example.alex.moneymanager.entities;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Operation extends RealmObject {

    @PrimaryKey
    private String id;

    private String type;

    private long createdAt;

    private double sum;

    private String category;

    private String description;

    public Operation() {
    }

    public Operation(String id, String type, long createdAt, double sum, String category, String description) {
        this.id = id;
        this.type = type;
        this.createdAt = createdAt;
        this.sum = sum;
        this.category = category;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", sum=" + sum +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("type", type);
        result.put("createdAt", createdAt);
        result.put("sum", sum);
        result.put("category", category);
        result.put("description", description);

        return result;
    }
}