package hcmute.edu.vn.mssv18110328.models;

public class Bill {
    public Bill(int id, String address, double totalPrice, int userId, String status, String phone) {
        this.id = id;
        this.address = address;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.status = status;
        this.phone = phone;
    }

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

    private int id;
    private String address;
    private double totalPrice;

    public Bill(String address, double totalPrice, int userId, String status, String phone) {
        this.address = address;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.status = status;
        this.phone = phone;
    }

    private int userId;
    private String status;
    private String phone;
}
