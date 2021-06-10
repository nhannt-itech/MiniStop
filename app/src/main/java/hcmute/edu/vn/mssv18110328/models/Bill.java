package hcmute.edu.vn.mssv18110328.models;

import java.time.DateTimeException;
import java.util.Date;

public class Bill {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private int id;
    private String address;
    private double totalPrice;
    private int userId;
    private String status;
    private String date;




    public Bill(String address, double totalPrice, int userId, String status, String phone, String date) {
        this.address = address;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.status = status;
        this.phone = phone;
        this.date = date;
    }

    private String phone;

    public Bill(int id, String address, double totalPrice, int userId, String status, String phone, String date) {
        this.id = id;
        this.address = address;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.status = status;
        this.phone = phone;
        this.date = date;
    }

}
