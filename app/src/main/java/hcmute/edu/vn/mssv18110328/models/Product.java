package hcmute.edu.vn.mssv18110328.models;

public class Product {
    public Product(int id, String name, Double price, String content, int stock, byte[] image, int categoryId, int brandId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.content = content;
        this.stock = stock;
        this.image = image;
        this.categoryId = categoryId;
        this.brandId = brandId;
    }

    private int id;
    private String name;
    private Double price;
    private String content;
    private int stock;
    private byte[] image;
    private int categoryId;
    private int brandId;

    public Product(String name, Double price, String content, int stock, byte[] image, int categoryId, int brandId) {
        this.name = name;
        this.price = price;
        this.content = content;
        this.stock = stock;
        this.image = image;
        this.categoryId = categoryId;
        this.brandId = brandId;
    }






    public Product(){
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


}
