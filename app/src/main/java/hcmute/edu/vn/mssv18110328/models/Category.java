package hcmute.edu.vn.mssv18110328.models;

public class Category {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private int id;
    private String name;
    private String content;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    private byte[] image;


    public Category(String name, String content, byte[] image) {
        this.name = name;
        this.content = content;
        this.image = image;
    }


    public Category(int id, String name, String content, byte[] image) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.image = image;
    }

    public Category(){ }

}
