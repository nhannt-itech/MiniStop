package hcmute.edu.vn.mssv18110328;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import hcmute.edu.vn.mssv18110328.models.Bill;
import hcmute.edu.vn.mssv18110328.models.BillDetail;
import hcmute.edu.vn.mssv18110328.models.Brand;
import hcmute.edu.vn.mssv18110328.models.Category;
import hcmute.edu.vn.mssv18110328.models.Product;
import hcmute.edu.vn.mssv18110328.models.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TAG = "DatabaseHelper";
    private final Context myContext;
    private static final String DATABASE_NAME = "MiniStopDB.db";
    private static final int DATABASE_VERSION = 2;
    private String pathToSaveDBFile;
    public DatabaseHelper(Context context, String filePath) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        pathToSaveDBFile = new StringBuffer(filePath).append("/").append(DATABASE_NAME).toString();
    }
    public void prepareDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        if(dbExist) {
            Log.d(TAG, "Database exists.");
            int currentDBVersion = getVersionId();
            if (DATABASE_VERSION > currentDBVersion) {
                Log.d(TAG, "Database version is higher than old.");
                deleteDb();
                try {
                    copyDataBase();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } else {
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            File file = new File(pathToSaveDBFile);
            checkDB = file.exists();
        } catch(SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }
    private void copyDataBase() throws IOException {
        OutputStream os = new FileOutputStream(pathToSaveDBFile);
        InputStream is = myContext.getAssets().open("sqlite/"+DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
    }
    public void deleteDb() {
        File file = new File(pathToSaveDBFile);
        if(file.exists()) {
            file.delete();
            Log.d(TAG, "Database deleted.");
        }
    }
    private int getVersionId() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT version_id FROM dbVersion";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int v =  cursor.getInt(0);
        db.close();
        return v;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "onCreate");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<Category> getCategories() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM Category";
        Cursor cursor = db.rawQuery(query, null);
        List<Category> list = new ArrayList<Category>();
        while(cursor.moveToNext()) {
            Category category = new Category(Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id"))),
                    cursor.getString(cursor.getColumnIndex("Name")),
                    cursor.getString(cursor.getColumnIndex("Content")),
                    cursor.getBlob(cursor.getColumnIndex("Image")));
            list.add(category);
        }
        db.close();
        return list;
    }

    public Category getCategory(int categoryId) {
        Category category = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM Category WHERE Id = " + categoryId;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            category = new Category(Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id"))),
                    cursor.getString(cursor.getColumnIndex("Name")),
                    cursor.getString(cursor.getColumnIndex("Content")),
                    cursor.getBlob(cursor.getColumnIndex("Image")));
        }
        cursor.close();
        db.close();
        return category;
    }

    public  boolean addCategory(Category category) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Name", category.getName());
        values.put("Content", category.getContent());
        values.put("Image", category.getImage());
        boolean createSuccessful = db.insert("Category", null, values) > 0;
        db.close();
        return createSuccessful;
    }

    public  boolean updateCategory(Category category) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Name", category.getName());
        values.put("Content", category.getContent());
        values.put("Image", category.getImage());
        boolean updateSuccessful = db.update("Category", values,
                "Id = ?", new String[]{ String.valueOf(category.getId()) }) > 0;
        db.close();
        return updateSuccessful;
    }

    public int countCategory() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String sql = "SELECT * FROM Category";
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();
        return recordCount;
    }

    public boolean deleteCategory(int id) {
        boolean deleteSuccessful = false;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        deleteSuccessful = db.delete("Category", "Id ='" + id + "'", null) > 0;
        db.close();
        return deleteSuccessful;
    }

    public Product getProduct(int productId) {
        Product product = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM Product WHERE Id = " + productId;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            product = new Product(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getBlob(5),
                    cursor.getInt(6),
                    cursor.getInt(7));
        }
        cursor.close();
        db.close();
        return product;
    }

    public List<Product> getProducts() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM Product";
        Cursor cursor = db.rawQuery(query, null);
        List<Product> list = new ArrayList<Product>();
        while(cursor.moveToNext()) {
            Product product = new Product(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getBlob(5),
                    cursor.getInt(6),
                    cursor.getInt(7));
            list.add(product);
        }
        db.close();
        return list;
    }

    public List<Product> getProductsByCategoryId(int categoryId) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM Product WHERE CategoryId = " + categoryId;
        Cursor cursor = db.rawQuery(query, null);
        List<Product> list = new ArrayList<Product>();
        while(cursor.moveToNext()) {
            Product product = new Product(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getBlob(5),
                    cursor.getInt(6),
                    cursor.getInt(7));
            list.add(product);
        }
        db.close();
        return list;
    }

    public  boolean addProduct(Product product) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Name", product.getName());
        values.put("Price", product.getPrice());
        values.put("Content", product.getContent());
        values.put("Stock", product.getStock());
        values.put("Image", product.getImage());
        values.put("CategoryId", product.getCategoryId());
        values.put("BrandId", product.getBrandId());
        boolean createSuccessful = db.insert("Product", null, values) > 0;
        db.close();
        return createSuccessful;
    }

    public  boolean updateProduct(Product product) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Name", product.getName());
        values.put("Price", product.getPrice());
        values.put("Content", product.getContent());
        values.put("Stock", product.getStock());
        values.put("Image", product.getImage());
        values.put("CategoryId", product.getCategoryId());
        values.put("BrandId", product.getBrandId());
        boolean updateSuccessful = db.update("Product", values,
                "Id = ?", new String[]{ String.valueOf(product.getId()) }) > 0;
        db.close();
        return updateSuccessful;
    }

    public int countProduct() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String sql = "SELECT * FROM Product";
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();
        return recordCount;
    }

    public boolean deleteProduct(int id) {
        boolean deleteSuccessful = false;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        deleteSuccessful = db.delete("Product", "Id ='" + id + "'", null) > 0;
        db.close();
        return deleteSuccessful;
    }

    public Brand getBrand(int brandId) {

        Brand brand = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM Brand WHERE Id = " + brandId;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            brand = new Brand(cursor.getInt(0),
                    cursor.getString(1));
        }
        cursor.close();
        db.close();
        return brand;
    }

    public List<Brand> getBrands() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM Brand";
        Cursor cursor = db.rawQuery(query, null);
        List<Brand> list = new ArrayList<Brand>();
        while(cursor.moveToNext()) {
            Brand brand = new Brand(cursor.getInt(0),
                    cursor.getString(1));
            list.add(brand);
        }
        db.close();
        return list;
    }

    public  boolean addBrand(Brand brand) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Name", brand.getName());
        boolean createSuccessful = db.insert("Brand", null, values) > 0;
        db.close();
        return createSuccessful;
    }

    public  boolean updateBrand(Brand brand) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Name", brand.getName());
        boolean updateSuccessful = db.update("Brand", values,
                "Id = ?", new String[]{ String.valueOf(brand.getId()) }) > 0;
        db.close();
        return updateSuccessful;
    }

    public int countBrand() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String sql = "SELECT * FROM Brand";
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();
        return recordCount;
    }

    public boolean deleteBrand(int id) {
        boolean deleteSuccessful = false;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        deleteSuccessful = db.delete("Brand", "Id ='" + id + "'", null) > 0;
        db.close();
        return deleteSuccessful;
    }

    public BillDetail getBillDetail(int id) {
        BillDetail billDetail = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM BillDetail WHERE Id = " + id;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            billDetail = new BillDetail(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getDouble(3),
                    cursor.getInt(4));
        }
        cursor.close();
        db.close();
        return billDetail;
    }

    public List<BillDetail> getBillDetailByBillId(int billId) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM BillDetail WHERE BillId = " + billId;
        Cursor cursor = db.rawQuery(query, null);
        List<BillDetail> list = new ArrayList<BillDetail>();
        while(cursor.moveToNext()) {
            BillDetail billDetail = new BillDetail(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getDouble(3),
                    cursor.getInt(4));
            list.add(billDetail);
        }
        cursor.close();
        db.close();
        return list;
    }

    public BillDetail getBillDetailByBillIdProductId(int billId, int productId) {
        BillDetail billDetail = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM BillDetail WHERE BillId = " + billId + " AND ProductId = " +productId;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            billDetail = new BillDetail(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getDouble(3),
                    cursor.getInt(4));
        }
        cursor.close();
        db.close();
        return billDetail;
    }

    public List<BillDetail> getBillDetails() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM BillDetail";
        Cursor cursor = db.rawQuery(query, null);
        List<BillDetail> list = new ArrayList<BillDetail>();
        while(cursor.moveToNext()) {
            BillDetail billDetail = new BillDetail(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getDouble(3),
                    cursor.getInt(4));
            list.add(billDetail);
        }
        db.close();
        return list;
    }

    public boolean isBillHasProduct(int billId, int productId) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String sql = "SELECT * FROM BillDetail WHERE BillId = " + billId + " AND ProductId = " +productId;
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();
        return (recordCount>0);
    }

    public  boolean addBillDetail(BillDetail billDetail) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("ProductId", billDetail.getProductId());
        values.put("Quantity", billDetail.getQuantity());
        values.put("Price", billDetail.getPrice());
        values.put("BillId", billDetail.getBillId());
        boolean createSuccessful = db.insert("BillDetail", null, values) > 0;
        db.close();
        return createSuccessful;
    }

    public  boolean updateBillDetail(BillDetail billDetail) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("ProductId", billDetail.getProductId());
        values.put("Quantity", billDetail.getQuantity());
        values.put("Price", billDetail.getPrice());
        values.put("BillId", billDetail.getBillId());
        boolean updateSuccessful = db.update("BillDetail", values,
                "Id = ?", new String[]{ String.valueOf(billDetail.getId()) }) > 0;
        db.close();
        return updateSuccessful;
    }

    public boolean deleteBillDetail(int id) {
        boolean deleteSuccessful = false;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        deleteSuccessful = db.delete("BillDetail", "Id ='" + id + "'", null) > 0;
        db.close();
        return deleteSuccessful;
    }

    public Bill getBill(int id) {
        Bill bill = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM Bill WHERE Id = " + id;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            bill = new Bill(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5));
        }
        cursor.close();
        db.close();
        return bill;
    }

    public Bill getBillByUserId(int userId) {
        Bill bill = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM Bill WHERE UserId = " + userId;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            bill = new Bill(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5));
        }
        cursor.close();
        db.close();
        return bill;
    }

    public Bill getIncompleteBillByUserId(int userId) {
        Bill bill = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM Bill WHERE Status = 'incomplete' AND UserId = " + userId;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            bill = new Bill(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5));
        }
        cursor.close();
        db.close();
        return bill;
    }

    public List<Bill> getBills() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM Bill";
        Cursor cursor = db.rawQuery(query, null);
        List<Bill> list = new ArrayList<Bill>();
        while(cursor.moveToNext()) {
            Bill bill = new Bill(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5));
            list.add(bill);
        }
        db.close();
        return list;
    }

    public boolean isUserHasCart(int userId) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String sql = "SELECT * FROM Bill WHERE Status = 'incomplete' AND UserId =" + userId;
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();
        return (recordCount>0);
    }

    public  boolean addBill(Bill bill) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Address", bill.getAddress());
        values.put("TotalPrice", bill.getTotalPrice());
        values.put("UserId", bill.getUserId());
        values.put("Status", bill.getStatus());
        values.put("Phone", bill.getPhone());
        boolean createSuccessful = db.insert("Bill", null, values) > 0;
        db.close();
        return createSuccessful;
    }

    public  boolean updateBill(Bill bill) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Address", bill.getAddress());
        values.put("TotalPrice", bill.getTotalPrice());
        values.put("UserId", bill.getUserId());
        values.put("Status", bill.getStatus());
        values.put("Phone", bill.getPhone());
        boolean updateSuccessful = db.update("Bill", values,
                "Id = ?", new String[]{ String.valueOf(bill.getId()) }) > 0;
        db.close();
        return updateSuccessful;
    }

    public boolean deleteBill(int id) {
        boolean deleteSuccessful = false;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        deleteSuccessful = db.delete("BillDetail", "Id ='" + id + "'", null) > 0;
        db.close();
        return deleteSuccessful;
    }

    public User getUser(int userId) {

        User user = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM User WHERE Id = " + userId;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            user = new User(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getBlob(5));
        }
        cursor.close();
        db.close();
        return user;
    }

    public User getUser(String email) {
        User user = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "SELECT * FROM User WHERE Email = '" + email + "'";
        Cursor cursor = db.rawQuery(sql, null);
        int recordCount = db.rawQuery(sql, null).getCount();
        if (cursor.moveToFirst()) {
            user = new User(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getBlob(5));
        }
        cursor.close();
        db.close();
        return user;
    }

    public List<User> getUsers() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM User";
        Cursor cursor = db.rawQuery(query, null);
        List<User> list = new ArrayList<User>();
        while(cursor.moveToNext()) {
            User user = new User(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getBlob(5));
            list.add(user);
        }
        db.close();
        return list;
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Name", user.getName());
        values.put("Username", user.getUsername()  );
        values.put("Email", user.getEmail());
        values.put("Password", md5(user.getPassword()));
        values.put("Image", user.getImage());
        boolean createSuccessful = db.insert("User", null, values) > 0;
        db.close();
        return createSuccessful;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("Name", user.getName());
        values.put("Username", user.getUsername()  );
        values.put("Email", user.getEmail());
        values.put("Password", md5(user.getPassword()));
        values.put("Image", user.getImage());
        boolean updateSuccessful = db.update("User", values,
                "Id = ?", new String[]{ String.valueOf(user.getId()) }) > 0;
        db.close();
        return updateSuccessful;
    }

    public int countUser() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String sql = "SELECT * FROM User";
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();
        return recordCount;
    }


    public boolean usernameExists(String username) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String sql = "SELECT * FROM User WHERE Username = '" + username + "'";
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();
        if (recordCount > 0)
            return true;
        else
            return false;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String sql = "SELECT * FROM User WHERE Email = '" + email + "'";
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();
        if (recordCount > 0)
            return true;
        else
            return false;
    }

    public boolean loginIsSuccess(String email, String password)
    {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String sql = "SELECT * FROM User WHERE Email = '" + email + "'" + " AND Password = '" + md5(password) +"'" ;
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();
        if (recordCount > 0)
            return true;
        else
            return false;
    }

    public String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
