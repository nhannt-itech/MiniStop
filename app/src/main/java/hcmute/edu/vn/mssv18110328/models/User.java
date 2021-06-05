package hcmute.edu.vn.mssv18110328.models;

public class User {


    public User(int id, String name, String username, String email, String password, byte[] image) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public User(String name, String username, String email, String password, byte[] image) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public User()
    {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    private int id;
    private String name;
    private String username;
    private String email;
    private String password;
    private byte[] image;
}
