package hcmute.edu.vn.mssv18110328.customer_area;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.Bill;
import hcmute.edu.vn.mssv18110328.models.BillDetail;
import hcmute.edu.vn.mssv18110328.models.Category;
import hcmute.edu.vn.mssv18110328.models.Product;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_NAME;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_PRODUCT_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.FormatPrice;
import static hcmute.edu.vn.mssv18110328.utils.Utility.INCOMPLETE_ORDER_STATUS;
import static hcmute.edu.vn.mssv18110328.utils.Utility.convertCompressedByteArrayToBitmap;

public class ProductActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.add);

        int id = Integer.parseInt(getIntent().getStringExtra(CURRENT_PRODUCT_ID));
        product = dbHelper.getProduct(id);

        ImageView ivImage = (ImageView) this.findViewById(R.id.ivImage);
        TextView tvName =  findViewById(R.id.tvName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvContent = findViewById(R.id.tvContent);

        ivImage.setImageBitmap(convertCompressedByteArrayToBitmap(product.getImage()));
        tvName.setText(product.getName());
        tvContent.setText(product.getContent());
        tvPrice.setText(FormatPrice(product.getPrice()));

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.back:
                        return true;
                    case R.id.add:
                        showDiaglogAdd();
                }
                return false;
            }
        });
    }

    public void showDiaglogAdd() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.cart_input_form, null, false);

        TextView tvName =  (TextView) formElementsView.findViewById(R.id.tvName);
        TextView tvPrice = (TextView) formElementsView.findViewById(R.id.tvPrice);
        TextView tvQuantity = (TextView) formElementsView.findViewById(R.id.tvQuantity);
        ImageView ivImage =  (ImageView) formElementsView.findViewById(R.id.ivImage);
        Button btnMinus = (Button) formElementsView.findViewById(R.id. btnMinus);
        Button btnPlus = (Button) formElementsView.findViewById(R.id. btnPlus);

        tvName.setText(product.getName());
        tvPrice.setText(FormatPrice(product.getPrice()));
        ivImage.setImageBitmap(convertCompressedByteArrayToBitmap(product.getImage()));

        btnMinus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(Integer.parseInt(tvQuantity.getText().toString()) == 1) {
                    tvPrice.setText(FormatPrice(product.getPrice()));
                }
                else{
                    tvQuantity.setText( String.valueOf(Integer.parseInt(tvQuantity.getText().toString()) - 1) );
                    tvPrice.setText(FormatPrice(product.getPrice()* Integer.parseInt(tvQuantity.getText().toString())));
                }
            }
        });
        btnPlus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(Integer.parseInt(tvQuantity.getText().toString()) == 10) {
                    tvPrice.setText(FormatPrice(product.getPrice() * 10));
                }
                else{
                    tvQuantity.setText( String.valueOf(Integer.parseInt(tvQuantity.getText().toString()) + 1) );
                    tvPrice.setText(FormatPrice(product.getPrice()* Integer.parseInt(tvQuantity.getText().toString())));
                }
            }
        });

        new AlertDialog.Builder(this)
                .setView(formElementsView)
                .setTitle("Thêm vào giỏ hàng")
                .setPositiveButton("Thêm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int curUserId = Integer.parseInt(SharedPrefs.getInstance().get(CURRENT_ID, String.class));
                                Product curProduct = dbHelper.getProduct(Integer.parseInt(getIntent().getStringExtra(CURRENT_PRODUCT_ID)));
                                if (!dbHelper.isUserHasCart(curUserId)){//User đã có giỏ hàng chưa
                                    if (dbHelper.addBill(new Bill("", 0, curUserId, INCOMPLETE_ORDER_STATUS, "" )))
                                        Toast.makeText(getApplicationContext(), "Bạn vừa tạo đơn hàng mới.", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(getApplicationContext(), "Thêm đơn hàng bị lỗi.", Toast.LENGTH_SHORT).show();
                                }
                                Bill curBill = dbHelper.getIncompleteBillByUserId(curUserId); //Lấy giỏ hàng
                                if (dbHelper.isBillHasProduct(curBill.getId(), curProduct.getId()))
                                {
                                    BillDetail billDetail = dbHelper.getBillDetailByBillIdProductId(curBill.getId(), curProduct.getId());
                                    billDetail.setQuantity(billDetail.getQuantity() + Integer.parseInt(tvQuantity.getText().toString()));
                                    billDetail.setPrice(curProduct.getPrice()*billDetail.getQuantity());
                                    if (dbHelper.updateBillDetail(billDetail))
                                        Toast.makeText(getApplicationContext(), "Bạn vừa cập nhật đơn hàng.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    BillDetail billDetail = new BillDetail(curProduct.getId(),
                                            Integer.parseInt(tvQuantity.getText().toString()),
                                            Integer.parseInt(tvQuantity.getText().toString())*curProduct.getPrice(),
                                            curBill.getId());
                                    if(dbHelper.addBillDetail(billDetail)){
                                        Toast.makeText(getApplicationContext(), "Bạn vừa thêm vào đơn hàng.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                //------------CẬP NHẬT LẠI TỔNG GIÁ TRỊ ĐƠN HÀNG-----------
                                double totalPrice = 0;
                                List<BillDetail> lBillDetail = dbHelper.getBillDetailByBillId(curBill.getId());
                                for ( BillDetail object : lBillDetail) {
                                    totalPrice = totalPrice + object.getPrice();
                                }
                                curBill.setTotalPrice(totalPrice);
                                dbHelper.updateBill(curBill);
                                //-----------KẾT THÚC CẬP NHẬT TỔNG GIÁ TRỊ ĐƠN HÀNG--------
                                dialog.cancel();
                            }
                        }).show();
    }
}