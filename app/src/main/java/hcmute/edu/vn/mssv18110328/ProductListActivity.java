package hcmute.edu.vn.mssv18110328;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.mssv18110328.adapter.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.adapter.ProductListAdapter;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.customer_area.InfoActivity;
import hcmute.edu.vn.mssv18110328.identity_area.LoginActivity;
import hcmute.edu.vn.mssv18110328.models.Bill;
import hcmute.edu.vn.mssv18110328.models.Brand;
import hcmute.edu.vn.mssv18110328.models.Category;
import hcmute.edu.vn.mssv18110328.models.Product;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_NAME;
import static hcmute.edu.vn.mssv18110328.utils.Utility.convertCompressedByteArrayToBitmap;

public class ProductListActivity extends AppCompatActivity {

    private final static String TAG = "CategoryListActivity";
    List<Map<String, Object>> dataSpinnerCategory;
    List<Map<String, Object>> dataSpinnerBrand;
    DatabaseHelper dbHelper= null;
    ImageView mImageView;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSTION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        loadData();

        Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProduct);
        TextView tvLogOut = findViewById(R.id.tvLogOut);

        tvLogOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                SharedPrefs.getInstance().put(CURRENT_ID, "");
                                SharedPrefs.getInstance().put(CURRENT_NAME, null);
                                startActivity(new Intent(ProductListActivity.this, LoginActivity.class));
                                finish();
                                Toast.makeText(getApplicationContext(), "Bạn đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProductListActivity.this);
                builder.setMessage("Đăng xuất tài khoản này?").setPositiveButton("Đăng Xuất", dialogClickListener)
                        .setNegativeButton("Hủy", dialogClickListener).show();
            }
        });

        btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showDiaglogAdd(view);
            }
        });
    }

    public void showDiaglogAdd(View view) {
        Context context = view.getRootView().getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.product_input_form, null, false);
        final EditText etName =  (EditText) formElementsView.findViewById(R.id.etName);
        final EditText etPrice = (EditText) formElementsView.findViewById(R.id.etPrice);
        final EditText etContent = (EditText) formElementsView.findViewById(R.id.etContent);
        final EditText etStock = (EditText) formElementsView.findViewById(R.id.etStock);
        final ImageView ivImage = (ImageView) formElementsView.findViewById(R.id.ivImage);
        mImageView =  (ImageView) formElementsView.findViewById(R.id.ivImage);
        final Spinner spinnerCategory = (Spinner) formElementsView.findViewById(R.id.spinCategoryId);
        final Spinner spinnerBrand = (Spinner) formElementsView.findViewById(R.id.spinBrandId);

        loadCategory_Spinner(spinnerCategory);
        loadBrand_Spinner(spinnerBrand);



        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED)
                    {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSTION_CODE);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, IMAGE_PICK_CODE);
                    }
                }
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("TẠO SẢN PHẨM")
                .setPositiveButton("Thêm", null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String bname = etName.getText().toString();
                        String bprice = etPrice.getText().toString();
                        String bcontent = etContent.getText().toString();
                        String bstock = etStock.getText().toString();

                        if (TextUtils.isEmpty(bname)) {
                            etName.requestFocus();
                            etName.setError("Bạn phải nhập tên sản phẩm!");
                        } else if (TextUtils.isEmpty(bprice)) {
                            etPrice.requestFocus();
                            etPrice.setError("Bạn phải nhập giá!");
                        } else if (TextUtils.isEmpty(bcontent)) {
                            etContent.requestFocus();
                            etContent.setError("Bạn phải nhập nội dung!");
                        } else if (TextUtils.isEmpty(bstock)) {
                            etStock.requestFocus();
                            etStock.setError("Bạn phải nhập số lượng!");
                        }
                        else{
                            String name = etName.getText().toString();
                            double price = Double.parseDouble(etPrice.getText().toString());
                            String content = etContent.getText().toString();
                            int stock = Integer.parseInt(etStock.getText().toString());
                            Map<String, Object> selectedCategory = dataSpinnerCategory.get(spinnerCategory.getSelectedItemPosition());
                            int categoryId = Integer.parseInt(selectedCategory.get("id").toString());
                            Map<String, Object> selectedBrand = dataSpinnerBrand.get(spinnerBrand.getSelectedItemPosition());
                            int brandId = Integer.parseInt(selectedBrand.get("id").toString());

                            Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                            byte[] byteivImage = getBitmapAsByteArray(bitmap);
                            List<Product> ll = dbHelper.getProducts();
                            Product product = new Product(name,price,content,stock,byteivImage,categoryId,brandId);
                            boolean createSuccessful = dbHelper.addProduct(product);

                            List<Category> count = dbHelper.getCategories();

                            if(createSuccessful){
                                Toast.makeText(context, "Tạo sản phẩm thành công.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Có lỗi khi tạo sản phẩm.", Toast.LENGTH_SHORT).show();
                            }
                            loadData();
                            dialog.cancel();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void showDiaglogUpdateDelete(View view, int id) {
        Context context = view.getContext();
        final CharSequence[] items = { "Cập nhật", "Xóa" };
        new AlertDialog.Builder(context).setTitle("Thao tác")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            editRecord(id, view);
                        }
                        else if (item == 1) {
                            boolean deleteSuccessful = dbHelper.deleteProduct(id);
                            if (deleteSuccessful){
                                Toast.makeText(context, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Bạn không thể xóa sản phẩm này!", Toast.LENGTH_SHORT).show();
                            }
                            loadData();
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    public void editRecord(int id, View view)
    {
        Context context = view.getRootView().getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View formElementsView = inflater.inflate(R.layout.product_input_form, null, false);
        final EditText etName =  (EditText) formElementsView.findViewById(R.id.etName);
        final EditText etPrice = (EditText) formElementsView.findViewById(R.id.etPrice);
        final EditText etContent = (EditText) formElementsView.findViewById(R.id.etContent);
        final EditText etStock = (EditText) formElementsView.findViewById(R.id.etStock);
        final ImageView ivImage = (ImageView) formElementsView.findViewById(R.id.ivImage);
        mImageView =  (ImageView) formElementsView.findViewById(R.id.ivImage);
        final Spinner spinnerCategory = (Spinner) formElementsView.findViewById(R.id.spinCategoryId);
        final Spinner spinnerBrand = (Spinner) formElementsView.findViewById(R.id.spinBrandId);

        Product product = dbHelper.getProduct(id);

        etName.setText(product.getName());
        etPrice.setText(product.getPrice().toString());
        etContent.setText(product.getContent());
        etStock.setText(String.valueOf(product.getStock()));
        mImageView.setImageBitmap(convertCompressedByteArrayToBitmap(product.getImage()));

        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED)
                    {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSTION_CODE);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, IMAGE_PICK_CODE);
                    }
                }
            }
        });

        loadCategory_Spinner(spinnerCategory);
        loadBrand_Spinner(spinnerBrand);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("SỬA SẢN PHẨM")
                .setPositiveButton("Lưu", null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String bname = etName.getText().toString();
                        String bprice = etPrice.getText().toString();
                        String bcontent = etContent.getText().toString();
                        String bstock = etStock.getText().toString();

                        if (TextUtils.isEmpty(bname)) {
                            etName.requestFocus();
                            etName.setError("Bạn phải nhập tên sản phẩm!");
                        } else if (TextUtils.isEmpty(bprice)) {
                            etPrice.requestFocus();
                            etPrice.setError("Bạn phải nhập giá!");
                        } else if (TextUtils.isEmpty(bcontent)) {
                            etContent.requestFocus();
                            etContent.setError("Bạn phải nhập nội dung!");
                        } else if (TextUtils.isEmpty(bstock)) {
                            etStock.requestFocus();
                            etStock.setError("Bạn phải nhập số lượng!");
                        }
                        else{
                            String name = etName.getText().toString();
                            double price = Double.parseDouble(etPrice.getText().toString());
                            String content = etContent.getText().toString();
                            int stock = Integer.parseInt(etStock.getText().toString());
                            Map<String, Object> selectedCategory = dataSpinnerCategory.get(spinnerCategory.getSelectedItemPosition());
                            int categoryId = Integer.parseInt(selectedCategory.get("id").toString());
                            Map<String, Object> selectedBrand = dataSpinnerBrand.get(spinnerBrand.getSelectedItemPosition());
                            int brandId = Integer.parseInt(selectedBrand.get("id").toString());

                            Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                            byte[] byteivImage = getBitmapAsByteArray(bitmap);
                            List<Product> ll = dbHelper.getProducts();
                            Product newProduct = new Product(product.getId(),name,price,content,stock,byteivImage,categoryId,brandId);
                            boolean updateSuccessful = dbHelper.updateProduct(newProduct);

                            if(updateSuccessful){
                                Toast.makeText(context, "Sản phẩm đã được cập nhật.", Toast.LENGTH_SHORT).show();
                                loadData();
                            }else{
                                Toast.makeText(context, "Không thể cập nhật sản phẩm.", Toast.LENGTH_SHORT).show();
                            }
                            dialog.cancel();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void loadCategory_Spinner(Spinner spinner){
        dataSpinnerCategory = new ArrayList<>();
        for (Category cate : dbHelper.getCategories())
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", cate.getId());
            item.put("name", cate.getName());
            dataSpinnerCategory.add(item);
        }
        SimpleAdapter arrayAdapter = new SimpleAdapter(ProductListActivity.this, dataSpinnerCategory,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"name"}, new int[]{android.R.id.text1});
        spinner.setAdapter(arrayAdapter);
    }

    private void loadBrand_Spinner(Spinner spinner){
        dataSpinnerBrand = new ArrayList<>();
        for (Brand brand : dbHelper.getBrands())
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", brand.getId());
            item.put("name", brand.getName());
            dataSpinnerBrand.add(item);
        }
        SimpleAdapter arrayAdapter = new SimpleAdapter(ProductListActivity.this, dataSpinnerBrand,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"name"}, new int[]{android.R.id.text1});
        spinner.setAdapter(arrayAdapter);
    }

    private void loadData()
    {
        List<Product> lProduct = dbHelper.getProducts();
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ProductListAdapter(this, lProduct));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id)
            {
                long productId = parent.getItemIdAtPosition(position);
                showDiaglogUpdateDelete(view, (int) productId);
            }
        });
    }


    private void pickImageFromGarelly() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSTION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGarelly();
                }
                else{
                    Toast.makeText(this, "Permission denide...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data!=null) {
            Uri ImageUrl = data.getData();
            CropImage.activity(ImageUrl)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                mImageView.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}