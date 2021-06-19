package hcmute.edu.vn.mssv18110328.customer_area;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.Bill;
import hcmute.edu.vn.mssv18110328.models.BillDetail;
import hcmute.edu.vn.mssv18110328.models.Brand;
import hcmute.edu.vn.mssv18110328.models.Product;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.FormatPrice;
import static hcmute.edu.vn.mssv18110328.utils.Utility.convertCompressedByteArrayToBitmap;

public class ProductActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;
    Product product;
    Brand brand;
    SimpleDateFormat formatterDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        formatterDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.add);

        int id = Integer.parseInt(getIntent().getStringExtra("current_product_id"));
        product = dbHelper.getProduct(id);
        brand = dbHelper.getBrand(product.getBrandId());

        ImageView ivImage = (ImageView) this.findViewById(R.id.ivImage);
        TextView tvName =  findViewById(R.id.tvName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvContent = findViewById(R.id.tvContent);
        TextView tvBrand = findViewById(R.id.tvBrand);

        ivImage.setImageBitmap(convertCompressedByteArrayToBitmap(product.getImage()));
        tvName.setText(product.getName());
        tvContent.setText(product.getContent());
        tvPrice.setText(FormatPrice(product.getPrice()));
        tvBrand.setText(brand.getName());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.back:
                        finish();
                    case R.id.add:
                        int curQuantityInCart = 0;
                        int curUserId = Integer.parseInt(SharedPrefs.getInstance().get(CURRENT_ID, String.class));
                        Bill curBill = dbHelper.getIncompleteBillByUserId(curUserId); //Lấy giỏ hàng
                        if (curBill!=null)
                        {
                            BillDetail billDetail = dbHelper.getBillDetailByBillIdProductId(curBill.getId(), product.getId());
                            if (billDetail != null)
                            {
                                curQuantityInCart = billDetail.getQuantity();
                            }
                        }
                        if(curQuantityInCart<10)
                        {
                            showDiaglogAddInCart(curQuantityInCart);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Giỏ hàng chỉ có thể chứa tối đa 10 sản phẩm này!", Toast.LENGTH_SHORT).show();
                        }
                }
                return false;
            }
        });
    }

    public void showDiaglogAddInCart(int curQuantityInCart) {
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
                if(Integer.parseInt(tvQuantity.getText().toString()) > 1) {
                    tvQuantity.setText( String.valueOf(Integer.parseInt(tvQuantity.getText().toString()) - 1) );
                    tvPrice.setText(FormatPrice(product.getPrice()* Integer.parseInt(tvQuantity.getText().toString())));
                }
            }
        });
        btnPlus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(Integer.parseInt(tvQuantity.getText().toString()) < (10 - curQuantityInCart)) {
                    tvQuantity.setText( String.valueOf(Integer.parseInt(tvQuantity.getText().toString()) + 1) );
                    tvPrice.setText(FormatPrice(product.getPrice()* Integer.parseInt(tvQuantity.getText().toString())));
                }
                else
                    Toast.makeText(getApplicationContext(), "Giỏ hàng chỉ có thể chứa tối đa 10 sản phẩm này!", Toast.LENGTH_SHORT).show();
            }
        });

        new AlertDialog.Builder(this)
                .setView(formElementsView)
                .setTitle("Thêm vào giỏ hàng")
                .setPositiveButton("Thêm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Date date = new Date(System.currentTimeMillis());
                                String dateBill = formatterDate.format(date);

                                int curUserId = Integer.parseInt(SharedPrefs.getInstance().get(CURRENT_ID, String.class));
                                if (!dbHelper.isUserHasCart(curUserId)){//User đã có giỏ hàng chưa
                                    if (dbHelper.addBill(new Bill("", 0, curUserId, "incomplete", "" , dateBill))  )
                                        Toast.makeText(getApplicationContext(), "Bạn vừa tạo đơn hàng mới.", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(getApplicationContext(), "Thêm đơn hàng bị lỗi.", Toast.LENGTH_SHORT).show();
                                }
                                Bill curBill = dbHelper.getIncompleteBillByUserId(curUserId); //Lấy giỏ hàng
                                if (dbHelper.isBillHasProduct(curBill.getId(), product.getId()))
                                {
                                    BillDetail billDetail = dbHelper.getBillDetailByBillIdProductId(curBill.getId(), product.getId());
                                    billDetail.setQuantity(billDetail.getQuantity() + Integer.parseInt(tvQuantity.getText().toString()));
                                    billDetail.setPrice(product.getPrice()*billDetail.getQuantity());
                                    if (dbHelper.updateBillDetail(billDetail))
                                        Toast.makeText(getApplicationContext(), "Bạn vừa cập nhật đơn hàng.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    BillDetail billDetail = new BillDetail(product.getId(),
                                            Integer.parseInt(tvQuantity.getText().toString()),
                                            Integer.parseInt(tvQuantity.getText().toString())*product.getPrice(),
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