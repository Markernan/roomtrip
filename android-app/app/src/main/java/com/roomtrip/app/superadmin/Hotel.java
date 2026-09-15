package com.roomtrip.app.superadmin;

import java.io.Serializable;

public class Hotel implements Serializable {
    private String name;
    private String location;
    private float rating;
    private String adminName;
    private String adminEmail;
    private String adminPhone;
    private String photoUrl;
    private int photoResId;
    private boolean isActive;

    public Hotel() {}

    public Hotel(String name, String location, float rating, String adminName, String adminEmail, String adminPhone, int photoResId, boolean isActive) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
        this.photoResId = photoResId;
        this.isActive = isActive;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getAdminPhone() { return adminPhone; }
    public void setAdminPhone(String adminPhone) { this.adminPhone = adminPhone; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public int getPhotoResId() { return photoResId; }
    public void setPhotoResId(int photoResId) { this.photoResId = photoResId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}