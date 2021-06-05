package hcmute.edu.vn.mssv18110328.models;

public class BillDetail {
    public BillDetail(int id, int productId, int quantity, Double price, int billId) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.billId = billId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    private int id;

    public BillDetail(int productId, int quantity, double price, int billId) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.billId = billId;
    }

    private int productId;
    private int quantity;
    private double price;
    private int billId;
}
